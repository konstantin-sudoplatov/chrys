/// Shared memory, global parameters.
module global;
import std.concurrency;

import tools;

immutable Tid _mainTid_;         /// Tid of the main thread
immutable Tid _attnDispTid_;     /// Attention dispatcher thread Tid
immutable Tid _consoleTid_;      /// Console thread Tid
shared Tid _consoleAttnCircleTid_;    /// A thread for console to contact with. It can be either an attention circle or the dispatcher if the circle does not exist yet.

/**
    Spawn the key threads (console_thread, attention dispatcher), capture their Tids.
*/
shared static this() {

    // Capture Tid of the main thread.
    _mainTid_ = cast(immutable)thisTid;

    // Spawn the attention dispatcher thread.
    import attn.disp_thread: attn_dispatcher;
    _attnDispTid_ = cast(immutable)spawn(&attn_dispatcher);

    // Spawn the console thread thread.
    import console_thread: console;
    _consoleTid_ = cast(immutable)spawn(&console);

    // Asign the console vis-a-vis. There is no attention circle for the console created yet, so it'll be the attention dispatcher
    _consoleAttnCircleTid_ = cast(shared)_attnDispTid_;
}
