module tools_db;
import std.conv;
import std.stdio;

import derelict.pq.pq;


shared static this() {

    // Add shared library /usr/lib/x_86-64-linux-gnu/libpq.so
//    DerelictPQ.load();

    PGconn * connection;

    connection = PQconnectdb( "user='su' dbname='chrysolite'" );
    if (!connection) writefln("Connecting failed");
    if (PQstatus(connection) != CONNECTION_OK)
        writefln("%s", to!string(PQerrorMessage(connection)));

    PGresult  *       result;
    result = PQexec(connection, `select * from "EURUSD" limit 3`);

    PQprintOpt        options;
    options.header    = 1;              /* Ask for column headers            */
    options.aligment     = 1;           /* Pad short columns for alignment   */
    options.fieldSep  = cast(char*)"|"; /* Use a pipe as the field separator */

    {
        import core.stdc.stdio: stdout;
        PQprint(stdout, result, &options);
    }

    PQfinish( connection);
}

//---***---***---***---***---***--- types ---***---***---***---***---***---***

//---***---***---***---***---***--- data ---***---***---***---***---***--

/**
        Constructor
*/
//this(){}

//---***---***---***---***---***--- functions ---***---***---***---***---***--

//~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
//
//                                 Protected
//
//~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
protected:
//---$$$---$$$---$$$---$$$---$$$--- data ---$$$---$$$---$$$---$$$---$$$--

//---$$$---$$$---$$$---$$$---$$$--- functions ---$$$---$$$---$$$---$$$---$$$---

//---$$$---$$$---$$$---$$$---$$$--- types ---$$$---$$$---$$$---$$$---$$$---

//===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
//
//                                  Private
//
//===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
private:
//---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
