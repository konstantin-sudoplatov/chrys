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

shared ThreadPool threadPool = new shared ThreadPool;
shared MessagePool msgPool = new shared MessagePool;

shared static this() {
bool yes = true;if(yes) return;     // bypass this test
    import proj_memoryerror;
    assert(registerMemoryErrorHandler);

    StopWatch sw;

    sw.start;

    // Create threads
    foreach(i; 0..MAX_THREADS-1) {
        new Thread_(i, threadPool, msgPool);
    }
    Thread_ portal = new Thread_(MAX_THREADS-1, threadPool, msgPool);     // to it we will forward newly created messages
    sw.stop;
    writefln("Threads created: %s [ms]", sw.peek.total!"msecs"); stdout.flush;
    sw.start;

    // Create and inject messages
    foreach(i; 0..MAX_MESSAGES) {
        Message m = new Message(i);
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

synchronized class ThreadPool {

    void add(Thread_ thread) {
        threads_[thread.id] = cast(shared)thread;
    }

    void remove(Thread_ thread) {
        threads_.remove(thread.id);
    }

    Thread_ get(int id) {
        return cast()threads_[id];
    }

    void terminate() {
        foreach(id, thread; threads_)
            (cast()thread).tid.send(new shared Terminate_msg);
    }

    ulong length() { return threads_.length; }

    private Thread_[int] threads_;
}

class Thread_ {

    int id;

    this(int id, shared ThreadPool threadPool, shared MessagePool msgPool) {
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
            Message msg;
            Terminate_msg term;
            receive(
                (shared Message m) { msg = cast()m; },
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
                Thread_ destThread = threadPool_.get(destThreadId);
                destThread.tid.send(cast(shared) msg);
            }
        }
    }

    @property Tid tid() { return tid_; }

    private shared ThreadPool threadPool_;
    private shared MessagePool msgPool_;
    private Tid tid_;
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

class Message {
    int id;
    int numberOfHops;

    this(int id) {
        this.id = id;
    }
}

shared class Terminate_msg {}