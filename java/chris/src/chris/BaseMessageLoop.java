package chris;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;

/**
 * Base message processing loop. Always ends up being a separate thread. 
 * @author su
 */
abstract public class BaseMessageLoop implements Runnable {
    
    /** If number of messages in the queue reaches the threshold, method put_in_queue() blocks waiting. */
    final static int QUEUE_THRESHOLD = 250;

    /**
     * Constructor.
     */
    public BaseMessageLoop() {
    }

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                            Методы внешнего интерфейса
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Start this loop as a thread.
     * @return the thread object
     */
    public Thread start_thread() {
        if
                (threaD == null)
        {
            threaD = new Thread(this);
            threaD.start();
        }
        else
            throw new Crash("Attempt to start a thread that is runnig already.");
        
        return threaD;
    }
    
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
        
        threaD = null;  // feel free to start again
    }   // run()

    /**
     * Shows if the queue is empty.
     * @return true/false
     */
    public synchronized  boolean queue_is_empty() {
        return msgQueue.isEmpty();
    }
    
    /**
     * Получить размер очереди сообщений.
     * @return
     */
    public synchronized int queue_size() {
        return msgQueue.size();
    }

    /**
     * Поместить сообщение в начало очереди. Для приоритетных сообщений.
     * @param сообщениеХризолита помещаемое в очередь сообщение.
     */
    public synchronized void put_in_the_head_of_queue(BaseMessage сообщениеХризолита) {
        msgQueue.addFirst(сообщениеХризолита);
        notifyAll();
    }

    /**
     * Поместить сообщение в конец очереди.
     * @param сообщениеХризолита помещаемое в очередь сообщение.
     */
    public synchronized void put_in_queue(BaseMessage сообщениеХризолита) {
        
        while (msgQueue.size() > QUEUE_THRESHOLD) {
            try {
                флагОжиданияПостановкиВОчередь = true;
                wait();
            } catch (InterruptedException ex) {
                флагОжиданияПостановкиВОчередь = false;
            }
        }

        msgQueue.addLast(сообщениеХризолита);
        notifyAll();
    }
    private boolean флагОжиданияПостановкиВОчередь = false;
    
    /**
     * Дождаться когда в очереди появится сообщение извлечь из начала очереди.
     * @return сообщение или null, если очередь пуста
     */
    public synchronized BaseMessage get_blocking() {
        // ждать появления сообщения
        while(msgQueue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException ex) {}
        }

        // Взять из очереди сообщение
        BaseMessage msg = msgQueue.pollFirst();

        // Возможно, метод put_in_queue ждет. Толкнуть его.
        if
                (флагОжиданияПостановкиВОчередь && msgQueue.size()<QUEUE_THRESHOLD)
            notifyAll();        

        return msg;
    }
    
    /**
     * Извлечь сообщение из начала очереди, если оно имеется.
     * @return сообщение или null, если очередь пуста
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
     * Getter.
     * @return the thread object.
     */
    public synchronized Thread get_thread() {
        return threaD;
    }

    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //                                  Наследуемый интерфейс
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$ protected переменные ---$$$---$$$---$$$---$$$---$$$--

    //---$$$---$$$---$$$---$$$---$$$--- protected методы ---$$$---$$$---$$$---$$$---$$$---

    /**
     * Hook after starting a thread.
     */
    protected void _afterStart_() {};

    /**
     * Специальная обработка потомка по завершению работы нити.
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
    //                               Внутренний интерфейс
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%--- private переменные ---%%%---%%%---%%%---%%%---%%%---%%%
    
    /** Thread object, that runs this loop. */
    private Thread threaD = null;
    
    /** Главная очередь сообщений обработчика. Очередь очень быстрая. Это кольцевой буфер. */
    private ArrayDeque<BaseMessage> msgQueue = new ArrayDeque<>();
    
    //---%%%---%%%---%%%---%%%---%%% private методы ---%%%---%%%---%%%---%%%---%%%---%%%--

    /**
     *  Вызвать функтор, для обработки сообщения, извлеченного из очереди.
     * <p>Функтор - это класс со статическим методом "обработать". Он, обычно,
     * содержится в сообщении в поле "Адресат".
     * @param message
     */
    private void invokeFunctor(BaseMessage message) {

        // выделить статический метод "обработать" функтора
        Method методОбработатьФунктора = null;      // сюда поместим объект, представляющий вызываемый метод
        try {
            методОбработатьФунктора = message.handler_class.getMethod("go",       // имя метода функтора, пришедшего в поле "Адресат" сообщения
                    new Class[] {       // типы параметров, которые будут передаваться методу
                        BaseMessage.class
                    }        
            );
        } catch (NoSuchMethodException ex) {
            throw new Crash("Error while invoking functor " + message.handler_class + ".go()", ex);
        }   // try   // try

        // вызвать его, передав ему как параметр пришедшее сообщение
        try {
            методОбработатьФунктора.invoke(
                    null,               // метод статическийобъект, нет объекта, которому он принадлежит
                    message         // сообщение, передаваемое методу в качестве параметра
            );
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new Crash("Error while invoking functor " + message.handler_class + ".go()", ex);
        }
    }   // void ВызватьФунктор()

    //---%%%---%%%---%%%---%%%---%%% private классы ---%%%---%%%---%%%---%%%---%%%---%%%--
    
}   // class ПетляСообщений
