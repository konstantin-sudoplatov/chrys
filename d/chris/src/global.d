/// Shared memory, global parameters.
module global;
import std.concurrency;

import tools;
import attn.attn_dispatcher_thread;

//---***---***---***---***---***--- types ---***---***---***---***---***---***

alias Cid = uint;       /// Concept identifier is 4 bytes long at the moment.


//---***---***---***---***---***--- data ---***---***---***---***---***--

// Key threads of the project
immutable Tid _mainTid_;         /// Tid of the main thread
immutable Tid _attnDispTid_;     /// Attention dispatcher thread Tid

//---***---***---***---***---***--- functions ---***---***---***---***---***--

/**
    Spawn the key threads (console_thread, attention dispatcher), capture their Tids.
*/
shared static this() {

    // Capture Tid of the main thread.
    _mainTid_ = cast(immutable)thisTid;

    // Spawn the attention dispatcher thread.
    _attnDispTid_ = cast(immutable)spawn(&attention_dispatcher_thread);

    // Spawn the console thread thread.
    import console_thread: console_thread;
    spawn(&console_thread);
}


//###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
//
//                               Private
//
//###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
private:
//---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

///     caldron/cid map, where "cid" is the cid of the seed neuron of the reasoning branch as an identifier of the branch
/// and caldron. We will need synchronization, because the map can be concurrently accessed by different caldrons.
shared Tid[Cid] _caldronMap_;


//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--


//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
