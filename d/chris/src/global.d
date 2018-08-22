/// Shared memory, global parameters.
module global;
import std.concurrency;

import tools;

immutable Tid mainTid;      /// Tid of the main thread
immutable Tid consoleTid;   /// Console thread Tid

/**
    Spawn the key threads (console, attention dispatcher), capture their Tids.
*/
shared static this() {

    // Capture Tid of the main thread
    mainTid = cast(immutable)thisTid;

    // Spawn the console thread
    import console: console;
    consoleTid = cast(immutable)spawn(&console);
}
