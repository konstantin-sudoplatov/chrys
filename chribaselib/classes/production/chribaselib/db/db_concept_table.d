module db.db_concept_table;
import std.exception;
import std.format, std.string, std.conv;
import derelict.pq.pq;
import db.db_main;

import global_data, tools;

//---***---***---***---***---***--- data ---***---***---***---***---***--

//---***---***---***---***---***--- types ---***---***---***---***---***---***

/// All we need to control the concepts table.
struct TableConcepts {

    /// Name of the concept table
    enum tableName = "concepts";

    /// Table fields
    enum Fld {
        cid = "cid",
        ver = "ver",        // version of the concept
        shallow = "shallow",// shallow copy of the concept
        deep = "deep"       // deep copy fields
    }

    @disable this();
    this(PGconn* conn) {
        conn_ = conn;
    }

    /// Prepared statement's names
    enum {
        addConcept_stmt = "addConcept_stmt",            // insert into table new concept
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
            shallow = byte array of the shallow copy of the concept object
            deep = byte array of the serialized fields, which are referencies and must be deep copied.
        Throws: enforce, for a duplicate key, for example.
    */
    void addConcept(Cid cid, Cvr ver, byte[] shallow, byte[] deep) {
        PGresult* res;

        Cid c = invertEndianess(cid);
        Cvr v = invertEndianess(ver);
        char** paramValues = [
            cast(char*)&c,
            cast(char*)&v,
            cast(char*)shallow.ptr,
            cast(char*)deep.ptr
        ].ptr;
        res = PQexecPrepared(
            conn_,
            addConcept_stmt,
            4,      // nParams
            paramValues,
            (cast(int[])[Cid.sizeof, Cvr.sizeof, shallow.length, deep.length]).ptr,
            (cast(int[])[1, 1, 1, 1]).ptr,
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
    void deleteConcept(Cid cid, Cvr ver) {
        PGresult* res;

        Cid c = invertEndianess(cid);
        Cvr v = invertEndianess(ver);
        char** paramValues = [
            cast(char*)&c,
            cast(char*)&v
        ].ptr;
        res = PQexecPrepared(
            conn_,
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
            Get a concept's shallow and deep parts from the table.
        Parameters:
            cid = cid
            ver = version of the concept
        Returns: (shallow, deep) byte arrays as voldemort type. If there is no such concept (null, null) will be returned.
    */
    auto getConcept(Cid cid, Cvr ver) {
        PGresult* res;

        Cid c = invertEndianess(cid);
        Cvr v = invertEndianess(ver);
        char** paramValues = [
            cast(char*)&c,
            cast(char*)&v
        ].ptr;

        res = PQexecPrepared(
            conn_,
            getConcept_stmt,
            2,      // nParams
            paramValues,
            (cast(int[])[Cid.sizeof, Cvr.sizeof]).ptr,
            (cast(int[])[1, 1]).ptr,
            1       // binary result
        );
        enforce(PQresultStatus(res) == PGRES_TUPLES_OK, to!string(PQerrorMessage(conn_)));
        scope(exit) PQclear(res);

        struct Result {
            byte[] shallow;
            byte[] deep;
        }
        Result cs;   // result
        if      // is there such concept?
                (PQntuples(res) != 0)
        {
            // get the shallow
            int len = PQgetlength(res, 0, 0);
            cs.shallow.length = len;
            cs.shallow[0..len] = (cast(byte*)PQgetvalue(res, 0, 0))[0..len];

            // get the deep
            len = PQgetlength(res, 0, 1);
            if(len) {
                cs.deep.length = len;
                cs.deep[0..len] = (cast(byte*)PQgetvalue(res, 0, 1))[0..len];
            }
        }

        return cs;
    }

    /**
            Update shallow and deep parts of a concept in the table.
        Parameters:
            cid = cid
            ver = version of the concept
            shallow = byte array of the shallow copy of the concept object
            deep = byte array of the serialized fields, which are referencies and must be deep copied.
        Throws: enforce, if there is no record to update, for example.
    */
    void updateConcept(Cid cid, Cvr ver, byte[] shallow, byte[] deep) {
        PGresult* res;

        Cid c = invertEndianess(cid);
        Cvr v = invertEndianess(ver);
        char** paramValues = [
            cast(char*)&c,
            cast(char*)&v,
            cast(char*)shallow.ptr,
            cast(char*)deep.ptr
        ].ptr;
        res = PQexecPrepared(
            conn_,
            updateConcept_stmt,
            4,      // nParams
            paramValues,
            (cast(int[])[Cid.sizeof, Cvr.sizeof, shallow.length, deep.length]).ptr,
            (cast(int[])[1, 1, 1, 1]).ptr,
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
    Cvr[] findConceptVersions(Cid cid) {
        PGresult* res;

        Cid c = invertEndianess(cid);
        char** paramValues = [
            cast(char*)&c,
        ].ptr;
        res = PQexecPrepared(
            conn_,
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
    void prepare() {
        PGresult* res;

        // Add concept
        res = PQprepare(
            conn_,
            addConcept_stmt,
            format!"insert into %s (%s, %s, %s, %s) values($1, $2, $3, $4)"
                    (tableName, Fld.cid, Fld.ver, Fld.shallow, Fld.deep).toStringz,
            0,
            null
        );
        assert(PQresultStatus(res) == PGRES_COMMAND_OK, to!string(PQerrorMessage(conn_)));
        PQclear(res);

        // Delete
        res = PQprepare(
            conn_,
            deleteConcept_stmt,
            format!"delete from %s where %s=$1 and %s=$2"(tableName, Fld.cid, Fld.ver).toStringz,
            0,
            null
        );
        assert(PQresultStatus(res) == PGRES_COMMAND_OK, to!string(PQerrorMessage(conn_)));
        PQclear(res);

        // Get
        res = PQprepare(
            conn_,
            getConcept_stmt,
            format!"select %s, %s from %s where %s=$1 and %s=$2"
                    (Fld.shallow, Fld.deep, tableName, Fld.cid, Fld.ver).toStringz,
            0,
            null
        );
        assert(PQresultStatus(res) == PGRES_COMMAND_OK, to!string(PQerrorMessage(conn_)));
        PQclear(res);

        // Update
        res = PQprepare(
            conn_,
            updateConcept_stmt,
            format!"update %s set %s=$3, %s=$4 where %s=$1 and %s=$2"
                    (tableName, Fld.shallow, Fld.deep, Fld.cid, Fld.ver).toStringz,
            0,
            null
        );
        assert(PQresultStatus(res) == PGRES_COMMAND_OK, to!string(PQerrorMessage(conn_)));
        PQclear(res);

        // Find versions
        res = PQprepare(
            conn_,
            findConceptVersions_stmt,
            format!"select %s from %s where %s=$1 order by %s"(Fld.ver, tableName, Fld.cid, Fld.ver).toStringz,
            0,
            null
        );
        assert(PQresultStatus(res) == PGRES_COMMAND_OK, to!string(PQerrorMessage(conn_)));
        PQclear(res);
    }

    private PGconn* conn_;      /// Connection
}

unittest {
    PGconn* conn = connectToDb;
    auto tc = TableConcepts(conn);
    tc.prepare;

    // Delete to clean up possible remnants
    tc.deleteConcept(0, 10);
    tc.deleteConcept(0, 20);

    // Add test concept
    tc.addConcept(0, 10, cast(byte[])[1,2,3,4,5], cast(byte[])[6,7,8,9,10,11]);
//    tc.addConcept(0, 10, cast(byte[])[1,2,3,4,5], cast(byte[])[6,7,8,9,10,11]);   // throws an exception
    tc.addConcept(0, 20, cast(byte[])[1,2,3,4,5], null);

    // Get
    assert(to!string(tc.getConcept(0, 10).shallow) == "[1, 2, 3, 4, 5]"
            && to!string(tc.getConcept(0, 10).deep) == "[6, 7, 8, 9, 10, 11]");
    assert(to!string(tc.getConcept(0, 20).shallow) == "[1, 2, 3, 4, 5]" && tc.getConcept(0, 20).deep is null);

    // Update
    tc.updateConcept(0, 10, cast(byte[])[1,2,3], null);
    assert(to!string(tc.getConcept(0, 10).shallow) == "[1, 2, 3]" && tc.getConcept(0, 10).deep is null);
    tc.updateConcept(0, 20, cast(byte[])[1,2,3], cast(byte[])[10,11]);
    assert(to!string(tc.getConcept(0, 20).shallow) == "[1, 2, 3]" && to!string(tc.getConcept(0, 20).deep) =="[10, 11]");
//    tc.updateConcept(0, 30, cast(byte[])[1,2,3], cast(byte[])[10,11]);    // throws exception, because there is no such concept

    // Find versions
    assert(to!string(tc.findConceptVersions(0)) == "[10, 20]");

    // Clean up
    tc.deleteConcept(0, 10);
    tc.deleteConcept(0, 20);
    assert(tc.findConceptVersions(0) is null);

    disconnectFromDb(conn);
}

////
//                               Private
//

//---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
