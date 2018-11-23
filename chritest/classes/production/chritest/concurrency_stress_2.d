module concurrency_stress_2;
import std.stdio;
import std.concurrency, core.thread;
import std.datetime.stopwatch;

import proj_memoryerror, proj_data;

import chri_data, chri_types;
import cpt.cpt_neurons, cpt.cpt_premises;
import messages;

import atn.atn_dispatcher, atn.atn_caldron;

enum MAX_THREADS = 100;
enum MAX_MESSAGES = 10000;
enum MAX_HOPS = 1000;

shared static this() {
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

    // Create  and inject messages
    shared static MessagePool msgPool = new MessagePool;
    foreach(i; 0..MAX_MESSAGES) {
        auto m = new immutable TestMessage(i);
        msgPool.add(cast()m);
        portal.tid.send(cast(shared) m);
    }
    sw.stop;
    writefln("Messages created&injected: %s [ms]", sw.peek.total!"msecs"); stdout.flush;
    sw.start;

    // Terminate workers
    wrkPool.expellAll;

    // Terminate all canned threads
    for(; !_threadPool_.requestTerminatingCanned;)
        Thread.sleep(SPIN_WAIT);

    // Request terminating dispatcher
    (cast()_attnDispTid_).send(new immutable TerminateApp_msg);

    thread_joinAll;

    sw.stop;
    writefln("Total: %s [ms]", sw.peek.total!"msecs"); stdout.flush;
}

/// Get thread from the pool
private CaldronThread getThread() {

    TestCaldron cld = new TestCaldron;
    CaldronThread thread;
    for(; (thread = _threadPool_.pop(cld)) is null;) {
        Thread.sleep(SPIN_WAIT);
    }

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

    void add(TestMessage msg) {
        messages_[msg.id] = cast(shared)msg;
    }

    void remove(TestMessage msg) {
        messages_.remove(msg.id);
    }

    ulong length() { return messages_.length; }

    private TestMessage[int] messages_;
}

class TestMessage: Msg {

    immutable int id;
    int numberOfHops = 0;

    this(int id) {
        super();
        this.id = id;
    }
}

class TestCaldron: Caldron {

    this() {
        static Cid newCid = MIN_DYNAMIC_CID;

        SpActionNeuron seed = new SpActionNeuron(newCid++);
        _sm_.add(seed);

        DcpDsc breedDynCptDesc = DcpDsc("SpBreed", newCid++);
        SpBreed breed = new SpBreed(breedDynCptDesc.cid);
        breed.load(breedDynCptDesc, null, null);
        _sm_.add(breed);
        super(null, breedDynCptDesc.cid, null);
    }
}