/// Console thread
module console_thread;
import std.stdio;
import std.string;
import std.concurrency;
import core.thread;

import proj_data, proj_funcs;

import chri_data;
import messages;

enum msgTimeout = 10;    // seconds
/+
/**
                            Thread function for console.
    There is an issue - the debugger in Intellij Idea goes mad on the readln() function. So I have to write diffrent code
    for debugging. Instead of taking strings from user it generates and sends them out as if they were entered by user.
*/
void console_thread_func() {try {   // catchall try block for catching flying exceptions and forwarding them to the owner thread.
    import std.variant: Variant;

    // Get new line from user (separate treatment for the debug and release mode).
    debug {
        // define a generator function as a series of lines, that user was supposed to enter.
        auto r = new Generator!string({
            yield("hello");
            yield("world!");
            yield("world!");
            yield("world!");
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
            logit("Message timeout %s in console thread while getting the circle's Tid.".format(msgTimeout));
        }
    }
    while(attnCircleTid_ == Tid.init);      // until get the circle's Tid

    while(true) {
        string userLine;
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
            return;
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
                    logit("Unexpected message of Msg type in console thread: %s".format(msg), TermColor.red);
            }
            else {  //no: that was variant. Log an error, continue
                logit("Unexpected message of Variable type in console thread: %s".format(var.toString), TermColor.red);
            }
        }
        else {  //no: log it, continue
            logit("Message timeout %s in console thread while waiting for the line request.".format(msgTimeout));
            continue;
        }
    }

} catch // uncaught exception happened in the function?
        (Throwable e)
{   //yes: send it to the main thread, so it could be rethrown
    ownerTid.send(cast(shared)e);
}}
+/
/**
                            Thread function for console.
    There is an issue - the debugger in Intellij Idea goes mad on the readln() function. So I have to write diffrent code
    for debugging. Instead of taking strings from user it generates and sends them out as if they were entered by user.
*/
void console_thread_func() {try {   // catchall try block for catching flying exceptions and forwarding them to the owner thread.
    import std.variant: Variant;

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
            logit("Message timeout %s in console thread while getting the circle's Tid.".format(msgTimeout));
        }
    }
    while(attnCircleTid_ == Tid.init);      // until get the circle's Tid

    // Get new line from user (separate treatment for the debug and release modes).
    debug {
        // define a generator function as a series of lines, that user was supposed to enter.
        auto r = new Generator!string({
            yield("hello");
            yield("world!");
            yield("p");
            //Thread.sleep(1000.msecs);    // this time will actually pass BEFORE the "p" would work (giving chat time to process the last word).
        });
    }
    else
        spawn(&readln_thread_func);

    // Message cycle
    bool requestedStop;      // deferred stop
    bool timedOut;
    while(true) {
        // receive message from the attention circle/dispatcher or user
        string userLine;
        Msg msg;
        Variant var;

        // get line from user
        debug { // take the next line from generator
            if (!r.empty) {
                userLine = r.front;
                r.popFront;
                writefln("> %s", userLine); stdout.flush;
            }
            else
                timedOut = !receiveTimeout(
                    msgTimeout.seconds,
                    (immutable Msg m){ msg = cast()m; },
                    (Variant v) { var = v; }
                );
        }
        else
            timedOut = !receiveTimeout(
                msgTimeout.seconds,
                (string uln){ userLine = uln; },
                (immutable Msg m){ msg = cast()m; },
                (Variant v) { var = v; }
            );

        if      // requested stop and nothing has come during timeout?
                (timedOut && requestedStop)
            return;     // finish

        if      // from user?
                (userLine)
        {
            if      // termination of the application was requested?
                    (userLine == "p" || userLine == "п")
            {   //yes: send termination request to the main thread and finish ours.
                (cast()_mainTid_).send(new immutable TerminateApp_msg());
                requestedStop = true;
            }
            else {//no: the line is intended for the attention circle, send it there
                (cast()attnCircleTid_).send(new immutable UserTellsCircle_msg(userLine));
            }
        }
        else if // from the circle?
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
                logit("Unexpected message of Msg type in console thread: %s".format(msg), TermColor.red);
        }
        else if(var.hasValue)
        {  //no: that was variant. Log an error, continue
            logit("Unexpected message of Variable type in console thread: %s".format(var.toString), TermColor.red);
        }
    }

} catch // uncaught exception happened in the function?
        (Throwable e)
{   //yes: send it to the main thread, so it could be rethrown
    ownerTid.send(cast(shared)e);
}}

/**
    Yet another thread. It is needed to make the readln() function non-blocking.
*/
void readln_thread_func() {try {

    while(true) {
        write("> "); stdout.flush;
        string s = readln.strip;        // read line from console and strip whitespaces including \ln
        ownerTid.send(s);
        if      // termination of the application was requested?
                (s == "p" || s == "п")
        {   //yes: send termination request to the main thread and finish ours.
            return;
        }
    }
} catch // uncaught exception happened in the function?
        (Throwable e)
{   //yes: send it to the main thread, so it could be rethrown
    ownerTid.send(cast(shared)e);
}}

/// The thread to talk to. It may be either an attention circle or the attention dispatcher if the circle does not exist yet.
private Tid attnCircleTid_;
