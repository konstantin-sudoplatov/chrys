package console;

import master.MasterMessage;

/**
 *  Ancestor for messages that are going to console loop. Messages are routed to the destination by their class.
 * @author su
 */
public abstract class ConsoleMessage extends MasterMessage {
    
    /** Originator of the message */
    Class sender;
   
    //---***---***---***---***---***--- public классы ---***---***---***---***---***---***
    
    /**
     * Constructor.
     * @param sender Message loop that sends this message
     */
    public ConsoleMessage(Class sender) {
        this.sender = sender;
    }
}
