/// Console thread
module console_thread;
import std.stdio;
import std.string;
import std.concurrency;
import core.thread;

import proj_data, proj_funcs;

import chri_data;
import messages;

enum msgTimeout = 1;    // seconds

/**
                            Thread function for console.
    There is an issue - the debugger in Intellij Idea goes mad on the readln() function. So I have to write diffrent code
    for debugging. Instead taking strings from user it generates it in internally and sends as if they were entered by the user.
*/
void console_thread_func() {try {   // catchall try block for catching flying exceptions and forwarding them to the owner thread.
    import std.variant: Variant;

    // Get new line from user (separate treatment for the debug and release mode).
    debug {
        // define a generator function as a series of lines, that user was supposed to enter.
        auto r = new Generator!string({
            yield("hello");
            yield("world!");
            yield("p");
//            Thread.sleep(1.seconds);    // this time will actually pass BEFORE the "p" would work (giving chat time to process the last word).
        });
    }

    // Request creation of an attention circle thread and receive its Tid
    do {
        send(cast()_attnDispTid_, new immutable UserRequestsCircleTid_msg());
        CircleProvidesUserWithItsTid_msg msg;
        bool gotMsg = receiveTimeout(
            msgTimeout.seconds,
            (immutable CircleProvidesUserWithItsTid_msg m) { msg = cast()m; }
        );
        if
                (gotMsg)
        {
            attnCircleTid_ = (cast(immutable)msg).tid;
            break;
        }
        else {
            logit(format!"Message timeout %s in console thread while getting the circle's Tid."(msgTimeout));
        }
    }
    while(attnCircleTid_ == Tid.init);      // until get the circle's Tid

    while(true) {
        Msg msg;
        Variant var;

        // print promt
        write("> "); stdout.flush;

        // get line from user
        debug { // take the next line from generator
                string s = r.front;
                r.popFront;
                writeln(s); stdout.flush;
        }
        else {  // take the next line from user
                string s = readln.strip;        // read line from console and strip whitespaces including \ln
        }

        // Analize and forward line
        if      // termination of the application was requested?
                (s == "p" || s == "п")
        {   //yes: send termination request to the main thread and finish ours.
            (cast()_mainTid_).send(new immutable TerminateApp_msg());
            goto FINISH_THREAD;
        }
        else//no: the line is intended for the attention circle, send it there
        {
            (cast()attnCircleTid_).send(new immutable UserTellsCircle_msg(s));
        }

        // receive response from the attention circle/dispatcher
        bool gotMsg = receiveTimeout(
            msgTimeout.seconds,
            (immutable Msg m){ msg = cast()m; },
            (Variant v) { var = v; }
        );

        // Analize message
        if      // message was gotten?
        (gotMsg)
        {
            if      // it was Msg class?
                    (msg)
            {   //yes: analize it
                if      // is circle ready to take the next line?
                        (cast(immutable CircleListensToUser_msg)msg)
                {
                    continue;
                }
                else if // has cirle anything to tell to user?
                        (auto m = cast(immutable CircleTellsUser_msg)msg)
                {
                    writeln(m.line); stdout.flush;
                }
                else
                    logit(format!"Unexpected message of Msg type in console thread: %s"(msg));
            }
            else {  //no: that was variant. Log an error, continue
                logit(format!"Unexpected message of Variable type in console thread: %s"(var.toString));
            }
        }
        else {  //no: log it, continue
            logit(format!"Message timeout %s in console thread while waiting for the line request."(msgTimeout));
            continue;
        }
    }

} catch // uncaught exception happened in the function?
        (Throwable e)
{   //yes: send it to the main thread, so it would rethrow it
    ownerTid.send(cast(shared)e);
}

FINISH_THREAD:
}

/// The thread to talk to. It may be either an attention circle or the attention dispatcher if the circle does not exist yet.
private Tid attnCircleTid_;
