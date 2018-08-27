module chris;
import std.stdio;
import std.concurrency, core.thread;
import std.variant;
import std.format;

import tools;
import global;
import messages;

// Show in console_thread, that it is the unittest mode
version(unittest) {
    pragma(msg, "Unittest");
}

/**
    Main function of the project.
    Main initialization including creation of the key processes done in the global module constructor. Actually, the application
    started there. Here we only wait for messages requiring termination.
*/
void main()
{
    // Wait for messages from the key threads. Thematically applicable only requests for termination or rethrown exceptions.
    while(true) {
        TerminateAppMsg termMsg;
        Throwable ex;

        Variant var;
        receive(
            (immutable TerminateAppMsg m){termMsg = cast()m;},
            (shared Throwable e){ex = cast()e;},
            (Variant v) {var = v;}
        );

        if      // TerminateAppMsg message has come?
                (termMsg) // || var.hasValue)
        {   //yes: terminate other subthreads, terminate application
            (cast()_attnDispTid_).send(new immutable TerminateAppMsg());
            goto TERMINATE_APPLICATION;
        }
        else if // has one of the thead thrown an exception?
                (ex)
        {   // rethrow it
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
