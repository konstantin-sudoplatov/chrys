module chris;
import std.stdio;
import std.concurrency, core.thread;
import std.variant;
import std.format;

import tools;
import global;
import messages;

// Show in console, that it is the unittest mode
version(unittest) {
    pragma(msg, "Unittest");
}

/**
    Main function of the project.
    Main initialization including creation of the key processes done in the global module constructor. Actually, the application
    started there. Here we wait for messages requiring termination and do it.
*/
void main()
{
    while(true) {
        shared TerminateAppMsg termMsg;
        shared Throwable ex;

        Variant var;
        receive(
            (shared TerminateAppMsg m){termMsg = m;},
            (shared Throwable e){ex = e;},
            (Variant v) {var = v;}
        );

        if      // TerminateAppMsg message has come?
                (termMsg) // || var.hasValue)
        {   //yes: terminate application
            goto TERMINATE_APPLICATION;
        }
        else if // has one of the thead thrown an exception?
                (ex)
        {   // rethrown it
            throw ex;
        }
        else if // has come an unexpected message?
                (var.hasValue)
        {   // log it
            logit(format!"Unexpected message to the main thread: %s"(var.toString));
        }
    }

TERMINATE_APPLICATION:
    scope(exit) {
        thread_joinAll;
        writeln("good bye, world!"); stdout.flush;
    }
}
