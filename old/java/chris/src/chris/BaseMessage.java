package chris;

import java.util.List;

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
        else if (debugLevel >= 0 ) {
            Glob.append_last_line(lst, String.format("handler_class = %s", handler_class));
        }

        return lst;
    }
    public List<String> to_list_of_lines() {
        return to_list_of_lines("", 20);
    }
}
