module db.db_main;
import std.exception;
import std.string, std.conv, std.format;
import std.stdio;

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

/// Minimal currently used version. If the raw version is equal to 0, it means this version. All versions older than that
/// may be removed.
shared Cvr _min_ver_;

/// The newest availabale version to use. This is the latest version commited by the tutor.
shared Cvr _cur_ver_;

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
    connectionPtr_ = PQconnectdb(s.toStringz);
    enforce(connectionPtr_, "Strange thing! May be we are out of memory?");
    if (PQstatus(connectionPtr_) != CONNECTION_OK) enforce(false, to!string(PQerrorMessage(connectionPtr_)));
}

/// Finish work with the data base.
void disconnectFromDb() {
    PQfinish(connectionPtr_);
}

//---***---***---***---***---***--- types ---***---***---***---***---***---***

/**
        Concept version control struct. BR
    It contains a raw version field, which is the part of each concept. Zero value of that field is quite legal and it
    means that
*/
struct ConceptVersion {

    /// Underlying version.
    Cvr rawVer;
}

//===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
//
//                                  Private
//
//===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
private:
//---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

/// Connection pointer
private PGconn* connectionPtr_;

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
