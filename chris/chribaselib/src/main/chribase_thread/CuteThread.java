package chribase_thread;

import java.util.ArrayDeque;

import static basemain.Base_dataKt.DEFAULT_MAX_THREAD_QUEUE;
import static basemain.Base_dataKt.DEFAULT_THREAD_QUEUE_TIMEOUT;
import static basemain.Base_funcsKt.logit;

/**
 * Base class for message driven concurrency. It contains a message queue (cyclic buffer), access to which is synchronized
 * and methods for sending messages to it and taking them out. All message processing is fulfilled in the descendants.
 * This class extends the Thread class, not just implements the runnable interface, since it gets us access to the
 * Thread class, for example we would be able to use chribase_thread.currentThread().
 */
abstract public class CuteThread extends Thread {

    /**
     *      Default constructor.
     */
    public CuteThread() {
        super();
        timeout = DEFAULT_THREAD_QUEUE_TIMEOUT;
        maxQueueSize = DEFAULT_MAX_THREAD_QUEUE;
        threadName_ = "noname";
    }

    /**
     *      Constructor.
     * @param timeoutMsecs  timeout for waiting new messages in the queue. If timeout happens new message TimeoutMsg is
     *                      generated and sent for processing. 0 disables the timeout.
     * @param maxQueueSize  maximum number of messages in the queue. After that the putInQueue() method blocks. 0 - no limit.
     * @param threadName    thread name for debugging purposes.
     */
    public CuteThread(int timeoutMsecs, int maxQueueSize, String threadName) {
        super();
        timeout = timeoutMsecs;
        this.maxQueueSize = maxQueueSize != 0? maxQueueSize: Integer.MAX_VALUE;
        threadName_ = threadName;
    }

    @Override
    public String toString() {
        String s= this.getClass().getName();
        s += "\n    threadName = " + threadName_;

        return s;
    }

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                            Public
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Main cycle of taking out and processing messages from the queue. Not synchronized, called only once by the Thread.start()
     */
    @Override
    public void run() {
        while(true) {
            MessageMsg msg = _getBlocking();
            if (!_messageProc(msg)) {
                String threadName = !threadName_.equals("noname")? threadName_: this.getClass().getName();
                logit("Unexpected message in " + threadName + ": " + msg.getClass().getName());
            }

            if      // is termination requested?
                    (msg instanceof TerminationRequestMsg)
            {
                break;
            }
        }
    }

    /**
     * Shows if the queue is empty.
     * @return true/false
     */
    public synchronized boolean empty() {
        return _msgQueue.isEmpty();
    }

    /**
     * Get the size of the queue.
     * @return current queue size
     */
    public synchronized int size() {
        return _msgQueue.size();
    }

    /**
     * Put the message in the tail of the queue.
     * @param msg message
     */
    public final synchronized void putInQueue(MessageMsg msg) {

        while (_msgQueue.size() >= maxQueueSize) {
            try {
                queueIsFull = true;
                wait();
            } catch (InterruptedException ex) {
                queueIsFull = false;
            }
        }

        _msgQueue.addLast(msg);
        notifyAll();
    }
    private boolean queueIsFull = false;

    /**
     * Put a message in the head of the queue, so that it would be extracted the first. Do not care about the
     * basemain.DEFAULT_MAX_THREAD_QUEUE limit.
     * @param msg message.
     */
    public final synchronized void putInQueuePriority(MessageMsg msg) {
        _msgQueue.addFirst(msg);
        notifyAll();
    }

    public String getThreadName() {
        return threadName_;
    }

    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //                                  Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$ protected data ---$$$---$$$---$$$---$$$---$$$--

    /** The queue of the thread. It is very fast from the both ends since it is a cyclic buffer. */
    protected ArrayDeque<MessageMsg> _msgQueue = new ArrayDeque<>();

    //---$$$---$$$---$$$---$$$---$$$--- protected methods ---$$$---$$$---$$$---$$$---$$$---

    /**
     *      Message processing. Successors define its logic. This method is not to be syncrhonized, since it or its
     *  inheritors are always called from the thread running this object. No contention here.
     * @param msg message to process
     * @return true - message accepted, false - message is not recognized.
     */
    abstract protected boolean _messageProc(MessageMsg msg);

    /**
     * Wait until there is a message in the queue and extract if from the head of the queue.
     * @return extracted message or TimeoutMsg, if method exited on timeout.
     */
    protected synchronized MessageMsg _getBlocking() {
        // Wait
        while(_msgQueue.isEmpty()) try {
            if(timeout == 0)
                wait();
            else {
                wait(timeout, 0);
                if      // is timeout happened?
                        (_msgQueue.isEmpty())
                    return new TimeoutMsg();
            }
        } catch (InterruptedException ignored) {}

        // Extract
        MessageMsg msg = _msgQueue.pollFirst();

        // May be the putInQueue() method is waiting. Kick it.
        if
                (queueIsFull && _msgQueue.size() < maxQueueSize)
            notifyAll();

        return msg;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%--- private data ---%%%---%%%---%%%---%%%---%%%---%%%

    /** Timeout in miliseconds for the _getBlocking() method. If 0, wait indefinitely. */
    final private int timeout;

    /** Number of messages that can be put into the queue before the putInQueue() method blocks. */
    final private int maxQueueSize;

    final private String threadName_;

    //---%%%---%%%---%%%---%%%--- private methods ---%%%---%%%---%%%---%%%---%%%---%%%
}
