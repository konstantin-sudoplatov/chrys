module db.db_concepts_table;
import std.exception;
import std.format, std.string, std.conv;
import derelict.pq.pq;
import db.db_main;

import project_params, tools;

//---***---***---***---***---***--- data ---***---***---***---***---***--

//---***---***---***---***---***--- types ---***---***---***---***---***---***

/// All we need to control the concepts table.
struct ConceptsTable {

    /// Name of the concept table
    enum tableName = "concepts";

    /// Table fields
    enum Fld {
        cid = "cid",        // concept identifier
        ver = "ver",        // version of the concept
        clid = "clid",
        stable = "stable", // data responsible for logic and behavior
        transient = "transient"  // changeable data like usage statistics
    }

    @disable this();
    this(PGconn* conn) {
        assert(conn);
        conn_ = conn;
        prepare;
    }

    /// Prepared statement's names
    enum {
        insertConcept_stmt = "addConcept_stmt",         // insert into table new concept
        deleteConcept_stmt = "deleteConcept_stmt",      // remove from the table concept with given cid+ver
        getConcept_stmt = "getConcept_stmt",            // get concept by cid+ver
        updateConcept_stmt = "updateConcept_stmt",      // update concept with given cid+ver
        findConceptVersions_stmt = "findConceptVersions_stmt",  // find all versions of a concept with given cid
    }

    /**
            Insert a concept into the table.
        Parameters:
            cid = cid
            ver = version of the concept
            clid = class identifier
            stable = byte array of the stable data (like effects, premises and so on)
            transient = byte array of the transient data (like usage statistics)
        Throws: enforce, for a duplicate key, for example.
    */
    void insertConcept(Cid cid, Cvr ver, Clid clid, const byte[] stable, const byte[] transient) const {
        PGresult* res;

        Cid c = invertEndianess(cid);
        Cvr v = invertEndianess(ver);
        Clid cl = invertEndianess(clid);
        char** paramValues = [
            cast(char*)&c,
            cast(char*)&v,
            cast(char*)&cl,
            cast(char*)stable.ptr,
            cast(char*)transient.ptr
        ].ptr;
        res = PQexecPrepared(
            cast(PGconn*)conn_,
            insertConcept_stmt,
            5,      // nParams
            paramValues,
            (cast(int[])[Cid.sizeof, Cvr.sizeof, Clid.sizeof, stable.length, transient.length]).ptr,
            (cast(int[])[1, 1, 1, 1, 1]).ptr,
            0       // result as a string
        );
        enforce(PQresultStatus(res) == PGRES_COMMAND_OK, to!string(PQerrorMessage(conn_)));
        PQclear(res);
    }

    /**
            Delete a concept from the table. If there is no such a concept, this function returns silently.
        Parameters:
            cid = cid
            ver = version of the concept
    */
    void deleteConcept(Cid cid, Cvr ver) const {
        PGresult* res;

        Cid c = invertEndianess(cid);
        Cvr v = invertEndianess(ver);
        char** paramValues = [
            cast(char*)&c,
            cast(char*)&v
        ].ptr;
        res = PQexecPrepared(
            cast(PGconn*)conn_,
            deleteConcept_stmt,
            2,      // nParams
            paramValues,
            (cast(int[])[Cid.sizeof, Cvr.sizeof]).ptr,
            (cast(int[])[1, 1]).ptr,
            0       // result as a string
        );
        enforce(PQresultStatus(res) == PGRES_COMMAND_OK, to!string(PQerrorMessage(conn_)));
        PQclear(res);
    }

    /**
            Get a concept's clid, shallow and deep parts from the table.
        Parameters:
            cid = cid
            ver = version of the concept
        Returns: pointer to struct {clid, stable, transient} as (Clid, byte[], byte[]). If there is no concept found, null
            will be returned.
    */
    auto getConcept(Cid cid, Cvr ver) const {

        struct Result {
            Clid clid = Clid.max;
            byte[] stable;
            byte[] transient;
        }
        PGresult* res;

        Cid c = invertEndianess(cid);
        Cvr v = invertEndianess(ver);
        char** paramValues = [
            cast(char*)&c,
            cast(char*)&v
        ].ptr;

        res = PQexecPrepared(
            cast(PGconn*)conn_,
            getConcept_stmt,
            2,      // nParams
            paramValues,
            (cast(int[])[Cid.sizeof, Cvr.sizeof]).ptr,
            (cast(int[])[1, 1]).ptr,
            1       // binary result
        );
        enforce(PQresultStatus(res) == PGRES_TUPLES_OK, to!string(PQerrorMessage(conn_)));
        scope(exit) PQclear(res);
        if      // does the concept exist?
                (PQntuples(res) != 0)
        {
            Result* rs = new Result;   // result

            // get the clid
            rs.clid = invertEndianess(*cast(Clid*)PQgetvalue(res, 0, 0));

            // get the shallow
            int len = PQgetlength(res, 0, 1);
            rs.stable.length = len;
            rs.stable[0..len] = (cast(byte*)PQgetvalue(res, 0, 1))[0..len];

            // get the deep
            len = PQgetlength(res, 0, 2);
            if(len) {
                rs.transient.length = len;
                rs.transient[0..len] = (cast(byte*)PQgetvalue(res, 0, 2))[0..len];
            }

            return rs;
        }
        else
            return null;
    }

    /**
            Update shallow and deep parts of a concept in the table. There is no optimization for the case if we need only
        partial update, i.e. if we need to update only clid we still update the shallow and deep fields. Such optimization
        is, of course, possible.
        Parameters:
            cid = cid
            ver = version of the concept
            clid = class identifier
            stable = byte array of the stable data (like effects, premises and so on)
            transient = byte array of the transient data (like usage statistics)
        Throws: enforce, if there is no record to update, for example.
    */
    void updateConcept(Cid cid, Cvr ver, Clid clid, const byte[] stable, const byte[] transient) const {
        PGresult* res;

        Cid c = invertEndianess(cid);
        Cvr v = invertEndianess(ver);
        Clid cl = invertEndianess(clid);
        char** paramValues = [
            cast(char*)&c,
            cast(char*)&v,
            cast(char*)&cl,
            cast(char*)stable.ptr,
            cast(char*)transient.ptr
        ].ptr;
        res = PQexecPrepared(
            cast(PGconn*)conn_,
            updateConcept_stmt,
            5,      // nParams
            paramValues,
            (cast(int[])[Cid.sizeof, Cvr.sizeof, Clid.sizeof, stable.length, transient.length]).ptr,
            (cast(int[])[1, 1, 1, 1, 1]).ptr,
            0       // result as a string
        );
        enforce(PQresultStatus(res) == PGRES_COMMAND_OK, to!string(PQerrorMessage(conn_)));
        string sTuplesAffected = to!string(PQcmdTuples(res));
        enforce(sTuplesAffected == "1", format!"Updated %s records for cid: %s, ver: %s"(sTuplesAffected, cid, ver));
        PQclear(res);
    }

    /**
            Find all versions of a given concept.
        Parameters:
            cid = cid
        Returns: array of versions. If no versions, null is returned.
    */
    Cvr[] findConceptVersions(Cid cid) const {
        PGresult* res;

        Cid c = invertEndianess(cid);
        char** paramValues = [
            cast(char*)&c,
        ].ptr;
        res = PQexecPrepared(
            cast(PGconn*)conn_,
            findConceptVersions_stmt,
            1,      // nParams
            paramValues,
            (cast(int[])[Cid.sizeof]).ptr,
            (cast(int[])[1]).ptr,
            1       // binary result
        );
        enforce(PQresultStatus(res) == PGRES_TUPLES_OK, to!string(PQerrorMessage(conn_)));
        scope(exit) PQclear(res);

        Cvr[] vers;
        foreach(i; 0..PQntuples(res)) {
            Cvr ver = *(cast(Cvr*)PQgetvalue(res, i, 0));
            vers ~= invertEndianess(ver);
        }

        return vers;
    }

    /// Prepare all statements, whose names a present in the enum
    void prepare() const {
        PGresult* res;

        // Add concept
        res = PQprepare(
            cast(PGconn*)conn_,
            insertConcept_stmt,
            format!"insert into %s (%s, %s, %s, %s, %s) values($1, $2, $3, $4, $5)"
                    (tableName, Fld.cid, Fld.ver, Fld.clid, Fld.stable, Fld.transient).toStringz,
            0,
            null
        );
        assert(PQresultStatus(res) == PGRES_COMMAND_OK, to!string(PQerrorMessage(conn_)));
        PQclear(res);

        // Delete
        res = PQprepare(
            cast(PGconn*)conn_,
            deleteConcept_stmt,
            format!"delete from %s where %s=$1 and %s=$2"(tableName, Fld.cid, Fld.ver).toStringz,
            0,
            null
        );
        assert(PQresultStatus(res) == PGRES_COMMAND_OK, to!string(PQerrorMessage(conn_)));
        PQclear(res);

        // Get
        res = PQprepare(
            cast(PGconn*)conn_,
            getConcept_stmt,
            format!"select %s, %s, %s from %s where %s=$1 and %s=$2"
                    (Fld.clid, Fld.stable, Fld.transient, tableName, Fld.cid, Fld.ver).toStringz,
            0,
            null
        );
        assert(PQresultStatus(res) == PGRES_COMMAND_OK, to!string(PQerrorMessage(conn_)));
        PQclear(res);

        // Update
        res = PQprepare(
            cast(PGconn*)conn_,
            updateConcept_stmt,
            format!"update %s set %s=$3, %s=$4, %s=$5 where %s=$1 and %s=$2"
                    (tableName, Fld.clid, Fld.stable, Fld.transient, Fld.cid, Fld.ver).toStringz,
            0,
            null
        );
        assert(PQresultStatus(res) == PGRES_COMMAND_OK, to!string(PQerrorMessage(conn_)));
        PQclear(res);

        // Find versions
        res = PQprepare(
            cast(PGconn*)conn_,
            findConceptVersions_stmt,
            format!"select %s from %s where %s=$1 order by %s"(Fld.ver, tableName, Fld.cid, Fld.ver).toStringz,
            0,
            null
        );
        assert(PQresultStatus(res) == PGRES_COMMAND_OK, to!string(PQerrorMessage(conn_)));
        PQclear(res);
    }

    private PGconn* conn_;      /// Pointer to the connection pointer
}

unittest {
    import std.stdio: writeln, writefln;

    PGconn* conn = connectToDb;
    auto ct = ConceptsTable(conn);

    // Delete to clean up possible remnants
    ct.deleteConcept(0, 10);
    ct.deleteConcept(0, 20);

    // Add test concept
    ct.insertConcept(0, 10, 42, cast(byte[])[1,2,3,4,5], cast(byte[])[6,7,8,9,10,11]);
//    tc.insertConcept(0, 10, 42, cast(byte[])[1,2,3,4,5], cast(byte[])[6,7,8,9,10,11]);   // reinserting throws an exception
    ct.insertConcept(0, 20, 42, cast(byte[])[1,2,3,4,5], null);

    // Get
    assert(ct.getConcept(0, 10).clid == 42);
    assert(to!string(ct.getConcept(0, 10).stable) == "[1, 2, 3, 4, 5]"
            && to!string(ct.getConcept(0, 10).transient) == "[6, 7, 8, 9, 10, 11]");
    assert(to!string(ct.getConcept(0, 20).stable) == "[1, 2, 3, 4, 5]" && ct.getConcept(0, 20).transient is null);

    // Update
    ct.updateConcept(0, 10, Clid.max, cast(byte[])[1,2,3], null);
    assert(ct.getConcept(0, 10).clid == Clid.max && to!string(ct.getConcept(0, 10).stable) == "[1, 2, 3]" &&
            ct.getConcept(0, 10).transient is null);
    ct.updateConcept(0, 20, Clid.max, cast(byte[])[1,2,3], cast(byte[])[10,11]);
    assert(ct.getConcept(0, 20).clid == cast(Clid)-1 && to!string(ct.getConcept(0, 20).stable) == "[1, 2, 3]" &&
            to!string(ct.getConcept(0, 20).transient) =="[10, 11]");
//    tc.updateConcept(0, 30, 42, cast(byte[])[1,2,3], cast(byte[])[10,11]);    // throws exception, since there is no such concept

    // Find versions
    assert(to!string(ct.findConceptVersions(0)) == "[10, 20]");

    // Clean up
    ct.deleteConcept(0, 10);
    ct.deleteConcept(0, 20);
    assert(ct.findConceptVersions(0) is null);

    disconnectFromDb(conn);
}

////
//                               Private
//

//---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
