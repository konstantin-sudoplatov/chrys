/// Console thread
module console;
import std.stdio;
import std.string;
import std.concurrency;

import tools;
import global;
import messages;

/**
    Dialog with user. Thread main function.

    Difficulty is that the debugger in Intellij Idea goes mad on the readln() function. So I have to write diffrent code
    for debugging. Instead taking strings from user it generates it in internally and sends as if they were entered by the user.
*/
void console() {
debug {
        // define a generator function as a series of lines, that user was supposed to enter.
        import std.concurrency;
        auto r = new Generator!string({
            yield("hello");
            yield("world!");
            yield("p");
        });
}
    try {
        while(true) {

            // print promt
            write("> "); stdout.flush;

debug { // take the next line from generator
                string s = r.front;
                r.popFront;
                writeln(s);
}
else {  // take the next line from user
                string s = readln.strip;        // read line from console and strip whitespaces including \ln
}

            if // termination of the application was requested?
            (s == "p" || s == "п")
            {   //yes: send termination request to the main thread and finish ours.
                (cast()mainTid).send(cast(shared)new TerminateAppMsg());
                goto FINISH_THE_THREAD;
            }
        }
    }
    catch   // uncaught exception happened in the function?
            (Throwable e)
    {   //yes: send it to the main thread, so it would rethrow it
        (cast()mainTid).send(cast(shared)e);
    }

FINISH_THE_THREAD:
}
