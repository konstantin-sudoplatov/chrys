package attention;

import chris.BaseMessage;
import chris.BaseMessageLoop;
import concepts.Concept;
import concepts.ConceptDirectory;
import console.Msg_ReadFromConsole;

/**
 *
 * @author su
 */
public class AttnBubbleLoop extends BaseMessageLoop {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** 
     * Constructor.
     * @param attnDisp attention dispatcher (parent).
     */ 
    public AttnBubbleLoop(AttnDispatcherLoop attnDisp) {
        this.attnDisp = attnDisp;
        caldroN = new AttnCaldronLoop();
        caldroN.start_thread();
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
        
        // terminate the caldron hierarchy
        Thread thread = caldroN.get_thread();
        if 
                (thread.isAlive())
        {
            try {
                caldroN.request_termination();
                thread.join();
            } catch (InterruptedException ex) {}
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

    /** Concept directory: a map of concepts by cids and some cids by names. Though it can be changed both from inside and 
     outside from the attention dispatcher, all changes would be from this thread (on our request), without concurrency. 
     Just in case don't use direct access to it from inside, use public methods. That way access can be easily synchronized. */
    private final ConceptDirectory cptDir = new ConceptDirectory();
    
    
    /** Attention dispatcher. Parent. */
    private AttnDispatcherLoop attnDisp;
    
    /** Main caldron of the bubble */
    private AttnCaldronLoop caldroN;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
