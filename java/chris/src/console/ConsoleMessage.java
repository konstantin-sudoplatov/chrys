package console;

import master.MasterMessage;

/**
 *  Ancestor for messages that are going to console loop. Messages are routed to the destination by their class.
 * @author su
 */
public abstract class ConsoleMessage extends MasterMessage {
   
    //---***---***---***---***---***--- public классы ---***---***---***---***---***---***
    
    /**
     * Constructor.
     */
    public ConsoleMessage() {}
    
    /**
     * Constructor.
     * @param handlerClass functor, that handles this message or null if it is meant to get handled by the message loop.
     */
    public ConsoleMessage(Class handlerClass) {
        super(handlerClass);
    }
}
