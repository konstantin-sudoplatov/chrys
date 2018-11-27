module concurrency_stress_2;
import std.stdio;
import std.concurrency, core.thread, core.atomic;
import std.random;
import std.datetime.stopwatch;
import std.algorithm;
import core.exception;

import proj_memoryerror, proj_data;

import chri_data, chri_types;
import cpt.cpt_neurons, cpt.cpt_premises;
import messages;

import atn.atn_dispatcher, atn.atn_caldron;

enum MAX_THREADS = 100;
static assert(MAX_THREADS >= 4, "Total number of threads cannot be less than 4.");
enum MAX_MESSAGES = 100_000;
enum MAX_HOPS = 5_000;
enum ROTATION_THRESHOLD = 500;     // number of hops before adding/subtracting threads in wrkPool
enum ROTATION_UP_TO = 5;            // on reaching threshold add up to -/+ ROTATION_UP_TO threads
enum ROTATION_TIMEOUT = 10.msecs;   // waiting for belated test messages before returning thread to the pool

shared WorkerPool wrkPool;
shared MessagePool msgPool;

shared static this() {
    assert(registerMemoryErrorHandler);

    // Create infrastructure
    cast()_attnDispTid_ = spawn(&attention_dispatcher_thread_func);
    cast()_mainTid_ = thisTid;
    _sm_ = new shared SpiritMap;
    cast(shared)_threadPool_ = new shared CaldronThreadPool;
    wrkPool = new WorkerPool;

    StopWatch sw;

    sw.start;

    // Create threads
    foreach(unused; 0..MAX_THREADS/2-1)
        wrkPool.addActive(getThread);
    CaldronThread portal = getThread;
    wrkPool.addActive(portal);
    sw.stop;
    writefln("Threads created: %s [ms]", sw.peek.total!"msecs"); stdout.flush;
    sw.start;

    // Create  and inject messages
    msgPool = new MessagePool;
    foreach(i; 0..MAX_MESSAGES) {
        auto m = new immutable Test_msg(i);
        msgPool.add(m);
        portal.tid.send(m);
    }
    sw.stop;
    writefln("Messages created&injected: %s [ms]", sw.peek.total!"msecs"); stdout.flush;
    sw.start;

    // Let messages travel
    while(msgPool.length) {
        Throwable ex;
        receiveTimeout(
            0.seconds,
            (shared Throwable e) { ex = cast()e; }
        );
        if(ex) throw ex;

//        Thread.sleep(SPIN_WAIT);
Thread.sleep(1.seconds);
writefln("msgPool.length %s, wrkPool.active %s, wrkPool.retiring %s, _threadPool_.cannedThreads %s, _threadPool_.activeThreads %s",
        msgPool.length, wrkPool.activeThreads_.length, wrkPool.retiringThreads_.length, _threadPool_.cannedThreads,
        _threadPool_.activeThreads); stdout.flush;
    }
    sw.stop;
    writefln("Messages stopped traveling: %s [ms]", sw.peek.total!"msecs"); stdout.flush;
    sw.start;

    // Terminate workers
    wrkPool.returnAllThreadsToPool;

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
    void returnAllThreadsToPool() {

        // cann retiring
        foreach(thread; (cast()retiringThreads_).byValue) {
            _threadPool_.push(cast()thread);
        }

        // cann active
        foreach(thread; activeThreads_) {
            _threadPool_.push(cast()thread);
        }
    }

    /// Increment hopCount_ until it reaches ROTATION_THRESHOLD. Then rotate threads.
    void countHop() {
        atomicOp!"+="(hopCount_, 1);
        if(hopCount_ >= ROTATION_THRESHOLD) {
            hopCount_ = 0;
            int addThreads = uniform!"[]"(-ROTATION_UP_TO, ROTATION_UP_TO);
//writefln("addThreads %s, activeThreads_.length %s, retiringThreads_.length %s, _threadPool_.threads.length %s", addThreads,
//        activeThreads_.length, retiringThreads_.length, _threadPool_.threads.length); stdout.flush;
            addThreads = max(2-cast(int)activeThreads_.length, addThreads);     // add >= 2 - activeThreads_.length;
            addThreads = min(MAX_THREADS-activeThreads_.length, addThreads);    // add <= MAX_THREADS - activeThreads_.length;
//writefln("tempered addThreads %s", addThreads); stdout.flush;
            assert(activeThreads_.length+addThreads >=2 && activeThreads_.length+addThreads <= MAX_THREADS);
            if      //need to add threads?
                    (addThreads > 0)
            {
                foreach(unused; 0..addThreads) {
                    addActive(getThread);
                }
            }
            else {
                foreach(unused; 0..-addThreads) {
                    shared CaldronThread thread = activeThreads_[$-1];
                    activeThreads_ = activeThreads_.remove(activeThreads_.length-1);
                    retiringThreads_[(cast()thread).tid] = thread;
                    send((cast()thread).tid, new immutable RequestReturningThreadToPool_msg);
                }
            }
        }
    }

    /// Randomly choose an active thread and send it a message
    void randomlySendToActiveThread(Test_msg msg) {
        const ulong destThreadInd = uniform(0, wrkPool.activeLength);
//writefln("m.id %s, m.numberOfHops %s, destThreadInd %s, wrkPool.activeLength %s", msg.id, msg.numberOfHops,
//        destThreadInd, wrkPool.activeLength); stdout.flush;
        wrkPool.getActiveThread(destThreadInd).tid.send(cast(immutable)msg);
    }

    /// Remove from list of retiring threads
    void removeFromRetiring(CaldronThread thread) {
        retiringThreads_.remove(thread.tid);
    }

    @property activeLength() { return activeThreads_.length; }

    CaldronThread getActiveThread(ulong ind) { return cast()activeThreads_[ind]; }

    private CaldronThread[] activeThreads_;

    private CaldronThread[Tid] retiringThreads_;

    private int hopCount_;
}

synchronized class MessagePool {

    void add(immutable Test_msg msg) {
        messages_[msg.id] = cast(shared)msg;
    }

    void remove(immutable Test_msg msg) {
        messages_.remove(msg.id);
    }

    ulong length() { return messages_.length; }

    private Test_msg[int] messages_;
}

class Test_msg: Msg {

    immutable int id;
    int numberOfHops = 0;

    this(int id) {
        super();
        this.id = id;
    }

    @property void incrementNumberOfHops() { (cast()numberOfHops)++; }
}

class RequestReturningThreadToPool_msg: Msg {
    this() { super(); }
}

class TestCaldron: Caldron {

    this() {
        SpActionNeuron seed = new SpActionNeuron(atomicOp!"+="(newCid, 1));
        _sm_.add(seed);

        DcpDsc breedDynCptDesc = DcpDsc("SpBreed", atomicOp!"+="(newCid, 1));
        SpBreed breed = new SpBreed(breedDynCptDesc.cid);
        breed.load(breedDynCptDesc, null, null);
        _sm_.add(breed);
        super(null, breedDynCptDesc.cid, null);
    }

    protected override bool _processMessage(immutable Msg msg) {

        if(auto m = cast(immutable Test_msg) msg) {
            if(m.numberOfHops >= MAX_HOPS) {
                msgPool.remove(m);
                return true;
            }

            m.incrementNumberOfHops;
            wrkPool.countHop;
            wrkPool.randomlySendToActiveThread(cast()m);
            return true;
        }
        else if (auto m = cast(immutable RequestReturningThreadToPool_msg) msg) {
            assert(myThread);

            // Process belated
            while(true) {
                Test_msg testMsg;
                receiveTimeout(
                    ROTATION_TIMEOUT,
                    (immutable Test_msg tm) { testMsg = cast()tm; }
                );

                if      // belated message has come?
                        (testMsg)
                    //yes: recurse
                    _processMessage(cast(immutable)testMsg);
                else //no: that was timeout, break
                    break;
            }

            wrkPool.removeFromRetiring(myThread);
            _threadPool_.push(myThread);
            return true;
        }

        return false;
    }
}
shared Cid newCid = MIN_DYNAMIC_CID;    // unique cid for creating concepts
