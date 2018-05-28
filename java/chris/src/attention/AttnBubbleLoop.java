package attention;

import chris.BaseMessage;
import concepts.Concept;
import concepts.ConceptDirectory;
import java.util.List;

/**
 * Attention bubble loop. Works as a main caldron, can contain subcaldrons.
 * @author su
 */
public class AttnBubbleLoop extends BaseCaldronLoop {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** 
     * Constructor.
     * @param attnDisp attention dispatcher (parent).
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
    public synchronized boolean cpt_dir_containsKey(long cid) {
        return cptDir.cpt_dir.containsKey(cid);
    }
    
    /**
     * Put new concept into the concept directory.
     * @param cid
     * @param cpt 
     */
    public synchronized void cpt_dir_put(long cid, Concept cpt) {
        cptDir.cpt_dir.put(cid, cpt);
    }

    /** 
     * Test if the name directory contains a name of concept.
     * @param name
     * @return true/false
     */
    public synchronized boolean name_dir_containsKey(String name) {
        return cptDir.name_dir.containsKey(name);
    }
    
    /**
     * Put new concept into the concept directory.
     * @param name
     * @param cid
     */
    public synchronized void name_dir_put(String name, long cid) {
        cptDir.name_dir.put(name, cid);
    }

    @Override
    public synchronized void request_termination() {
        
        // terminate the caldron hierarchy, if exists
        if
                (caldronList != null)
        {
            for(CaldronLoop caldron : caldronList)
                if 
                        (caldron.get_thread().isAlive())
                {
                    try {
                        caldron.request_termination();
                        caldron.get_thread().join();
                    } catch (InterruptedException ex) {}
                }
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
    synchronized protected boolean _defaultProc_(BaseMessage msg) {

        if
                (msg instanceof Msg_ConsoleToAttnBubble)
        {
            // May be, initialize the bubble on the first message from console
            if      // hasn't the brewing started yet?
                    (_curAssert_ == null)
            {   //no: start it
                startBrewing(msg);  
            }
            
            return true;
        }
            
        // prompt console
//        attnDisp.put_in_queue(new Msg_ReadFromConsole(AttnDispatcherLoop.class));        
        
        return false;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%

    /** Concept directory: a map of concepts by cids and some cids by names. Though it can be changed both from inside and 
     outside from the attention dispatcher, all changes would be from this thread (on our request), without concurrency. 
     Just in case don't use direct access to it from inside, use public methods. That way access can be easily synchronized. */
    private final ConceptDirectory cptDir = new ConceptDirectory();
    
    
    /** Attention dispatcher. Parent. */
    private final AttnDispatcherLoop attnDisp;
    
    /** Possible set of child caldrons . */
    private List<CaldronLoop> caldronList;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    /**
     * Initialize a chat on the first message from a chatter.
     * @param msg 
     */
    private void startBrewing(BaseMessage msg) {
        
    }
    
    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
