module db.db_main;
import std.exception;
import std.string, std.conv, std.format;
version(unittest) import std.stdio;

import derelict.pq.pq;

import global_types;

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

//
//    PGconn * connection = connectToDb();
//
//    PGresult  *       result;
//    result = PQexec(connection, `select * from concepts limit 3`);
//
//    PQprintOpt        options;
//    options.header    = 0;              /* Ask for column headers            */
//    options.aligment     = 1;           /* Pad short columns for alignment   */
//    options.html3 = 0;
//    options.fieldSep  = cast(char*)"|"; /* Use a pipe as the field separator */
//
//    {
//        import core.stdc.stdio: stdout;
//        PQprint(stdout, result, &options);
//    }
//
//    disconnectFromDb(connection);
}

//---***---***---***---***---***--- functions ---***---***---***---***---***--

/// Connect to the database, set up the connectionPtr_ field.
void connectToDb() {

    string s = format!`user='%s' password='%s' dbname='%s'`(
        cast(string)DbCreds.user,
        cast(string)DbCreds.password,
        cast(string)DbCreds.dbname
    );
    conn_ = PQconnectdb(s.toStringz);
    if(PQstatus(conn_) != CONNECTION_OK)
            enforce(false, to!string(PQerrorMessage(conn_)));
}

/// Finish work with the data base.
void disconnectFromDb() {
    PQfinish(conn_);
}

/// Group functions to work with the params table.
struct TableParams {

    /// Name of the params table
    enum paramsTable = "params";

    /// Prepared statement's names
    enum {
        getParam_stmt = "getParam_stmt",
        setParam_stmt = "setParam_stmt"
    }

    ///**
    //        Get parameter by name. The record in database must exist, else an assertion is thrown.
    //    Parameters:
    //        name = name of the parameter
    //    Returns: value of the parameter as a string, null is possible since the field can be null.
    //*/
    //static string getParam(string name) {
    //    PGresult* res;
    //    scope(exit) PQclear(res);
    //    res = PQexec(conn_, format!"select value from params where name = '%s'"(name).toStringz);
    //    assert(PQresultStatus(res) == PGRES_TUPLES_OK, to!string(PQerrorMessage(conn_)));
    //    assert(PQntuples(res) == 1, format!"Found %s records for parameter: %s"(PQntuples(res), name));
    //
    //    return to!string(cast(char*)PQgetvalue(res, 0, 0));
    //}

    /**
            Get parameter by name. The record in database must exist, else an assertion is thrown.
        Parameters:
            name = name of the parameter
        Returns: value of the parameter as a string, null is possible since the field can be null.
    */
    static string getParam(string name) {
        PGresult* res;
        scope(exit) PQclear(res);
        char* pc = cast(char*)name.toStringz;
        char** paramValues = &pc;
        res = PQexecPrepared(
            conn_,
            getParam_stmt,
            1,      // nParams
            paramValues,
            null,
            null,
            0
        );
        assert(PQresultStatus(res) == PGRES_TUPLES_OK, to!string(PQerrorMessage(conn_)));
        assert(PQntuples(res) == 1, format!"Found %s records for parameter: %s"(PQntuples(res), name));

        return to!string(cast(char*)PQgetvalue(res, 0, 0));
    }

    /// Prepare all statements, whose names a present in the enum
    static void prepare() {
        PGresult* res;

        res = PQprepare(
            conn_,
            getParam_stmt,
            format!"select value from %s where name=$1"(paramsTable).toStringz,
            0,
            null
        );
        assert(PQresultStatus(res) == PGRES_COMMAND_OK, to!string(PQerrorMessage(conn_)));
        PQclear(res);

        res = PQprepare(
            conn_,
            setParam_stmt,
            format!"update %s set value=$1 where name=$2"(paramsTable).toStringz,
            0,
            null
        );
        assert(PQresultStatus(res) == PGRES_COMMAND_OK, to!string(PQerrorMessage(conn_)));
        PQclear(res);
    }
}

//---***---***---***---***---***--- types ---***---***---***---***---***---***

/**
        Concept version control struct. BR
    It contains a raw version field, which is the part of each concept. Zero value of that field is quite legal and it
    means that the concept is of the _min_ver_ version, the oldest valid version that cannot be removed yet.
*/
shared synchronized class ConceptVersion {

    /// The newest availabale version to use. This is the latest version commited by the tutor. If the _cur_ver_ rolled over the
    /// Cvr.max and became the lesser number than all other versions, it stil must not reach the _stale_ver_, or an assertion
    /// exception would be thrown.
    private static Cvr _cur_ver_;

    /// Minimal currently used version. If the raw version is equal to 0, it means this version. All versions older than that
    /// may be removed.
    private static Cvr _min_ver_;

    /// Minimum of the versions, which are less than _min_ver_ and so should be removed.
    private static Cvr _stale_ver_;
}

unittest {
    shared ConceptVersion cv = new shared ConceptVersion;
    connectToDb;

    //TableParams.getParam("_cur_ver_");
    TableParams.prepare;
writeln(TableParams.getParam("_cur_ver_"));
    disconnectFromDb;
}

//===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
//
//                                  Private
//
//===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@

//---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

/// Connection pointer
private PGconn* conn_;

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
