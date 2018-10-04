module chris;
import std.stdio;
import std.concurrency, core.thread;
import std.variant;
import std.format;

import common_tools, db.db_main;

import global_data;
import messages;
import attn_dispatcher_thread;

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
    version(unittest)
        if(auto b=true) return;

    // Connect to the data base
    connectToDb;
    scope(exit)
        disconnectFromDb;

    // Capture Tid of the main thread.
    cast()_mainTid_ = thisTid;

    // Spawn the attention dispatcher thread.
    cast()_attnDispTid_ = spawn(&attention_dispatcher_thread_func);

    // Spawn the console thread thread.
    import console_thread: console_thread_func;
    spawn(&console_thread_func);

    // Wait for messages from the key threads. Thematically applicable only requests for termination or rethrown exceptions.
    while(true) {
        TerminateApp_msg termMsg;
        Throwable ex;

        Variant var;
        receive(
            (immutable TerminateApp_msg m){termMsg = cast()m;},
            (shared Throwable e){ex = cast()e;},
            (Variant v) {var = v;}
        );

        if      // TerminateAppMsg message has come?
                (termMsg) // || var.hasValue)
        {   //yes: terminate other subthreads, terminate application
            (cast()_attnDispTid_).send(new immutable TerminateApp_msg());
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
