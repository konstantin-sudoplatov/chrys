package attention;

import chris.BaseMessage;

/**
 *  Ancestor for messages that are going to the attention dispatcher loop. Messages are routed to the destination by their class.
 * @author su
 */
public abstract class AttnDispMessage extends BaseMessage {

    /**
     * Constructor.
     */
    public AttnDispMessage() {
    }
    
    /**
     * Constructor.
     * @param handlerClass functor, that handles this message or null if it is meant to get handled by the message loop.
     */
    public AttnDispMessage(Class handlerClass) {
        super(handlerClass);
    }
}
