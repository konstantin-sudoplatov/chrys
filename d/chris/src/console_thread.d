/// Console thread
module console_thread;
import std.stdio;
import std.string;
import std.concurrency;

import tools;
import global;
import messages;

/**
    Dialog with user. Thread main function.

    There is an issue - the debugger in Intellij Idea goes mad on the readln() function. So I have to write diffrent code
    for debugging. Instead taking strings from user it generates it in internally and sends as if they were entered by the user.
*/
void console() {try {   // catchall try block for catching flying exceptions and forwarding them to the owner thread.

    // Initialize the correspondent with the attention dispatcher's Tid.
    attnCircleTid = cast()_attnDispTid_;

    // Get new line from user (separate treatment for the debug and release mode).
debug {
    // define a generator function as a series of lines, that user was supposed to enter.
    import std.concurrency;
    auto r = new Generator!string({
        yield("hello");
        yield("world!");
        yield("p");
    });
}

    while(true) {

        // print promt
        write("> "); stdout.flush;

        // get line from user
debug { // take the next line from generator
        string s = r.front;
        r.popFront;
        writeln(s);
}
else {  // take the next line from user
        string s = readln.strip;        // read line from console and strip whitespaces including \ln
}

        // Analize and forward line
        if      // termination of the application was requested?
                (s == "p" || s == "п")
        {   //yes: send termination request to the main thread and finish ours.
            (cast()_mainTid_).send(new immutable TerminateAppMsg());
            goto FINISH_THREAD;
        }
        else//no: the line is intended for an attention circle, send it there
        {
            (cast()attnCircleTid).send(new immutable ConsoleSaysToCircleMsg(s));
        }
    }
} catch // uncaught exception happened in the function?
        (Throwable e)
{   //yes: send it to the main thread, so it would rethrow it
    (cast()_mainTid_).send(cast(shared)e);
}

FINISH_THREAD:
}

/// The thread to talk to. It may be either an attention circle or the attention dispatcher if the circle does not exist yet.
private Tid attnCircleTid;
