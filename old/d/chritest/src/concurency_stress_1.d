/**
        This test is fully self-contained and independent.
    There is a pool of threads, size MAX_THREADS, and pool of messages, of MAX_MESSAGES. Messages are randomly bounced
    between threads. Each message has the counter of hops. When the counter reaches the limit of MAX_HOPS, the message
    is withdrawn from ciculation. When there are no messages remaind, test stops.
*/
module concurency_stress_1;
import std.stdio;
import std.concurrency, core.thread;
import std.datetime.stopwatch, core.time;
import std.typecons;
import std.random;

enum MAX_THREADS = 100;
enum MAX_MESSAGES = 10000;
enum MAX_HOPS = 1000;

shared TestThreadPool threadPool = new shared TestThreadPool;
shared TestMessagePool msgPool = new shared TestMessagePool;

shared static this() {
bool yes = true;if(yes) return;     // bypass this test
    import proj_memoryerror;
    assert(registerMemoryErrorHandler);

    StopWatch sw;

    sw.start;

    // Create threads
    foreach(i; 0..MAX_THREADS-1) {
        new TestThread(i, threadPool, msgPool);
    }
    TestThread portal = new TestThread(MAX_THREADS-1, threadPool, msgPool);     // to it we will forward newly created messages
    sw.stop;
    writefln("Threads created: %s [ms]", sw.peek.total!"msecs"); stdout.flush;
    sw.start;

    // Create and inject messages
    foreach(i; 0..MAX_MESSAGES) {
        TestMessage m = new TestMessage(i);
        msgPool.add(m);
        portal.tid.send(cast(shared) m);
    }
    sw.stop;
    writefln("Messages created&injected: %s [ms]", sw.peek.total!"msecs"); stdout.flush;
    sw.start;

    // Let messages travel
    while(msgPool.length) {
        Thread.sleep(100.usecs);
    }
    sw.stop;
    writefln("Messages stopped traveling: %s [ms]", sw.peek.total!"msecs"); stdout.flush;
    sw.start;

    // Stop threads
    threadPool.terminate;

    thread_joinAll;

    sw.stop;
    writefln("Total: %s [ms]", sw.peek.total!"msecs"); stdout.flush;
}

synchronized class TestThreadPool {

    void add(TestThread thread) {
        threads_[thread.id] = cast(shared)thread;
    }

    void remove(TestThread thread) {
        threads_.remove(thread.id);
    }

    TestThread get(int id) {
        return cast()threads_[id];
    }

    void terminate() {
        foreach(id, thread; threads_)
            (cast()thread).tid.send(new shared Terminate_msg);
    }

    ulong length() { return threads_.length; }

    private TestThread[int] threads_;
}

class TestThread {

    int id;

    this(int id, shared TestThreadPool threadPool, shared TestMessagePool msgPool) {
        this.id = id;
        threadPool_ = threadPool;
        msgPool_ = msgPool;
        threadPool.add(this);
        tid_ = spawn(cast(shared void delegate())&threadFunc);
    }

    void threadFunc() {
        scope(exit) {
            threadPool_.remove(this);
        }

        while(true) {
            TestMessage msg;
            Terminate_msg term;
            receive(
                (shared TestMessage m) { msg = cast()m; },
                (shared Terminate_msg t) { term = cast()t; }
            );

            if(term) {
                return ;
            }

            if(msg.numberOfHops >= MAX_HOPS) {
                msgPool_.remove(msg);
                continue;
            }
            else {
                msg.numberOfHops++;
                int destThreadId = uniform(0, MAX_THREADS);
                TestThread destThread = threadPool_.get(destThreadId);
                destThread.tid.send(cast(shared) msg);
            }
        }
    }

    @property Tid tid() { return tid_; }

    private shared TestThreadPool threadPool_;
    private shared TestMessagePool msgPool_;
    private Tid tid_;
}

synchronized class TestMessagePool {

    void add(TestMessage msg) {
        messages_[msg.id] = cast(shared)msg;
    }

    void remove(TestMessage msg) {
        messages_.remove(msg.id);
    }

    ulong length() { return messages_.length; }

    private TestMessage[int] messages_;
}

class TestMessage {
    int id;
    int numberOfHops;

    this(int id) {
        this.id = id;
    }
}

shared class Terminate_msg {}