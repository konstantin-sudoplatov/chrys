package chris;

/**
 *  Ancestor of all messages. Messages are put to a queue of a massage loop object, a successor of BaseMessageLoop, for example
 * the application loop, where they get processed.
 * @author su
 */
public abstract class BaseMessage {
    
    /** 
     * Class of a functor-handler. It can be null, in which case the message is handled by the message
     * loop, to which this message is sent.
     */
    public Class handler_class;
   
    //---***---***---***---***---***--- public классы ---***---***---***---***---***---***
    
    /**
     * Constructor.
     */
    public BaseMessage() {}
    
    /**
     * Constructor.
     * @param handlerClass functor, that handles this message or null if it is meant to get handled by the message loop.
     */
    public BaseMessage(Class handlerClass) {
        this.handler_class = handlerClass;
    }
}
