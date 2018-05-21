package attention;

import chris.BaseMessage;
import chris.BaseMessageLoop;
import chris.Glob;
import console.ConsoleMessage;
import console.Msg_ReadFromConsole;
import java.util.ArrayList;

/**
 *
 * @author su
 */
public class AttnDispatcherLoop extends BaseMessageLoop {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** 
     * Constructor.     */ 
    public AttnDispatcherLoop() { 
    } 

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    @Override
    public void request_termination() {
        
        // terminate bubbles
        for (AttnBubbleLoop bubble : attnBubbleList) {
            bubble.request_termination();
        }
        
        // terminate yourself
        super.request_termination();
    }
    
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //      Protected    Protected    Protected    Protected    Protected    Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$--- protected data $$$---$$$---$$$---$$$---$$$---$$$--

    //---$$$---$$$---$$$---$$$---$$$--- protected methods ---$$$---$$$---$$$---$$$---$$$---

    @Override
    protected void _afterStart_() {
//Glob.app_loop.put_in_queue(new Msg_WriteToConsole(AttnDispatcherLoop.class, "hello\n"));        
        // prompt console
        Glob.app_loop.put_in_queue(new Msg_ReadFromConsole(AttnDispatcherLoop.class));        
    }

    @Override
    protected boolean _defaultProc_(BaseMessage msg) {
        if      // a message from console to bubble?
                (msg instanceof Msg_ConsoleToAttnBubble)
        {
//System.out.println("gotten \"" + ((Msg_ConsoleToAttnBubble)msg).text + "\" from console");
            
            // May be, create the chat bubble
            if
                    (consoleChatBubble == null)
            {
                consoleChatBubble = new AttnBubbleLoop(this);
                attnBubbleList.add(consoleChatBubble);
                consoleChatBubble.start_thread();
            }
            
            // route the message to the cat bubble
            consoleChatBubble.put_in_queue(msg);

            return true;
        }
        else if // a message to console (probably from a bubble)?
                (msg instanceof ConsoleMessage)
        {   // reroute to the application loop, it'll route it to console
            Glob.app_loop.put_in_queue(msg);
            return true;
        }
        
        return false;
    }
    
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%
    
    /** List of all attention bubbles */
    private ArrayList<AttnBubbleLoop> attnBubbleList = new ArrayList<>();
    
    /** Attention bubble, that chats with console. */
    private AttnBubbleLoop consoleChatBubble;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
    
}
