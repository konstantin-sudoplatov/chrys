package attention;

import chris.BaseMessage;
import chris.BaseMessageLoop;
import concepts.Concept;
import console.Msg_ReadFromConsole;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author su
 */
public class AttnBubbleLoop extends BaseMessageLoop {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** 
     * Constructor.
     * @param attnDisp attention dispatcher
     */ 
    public AttnBubbleLoop(AttnDispatcherLoop attnDisp) {
        this.attnDisp = attnDisp;
    } 

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /** 
     * Test if the concept directory contains a concept.
     * @param cid
     * @return true/false
     */
    public boolean cptdir_containsKey(long cid) {
        return cptDir.containsKey(cid);
    }
    
    /**
     * Put new concept into the concept directory.
     * @param cid
     * @param cpt 
     */
    public void cptdir_put(long cid, Concept cpt) {
        cptDir.put(cid, cpt);
    }

    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //      Protected    Protected    Protected    Protected    Protected    Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //---$$$---$$$---$$$---$$$---$$$--- protected data $$$---$$$---$$$---$$$---$$$---$$$--
    //---$$$---$$$---$$$---$$$---$$$--- protected methods ---$$$---$$$---$$$---$$$---$$$---
    @Override
    protected boolean _defaultProc_(BaseMessage msg) {
        //System.out.println("gotten \"" + ((Msg_ConsoleToAttnBubble)msg).text + "\" from console");
            
        // prompt console
        attnDisp.put_in_queue(new Msg_ReadFromConsole(AttnDispatcherLoop.class));        
        
        return true;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%

    /** Concept directory: a map of concepts by cid's. Though it can be changed both from inside and 
     outside from the attention dispatcher, all changes would be from this thread (on our request), without concurrency. 
     Just in case don't use direct access to it from inside, use public methods. That way access can be easily synchronized. */
    private final Map<Long, Concept> cptDir = new ConcurrentHashMap<>();
    
    
    /** Attention dispatcher. Parent. */
    AttnDispatcherLoop attnDisp;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
