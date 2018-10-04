module db.db_concept;
import std.format, std.string, std.conv;
import derelict.pq.pq;
import db.db_main;

//---***---***---***---***---***--- data ---***---***---***---***---***--

//---***---***---***---***---***--- types ---***---***---***---***---***---***

/// All we need to control the concepts table.
struct TableConcepts {

    /// Name of the concept table
    enum tableName = "concepts";

    /// Table fields
    enum {
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
        getConcept_stmt = "getConcept_stmt",    // get concept by cid+ver
        setConcept_stmt = "setConcept_stmt",    // update concept with given cid+ver
        addConcept_stmt = "addConcept_stmt",    // insert into table new concept
        delConcept_stmt = "delConcept_stmt",    // remove from the table concept with given cid+ver
        findConceptVersions_stmt = "findConceptVersions_stmt",  // find all versions of a concept with given cid
    }

    /**
            Insert a concept into the table.
        Parameters:
            cid = cid
            ver = version of the concept
            shallow = byte array of the shallow copy of the concept object
            deep = byte array of the serialized fields, which are referencies and must be deep copied.
    */
//    void addConcept(uint cid, ushort ver, byte[] shallow, byte[] deep) {
    void addConcept(uint cid) {
        PGresult* res;

//        char** paramValues = [cast(char*)&cid, cast(char*)&ver, cast(char*)shallow.ptr, cast(char*)deep.ptr].ptr;
        char** paramValues = [cast(char*)&cid].ptr;
        res = PQexecPrepared(
            conn_,
            addConcept_stmt,
//            4,      // nParams
            1,      // nParams
            paramValues,
            //null,
            [4].ptr,
            //null,
            [1].ptr,
            0       // result as a string
        );
        assert(PQresultStatus(res) == PGRES_COMMAND_OK, to!string(PQerrorMessage(conn_)));
        PQclear(res);
    }

    /// Prepare all statements, whose names a present in the enum
    void prepare() {
        PGresult* res;

        res = PQprepare(
            conn_,
            addConcept_stmt,
//            format!"insert into %s (%s, %s, %s, %s) values($1::int4, $2::int2, $3, $4)"(tableName, cid, ver, shallow, deep).toStringz,
            "insert into cpts (cid) values($1::int)".toStringz,
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

//    tc.addConcept(1, 10, cast(byte[])[1,2,3,4,5], cast(byte[])[6,7,8,9,10,11]);
    tc.addConcept(1);

    disconnectFromDb(conn);
}

////
//                               Private
//

//---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
