package attention;

import chris.BaseMessage;
import chris.BaseMessageLoop;
import chris.Glob;
import concept.Concept;
import console.ConsoleMessage;
import console.Msg_ReadFromConsole;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

    /**
     * Add concept to comdir or to a bubble dir. Static concepts know their cid already, for dynamic concept it is randomly 
     * generated outside the static range.
     * @param cpt the concept to add
     * @param bubble the bubble, that contains the target directory or null if it is the comdir.
     * @return cid
     */
    @SuppressWarnings("UnnecessaryLabelOnContinueStatement")
    public synchronized long add_cpt(Concept cpt, AttnBubbleLoop bubble) {
        // determine cid
        long cid;
        if      
                (cpt.is_static())
            cid = cpt.getCid();
        else {   // generate a unique cid. it is unique to cpt and all privDir's.
            GENERATE_CID: while(true) {
                Random rnd = new Random();
                cid = rnd.nextLong();
                if      // cid in the static range? push it out
                        (cid >= 0 && cid <= Glob.MAX_STATIC_CID)
                    cid += Glob.MAX_STATIC_CID + 1;
                if      // is in cpt?
                        (comConDir.containsKey(cid))
                    continue GENERATE_CID;   // generate once more
                for(AttnBubbleLoop b: attnBubbleList) {
                    if      // is in PrivDir?
                            (b.cptdir_containsKey(cid))
                        continue GENERATE_CID;
                }
                break;
            }   // while
        }
        
        // put to target dir
        cpt.setCid(cid);
        if
                (bubble == null)
            comConDir.put(cid, cpt);      // put in cpt
        else
            bubble.cptdir_put(cid, cpt);
        
        return cid;
    }

    /**
     * Ditto.
     * @param cpt  the concept to add
     */
    public synchronized void add_cpt(Concept cpt) {
        add_cpt(cpt, null);
    }
        
    /**
     *  Check to see if the common concept map contains a concept.
     * @param cid 
     * @return
     */
    public static synchronized boolean comdir_containsKey(long cid) {
        return comConDir.containsKey(cid);
    }

    @Override
    public synchronized void request_termination() {
        
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

        // prompt console
        Glob.app_loop.put_in_queue(new Msg_ReadFromConsole(AttnDispatcherLoop.class));        
    }

    @Override
    protected boolean _defaultProc_(BaseMessage msg) {
        if      // a message from console to bubble?
                (msg instanceof Msg_ConsoleToAttnBubble)
        {
            // May be, create the chat bubble
            if
                    (consoleChatBubble == null)
            {
                consoleChatBubble = new AttnBubbleLoop(this);
                addBubbleToList(consoleChatBubble);
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

    /** Common concept directory: a map of concepts by cid's. */
    private static final Map<Long, Concept> comConDir = new HashMap<>();
    
    /** List of all attention bubbles */
    private ArrayList<AttnBubbleLoop> attnBubbleList = new ArrayList<>();
    
    /** Attention bubble, that chats with console. */
    private AttnBubbleLoop consoleChatBubble;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    /**
     * Synchronized adding new bubble to the list of bubbles. We need synchronization if someone from outside the thread wants
     * to work with the list of bubbles (for example, wants to find a bubble to add a new concept to it).
     * @param bubble attention bubble loop to add to the list.
     */
    private synchronized void addBubbleToList(AttnBubbleLoop bubble) {
        attnBubbleList.add(consoleChatBubble);
    }
    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
    
}
