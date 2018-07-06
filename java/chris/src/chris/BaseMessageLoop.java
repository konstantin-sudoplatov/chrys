package chris;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.List;

/**
 * Base message processing loop. Always ends up being a separate thread. 
 * @author su
 */
abstract public class BaseMessageLoop extends Thread {
    
    /** If number of messages in the queue reaches the threshold, method put_in_queue() blocks waiting. */
    final static int QUEUE_THRESHOLD = 250;

    /**
     * Constructor.
     */
    public BaseMessageLoop() {
    }

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                            Public
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^
    
    /**
     * Main cycle of taking out and processing messages in the queue.
     */
    @Override
    public void run() {

        _afterStart_();

        while(true) {
            
            // get a new message from the queue or wait if the queue is empty
            BaseMessage msg = get_blocking();

            // may be terminate the thread
            if      // is it a termination request?
                    (msg instanceof Msg_LoopTermination)
            {
                _beforeTermination_();
                break;
            }
            
            // may be we need to route this message to another message loop
            BaseMessageLoop nextHop = _nextHop_(msg);
            if      // this message is targeted to another loop?
                    (nextHop != null)
            {   // yes: send it over there
                nextHop.put_in_queue(msg);
                continue;
            }
            
            // process the message
            if      // no handler functor?
                    (msg.handler_class==null)
            {   // do default
                _defaultProc_(msg);
            }
            else   // else, invoke the functor
                try {
                    invokeFunctor(msg);
                } catch (Crash ex) {
                    Logger.getLogger(BaseMessageLoop.class.getName()).log(Level.SEVERE,
                            "Error invoking message handling functor: " + msg.getClass().getName(), ex);
                }
        }
    }   // run()

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
     * Put a message in the head of the queue, so that it would be extracted the first.
     * @param msg message.
     */
    public synchronized void put_in_queue_with_priority(BaseMessage msg) {
        msgQueue.addFirst(msg);
        notifyAll();
    }

    /**
     * Put the message in the tail of the queue.
     * @param msg message
     */
    public synchronized void put_in_queue(BaseMessage msg) {
        
        while (msgQueue.size() > QUEUE_THRESHOLD) {
            try {
                queueBusyFlag = true;
                wait();
            } catch (InterruptedException ex) {
                queueBusyFlag = false;
            }
        }

        msgQueue.addLast(msg);
        notifyAll();
    }
    private boolean queueBusyFlag = false;
    
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
                (queueBusyFlag && msgQueue.size()<QUEUE_THRESHOLD)
            notifyAll();        

        return msg;
    }
    
    /**
     * Extract a message from the head of the queue without waiting. Don't know if I ever need it.
     * @return the message or null, if the queue is empty
     */
    public synchronized BaseMessage get_nonblocking() {
        return msgQueue.isEmpty()? null: msgQueue.pollFirst();
    }

    /**
     * Request terminating this thread.
     */
    public synchronized void request_termination() {
        put_in_queue(new Msg_LoopTermination());
    }

    /**
     * Create list of lines, which shows the object's content. For debugging. Invoked from Glob.print().
     * @param note printed in the first line just after the object type.
     * @param debugLevel 0 - the shortest, 2 - the fullest
     * @return list of lines, describing this object.
     */
    public List<String> to_list_of_lines(String note, Integer debugLevel) {
        List<String> lst = Glob.create_list_of_lines(this, note, debugLevel);
        if (debugLevel < 0)
            return lst;
        else if (debugLevel == 0 ) {
            Glob.append_last_line(lst, String.format("msgQueue.size() = %s", msgQueue.size()));
        }
        else {
            Glob.add_list_of_lines(lst, "msgQueue[]", msgQueue.toArray(), debugLevel-1);
        }

        return lst;
    }
    public List<String> to_list_of_lines() {
        return to_list_of_lines("", 20);
    }

    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //                                  Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$ protected data ---$$$---$$$---$$$---$$$---$$$--

    //---$$$---$$$---$$$---$$$---$$$--- protected methods ---$$$---$$$---$$$---$$$---$$$---

    /**
     * Hook after starting a thread.
     */
    protected void _afterStart_() {};

    /**
     * Optional processing on the termination of the thread.
     */
    protected void _beforeTermination_() {};
    
    /**
     * Determine if and to which loop we have to route a message.
     * @param msg message to be routed
     * @return message loop we have to be routed to or null if the message is targeted to this loop.
     */
    protected BaseMessageLoop _nextHop_(BaseMessage msg) {
        return null;
    }
    
    /**
     * Default message processing. It is invoked in case when the handler functor is null. It must be synchronized in successors
     * in order to avoid possible contention race for local variables between our thread and external calls of other synchronized methods.
     * @param msg message to process
     * @return true - message accepted, false - message is not recognized.
     */
    abstract protected boolean _defaultProc_(BaseMessage msg);
    
    //---$$$---$$$---$$$---$$$---$$$--- protected классы ---$$$---$$$---$$$---$$$---$$$---

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%--- private data ---%%%---%%%---%%%---%%%---%%%---%%%
    
    /** The queue of the loop. It is very fast from the both ends since it is a cyclic buffer. */
    private ArrayDeque<BaseMessage> msgQueue = new ArrayDeque<>();
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    /**
     *  Call a functor to process the message from the queue.
     * <p>Functor - this is an object with a static method go(). The message may have its address, then it gets in here,
     * else it is get processed in the _defaultProc_() method.
     * @param message
     */
    private void invokeFunctor(BaseMessage message) {

        // Extract the static method go() of the functor
        Method methodGo = null;
        try {
            methodGo = message.handler_class.getMethod("go",       // name of the method
                    new Class[] {       // parameter types
                        BaseMessage.class
                    }        
            );
        } catch (NoSuchMethodException ex) {
            throw new Crash("Error while invoking functor " + message.handler_class + ".go()", ex);
        }   // try   // try

        // Call it
        try {
            methodGo.invoke(
                    null,           // the method is static, there is no object it belongs to
                    message         // the message as a parameter
            );
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new Crash("Error while invoking functor " + message.handler_class + ".go()", ex);
        }
    }
}
