package base_thread;

import java.util.ArrayDeque;
import static base.Base_dataKt.MAX_THREAD_QUEUE_SIZE;
import static base.Base_funcsKt.logit;

/**
 * Base class for message driven concurrency. It contains a message queue (cyclic buffer), access to which is synchronized
 * and methods for sending messages to it and taking them out. All message processing is fulfilled in the descendants.
 * This class extends the Thread class, not just implements the runnable interface, since it gets us access to the
 * Thread class, for example we would be able to use base_thread.currentThread().
 */
public class ThreadQueue extends Thread {

    /**
     *      Constructor.
     * @param timeoutMsecs timeout for waiting new messages in the queue. If timeout happens new message TimeoutMsg is
     *                     generated and sent for processing. 0 disables the timeout.
     */
    public ThreadQueue(int timeoutMsecs) {
        timeout = timeoutMsecs;
    }

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                            Public
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Main cycle of taking out and processing messages from the queue.
     */
    @Override
    public void run() {
        while(true) {
            MessageMsg msg = getBlocking_();
            if (!_messageProc(msg))
                logit("Unexpected message in " + this.getClass().getName());

            if      // is termination requested?
            (msg instanceof RequestTerminationMsg)
                break;
        }
    }

    /**
     * Shows if the queue is empty.
     * @return true/false
     */
    public synchronized  boolean empty() {
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

        while (_msgQueue.size() >= MAX_THREAD_QUEUE_SIZE) {
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
     * MAX_THREAD_QUEUE_SIZE limit.
     * @param msg message.
     */
    public final synchronized void putInQueuePriority(MessageMsg msg) {
        _msgQueue.addFirst(msg);
        notifyAll();
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
     *  This method is not abstract since we want to create the object in JUnit tests.
     * @param msg message to process
     * @return true - message accepted, false - message is not recognized.
     */
    protected boolean _messageProc(MessageMsg msg) {
        return true;
    }

    /**
     * Pass through to the getBlocking_(), used by JUnit tests.
     * @return xtracted message or TimeoutMsg, if method exited on timeout.
     */
    protected MessageMsg _getBlocking() {
        return getBlocking_();
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%--- private data ---%%%---%%%---%%%---%%%---%%%---%%%

    /** Timeout in miliseconds for the getBlocking_() method. If 0, wait indefinitely. */
    final private int timeout;

    //---%%%---%%%---%%%---%%%--- private methods ---%%%---%%%---%%%---%%%---%%%---%%%

    /**
     * Wait until there is a message in the queue and extract if from the head of the queue.
     * @return extracted message or TimeoutMsg, if method exited on timeout.
     */
    private synchronized MessageMsg getBlocking_() {
        // Wait
        while(_msgQueue.isEmpty()) try {
            if(timeout == 0)
                wait();
            else {
                wait(timeout, 0);
                assert _msgQueue.isEmpty(): "" + size() + " messages in queue. Someone forgot to notify us.";
                return new TimeoutMsg();
            }
        } catch (InterruptedException ignored) {}

        // Extract
        MessageMsg msg = _msgQueue.pollFirst();

        // May be the putInQueue() method is waiting. Kick it.
        if
                (queueIsFull && _msgQueue.size() < MAX_THREAD_QUEUE_SIZE)
            notifyAll();

        return msg;
    }
}
