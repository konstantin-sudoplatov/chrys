/// Shared memory, global parameters.
module global;
import std.concurrency;

import tools;
import attn.attn_dispatcher_thread;

immutable Tid _mainTid_;         /// Tid of the main thread
immutable Tid _attnDispTid_;     /// Attention dispatcher thread Tid
immutable Tid _consoleTid_;      /// Console thread Tid
shared Tid _consoleAttnCircleTid_;    /// A thread for console to contact with. It can be either an attention circle or the dispatcher if the circle does not exist yet.

shared AttentionDispatcher _attnDisp_ = new shared AttentionDispatcher();

/**
    Spawn the key threads (console_thread, attention dispatcher), capture their Tids.
*/
shared static this() {

    // Capture Tid of the main thread.
    _mainTid_ = cast(immutable)thisTid;

    // Spawn the attention dispatcher thread.
    _attnDispTid_ = cast(immutable)spawn(&_attnDisp_.thread_function);

    // Spawn the console thread thread.
    import console_thread: console_thread;
    _consoleTid_ = cast(immutable)spawn(&console_thread);

    // Asign the console vis-a-vis. There is no attention circle for the console created yet, so it'll be the attention dispatcher
    _consoleAttnCircleTid_ = cast(shared)_attnDispTid_;
}
