package message;

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
    
    public Object sender;
   
    //---***---***---***---***---***--- public классы ---***---***---***---***---***---***
    
    /**
     * Constructor.
     * @param handlerClass functor, that handles this message or null if it is meant to get handled by the message loop.
     * @param sender sender, can be null if the handler is not interested in the originator identity.
     */
    public BaseMessage(Class handlerClass, Object sender) {
        this.handler_class = handlerClass;
        this.sender = sender;
    }
   
    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                            Методы внешнего интерфейса
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Setter.
     * @param handlerClass functor, that handles this message or null if it is meant to get handled by the message loop.
     */
    public void set_handler_class(Class handlerClass) {
        this.handler_class = handlerClass;
    }
}
