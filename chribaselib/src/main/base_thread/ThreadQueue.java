package base_thread;

import java.util.ArrayDeque;
import static chribaselib.Base_dataKt.MAX_THREAD_QUEUE_SIZE;

/**
 * Base class for message driven concurrency. It contains a message queue (cyclic buffer), access to which is synchronized
 * and methods for sending messages to it and taking them out. All message processing is fulfilled in the descendants.
 * This class extends the Thread class, not just implements the runnable interface, since it gets us access to the
 * Thread class, for example we would be able to use base_thread.currentThread().
 */
public class ThreadQueue extends Thread {

    /** Constructor. */
    public ThreadQueue() {
    }

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                            Public
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Shows if the queue is empty.
     * @return true/false
     */
    public synchronized  boolean queue_is_empty() {
        return msgQueue.isEmpty();
    }

    /**
     * Get the size of the queue.
     * @return
     */
    public synchronized int queue_size() {
        return msgQueue.size();
    }

    /**
     * Put the message in the tail of the queue.
     * @param msg message
     */
    public final synchronized void put_in_queue(BaseMessage msg) {

        while (msgQueue.size() > MAX_THREAD_QUEUE_SIZE) {
            try {
                queueIsFull = true;
                wait();
            } catch (InterruptedException ex) {
                queueIsFull = false;
            }
        }

        msgQueue.addLast(msg);
        notifyAll();
    }
    private boolean queueIsFull = false;

    /**
     * Wait until there is a message in the queue and extract if from the head of the queue.
     * @return extracted message
     */
    public synchronized BaseMessage get_blocking() {
        // Wait
        while(msgQueue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException ex) {}
        }

        // Extract
        BaseMessage msg = msgQueue.pollFirst();

        // May be the put_in_queue() method is waiting. Kick it.
        if
                (queueIsFull && msgQueue.size() < MAX_THREAD_QUEUE_SIZE)
            notifyAll();

        return msg;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%--- private data ---%%%---%%%---%%%---%%%---%%%---%%%

    /** The queue of the thread. It is very fast from the both ends since it is a cyclic buffer. */
    private ArrayDeque<BaseMessage> msgQueue = new ArrayDeque<>();
}
