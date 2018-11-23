module concurrency_stress_2;
import std.stdio;
import std.concurrency, core.thread;
import std.datetime.stopwatch;

import proj_data;

import chri_data, chri_types;
import cpt.cpt_neurons, cpt.cpt_premises;
import messages;

import atn.atn_dispatcher, atn.atn_caldron;

enum MAX_THREADS = 5;
enum MAX_MESSAGES = 10000;
enum MAX_HOPS = 1000;

shared static this() {
    import proj_memoryerror;
    assert(registerMemoryErrorHandler);

    // Create infrastructure
    cast()_attnDispTid_ = spawn(&attention_dispatcher_thread_func);
    cast()_mainTid_ = thisTid;
    _sm_ = new shared SpiritMap;
    cast(shared)_threadPool_ = new shared CaldronThreadPool;

    StopWatch sw;

    sw.start;

    // Create threads
    shared WorkerPool wrkPool = new WorkerPool;
    foreach(unused; 0..MAX_THREADS-1)
        wrkPool.addActive(getThread);
    CaldronThread portal = getThread;
    wrkPool.addActive(portal);
    sw.stop;
    writefln("Threads created: %s [ms]", sw.peek.total!"msecs"); stdout.flush;
    sw.start;

    MessagePool msgPool = new MessagePool;

    // Terminate workers
    wrkPool.expellAll;

    // Terminate all canned threads
    for(; !_threadPool_.requestTerminatingCanned;)
        Thread.sleep(SPIN_WAIT);
    sw.stop;
    writefln("Threads stopped: %s [ms]", sw.peek.total!"msecs"); stdout.flush;
    sw.start;

    // Request terminating dispatcher
    (cast()_attnDispTid_).send(new immutable TerminateApp_msg);

    thread_joinAll;

    sw.stop;
    writefln("Total: %s [ms]", sw.peek.total!"msecs"); stdout.flush;
}

/// Create new caldron with empty seed (it will be waiting on the seed).
private Caldron getCaldron_() {
    static Cid newCid = MIN_DYNAMIC_CID;

    SpActionNeuron seed = new SpActionNeuron(newCid++);
    _sm_.add(seed);

    DcpDsc breedDynCptDesc = DcpDsc("SpBreed", newCid++);
    SpBreed breed = new SpBreed(breedDynCptDesc.cid);
    breed.load(breedDynCptDesc, null, null);
    _sm_.add(breed);

    return new Caldron(null, breedDynCptDesc.cid, null);
}

/// Get thread from the pool
private CaldronThread getThread() {

    CaldronThread thread;
    for(; (thread = _threadPool_.pop(getCaldron_)) is null;)
        Thread.sleep(SPIN_WAIT);

    return thread;
}

synchronized class WorkerPool {

    /// Add a thread to the list of active threads
    void addActive(CaldronThread thread) {
        activeThreads_ ~= cast(shared)thread;
    }

    /// Push all threads, active and retiring back to the thread pool
    void expellAll() {

        // Terminate retiring
        foreach(thread; (cast()retiringThreads_).byValue) {
            _threadPool_.push(cast()thread);
        }

        // Terminate active
        foreach(thread; activeThreads_) {
            _threadPool_.push(cast()thread);
        }
    }

    private CaldronThread[] activeThreads_;

    private CaldronThread[Tid] retiringThreads_;
}

synchronized class MessagePool {

    void add(Message msg) {
        messages_[msg.id] = cast(shared)msg;
    }

    void remove(Message msg) {
        messages_.remove(msg.id);
    }

    ulong length() { return messages_.length; }

    private Message[int] messages_;
}

class Message: Msg {

    immutable int id;
    int numberOfHops = 0;

    this(int id) {
        super();
        this.id = id;
    }
}
