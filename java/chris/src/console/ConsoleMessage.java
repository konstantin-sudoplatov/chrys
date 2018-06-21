package console;

import chris.BaseMessage;
import chris.BaseMessageLoop;

/**
 *  Ancestor for messages that are going to console loop. Messages are routed to the destination by their class.
 * @author su
 */
public abstract class ConsoleMessage extends BaseMessage {
    
    /** Originator of the message */
//    public BaseMessageLoop sender;
   
    //---***---***---***---***---***--- public классы ---***---***---***---***---***---***
    
//    /**
//     * Constructor.
//     * @param sender Message loop that sends this message
//     */
//    public ConsoleMessage(BaseMessageLoop sender) {
//        this.sender = sender;
//    }
}
