package console;

import chris.BaseMessage;

/**
 *  Ancestor for messages that are going to console loop. Messages are routed to the destination by their class.
 * @author su
 */
public abstract class ConsoleMessage extends BaseMessage {
    
    /** Originator of the message */
    public Class sender;
   
    //---***---***---***---***---***--- public классы ---***---***---***---***---***---***
    
    /**
     * Constructor.
     * @param sender Message loop that sends this message
     */
    public ConsoleMessage(Class sender) {
        this.sender = sender;
    }
}
