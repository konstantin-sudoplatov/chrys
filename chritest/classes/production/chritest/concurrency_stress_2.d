module concurrency_stress_2;
import std.stdio;
import std.concurrency, core.thread;

import chri_data;
import messages;

import atn.atn_dispatcher, atn.atn_caldron;


shared CaldronThreadPool pool;

shared static this() {
    import proj_memoryerror;
    assert(registerMemoryErrorHandler);

    cast()_attnDispTid_ = spawn(&attention_dispatcher_thread_func);
    cast()_mainTid_ = thisTid;
    cast(shared)_threadPool_ = new shared CaldronThreadPool;

    auto thread = new CaldronThread;
    thread.spawn;
    pool.push(thread);

    pool.terminate_canned;
    (cast()_attnDispTid_).send(new immutable TerminateApp_msg);
    thread_joinAll;
}