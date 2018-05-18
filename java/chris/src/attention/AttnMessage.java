package attention;

/**
 *  Ancestor for messages that are going to attention bubble loops. Messages are routed to the destination by their class.
 * This class descends from the AttnDispMessage because it is routed to an attention bubble by attention dispatcher. The tree
 * of descent goes after the tree of routing from master loop to attention loop to attention bubble loop. That way it can 
 * be easily routed to destination from any level of loops.
 * @author su
 */
public abstract class AttnMessage extends AttnDispMessage {
   
    //---***---***---***---***---***--- public классы ---***---***---***---***---***---***
    
    /**
     * Constructor.
     */
    public AttnMessage() {}
    
    /**
     * Constructor.
     * @param handlerClass functor, that handles this message or null if it is meant to get handled by the message loop.
     */
    public AttnMessage(Class handlerClass) {
        super(handlerClass);
    }
}
