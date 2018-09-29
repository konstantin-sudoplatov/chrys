module db.common;
import std.exception;
import std.string;
import std.conv;
import std.stdio;
import std.format;
import derelict.pq.pq;

/// Credentials for the DB. Be carefull not to use named enums in writeln or format functions without implicit cast to
/// string. You'll get strange results! While cast(string)DbCreds.password gives out "chris", to!string(DbCreds.password) will
/// produce "user"!!! D is sometimes weird.
enum DbCreds {
    user = "chris",
    password = "chris",
    dbname = "chris"
}

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

//---***---***---***---***---***--- data ---***---***---***---***---***--

//this(){}

//---***---***---***---***---***--- functions ---***---***---***---***---***--

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
