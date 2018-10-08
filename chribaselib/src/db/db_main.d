module db.db_main;
import std.exception;
import std.string, std.conv, std.format;
version(unittest) import std.stdio;

import derelict.pq.pq;

import tools;

/// Credentials for the DB. Be carefull not to use named enums in writeln or format functions without implicit cast to
/// string. You'll get strange results! While cast(string)DbCreds.password gives out "chris", to!string(DbCreds.password) will
/// produce "user"!!! D is sometimes weird.
enum DbCreds {
    user = "chris",
    password = "chris",
    dbname = "chris"
}

//---***---***---***---***---***--- data ---***---***---***---***---***--


/**
        Static constructor
*/
shared static this() {
    // Add shared library /usr/lib/x_86-64-linux-gnu/libpq.so
//    DerelictPQ.load();
}

//---***---***---***---***---***--- functions ---***---***---***---***---***--

/// Connect to the database, set up the connectionPtr_ field.
PGconn* connectToDb() {

    string s = format!`user='%s' password='%s' dbname='%s'`(
        cast(string)DbCreds.user,
        cast(string)DbCreds.password,
        cast(string)DbCreds.dbname
    );
    PGconn* conn = PQconnectdb(s.toStringz);
    if(PQstatus(conn) != CONNECTION_OK)
            enforce(false, to!string(PQerrorMessage(conn)));
    return conn;
}

/// Finish work with the data base.
void disconnectFromDb(PGconn* conn) {
    PQfinish(conn);
}

/**
        Convert a numeric (one of int or float types) from native little endianess to the big endian form.
    Parameters:
        arg = a variable to convert
    Returns: bytes in the reverse order as an initial type.
*/
static import std.traits;
T invertEndianess(T)(T arg) if(std.traits.isNumeric!T) {
    union Res {
        byte[T.sizeof] bar;
        T t;
    }
    Res res;
    for(int i, j = T.sizeof-1; i < T.sizeof; ++i, --j)
        res.bar[j] = (cast(byte*)&arg)[i];

    return res.t;
}
///
unittest{
    assert(invertEndianess(0x01020304) == 0x04030201);
}

//---***---***---***---***---***--- types ---***---***---***---***---***---***

/// All we need to control the params table.
struct TableParams {

    @disable this();
    this(PGconn* conn) {
        conn_ = conn;
    }

    /// Name of the params table
    enum tableName = "params";

    /// Table fields
    enum Fld {
        name = "name",      // parameter name
        value = "value",    // parameter value
        description = "description"     // description of the parameter
    }

    /// Prepared statement's names
    enum {
        getParam_stmt = "getParam_stmt",
        setParam_stmt = "setParam_stmt"
    }

    /**
            Get parameter by name. The record in database must exist, else an assertion is thrown.
        Parameters:
            name = name of the parameter
        Returns: value of the parameter as a string, null is possible since the field can be null.
    */
    string getParam(string name) {
        PGresult* res;
        char** paramValues = [cast(char*)name.toStringz].ptr;
        res = PQexecPrepared(
            conn_,
            getParam_stmt,
            1,      // nParams
            paramValues,
            null,
            null,
            0       // result as a string
        );
        enforce(PQresultStatus(res) == PGRES_TUPLES_OK, to!string(PQerrorMessage(conn_)));
        enforce(PQntuples(res) == 1, format!"Found %s records for parameter: %s"(PQntuples(res), name));
        scope(exit) PQclear(res);

        char* pc = cast(char*)PQgetvalue(res, 0, 0);
        if      // not empty string?
                (*pc != 0)
            return to!string(pc);
        else //no: make difference betwee the null and empty string
            if(PQgetisnull(res, 0, 0))
                return null;
            else
                return "";
    }

    /**
            Set parameter's value. Setting the null value is legal. Exactly one record must be updated, else an assertion
        is thrown.
        Parameters:
            name = name of the parameter
            value = value to set, can be null
    */
    void setParam(string name, string value) {
        PGresult* res;

        char* pcValue;
        pcValue = value is null? null: cast(char*)value.toStringz;
        char** paramValues = [cast(char*)name.toStringz, pcValue].ptr;
        res = PQexecPrepared(
            conn_,
            setParam_stmt,
            2,      // nParams
            paramValues,
            null,
            null,
            0       // result as a string
        );
        enforce(PQresultStatus(res) == PGRES_COMMAND_OK, to!string(PQerrorMessage(conn_)));
        string sTuplesAffected = to!string(PQcmdTuples(res));
        enforce(sTuplesAffected == "1", format!"Updated %s records for parameter: %s"(sTuplesAffected, name));
        PQclear(res);
    }

    /// Prepare all statements, whose names a present in the enum
    void prepare() {
        PGresult* res;

        res = PQprepare(
            conn_,
            getParam_stmt,
            format!"select %s from %s where %s=$1"(Fld.value, tableName, Fld.name).toStringz,
            0,
            null
        );
        enforce(PQresultStatus(res) == PGRES_COMMAND_OK, to!string(PQerrorMessage(conn_)));
        PQclear(res);

        res = PQprepare(
            conn_,
            setParam_stmt,
            format!"update %s set %s=$2 where %s=$1"(tableName, Fld.value, Fld.name).toStringz,
            0,
            null
        );
        enforce(PQresultStatus(res) == PGRES_COMMAND_OK, to!string(PQerrorMessage(conn_)));
        PQclear(res);
    }

    private PGconn* conn_;      /// Connection
}

unittest {
    PGconn* conn = connectToDb;
    auto tp = TableParams(conn);

    //TableParams.getParam("_cur_ver_");
    tp.prepare;
    string par = tp.getParam("_stale_ver_");
    tp.setParam("_stale_ver_", par);
    assert(tp.getParam("_stale_ver_") == par);
    disconnectFromDb(conn);
}

//===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
//
//                                  Private
//
//===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@

//---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
