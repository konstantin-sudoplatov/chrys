package attention;

import chris.BaseMessage;
import chris.Crash;
import chris.Glob;
import concepts.Concept;
import concepts.DynCptName;
import concepts.dyn.Neuron;
import concepts.dyn.premises.String_prem;
import java.util.List;

/**
 * Attention bubble loop. Works as a main caldron, can contain subcaldrons.
 * @author su
 */
public class AttnCircle extends Caldron implements ConceptNameSpace {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** 
     * Constructor.
     * @param attnDisp attention dispatcher (parent).
     * @param circleType example: DynCptName.it_is_console_chat_prem
     */ 
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public AttnCircle(AttnDispatcherLoop attnDisp, DynCptName circleType) 
    {   super(null, null);    // null for being a main caldron
        this.attnDisp = attnDisp;
    } 

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Get a local concept by cid, may be load it initially.
     * @param cid
     * @return the concept
     * @throws Crash if not found
     */
    @Override
    public synchronized Concept get_cpt(long cid) {
        
        if      // is it a static concept? get it from the common directory
                (cid >= 0 && cid <= Glob.MAX_STATIC_CID)
            return attnDisp.get_cpt(cid);
            
        Concept cpt = _cptDir_.get(cid);
        if      // found in the local concept directory?
                (cpt != null)
            // yes: return the concept
            return cpt;
        else {  // no: load the concept from the common directory and return it
            attnDisp.copy_cpt_to_circle(cid, this);
            return _cptDir_.get(cid);
        }
    }

    /** 
     * Test if the concept directory contains a concept. Called from attention dispatcher.
     * @param cid
     * @return true/false
     */
    public synchronized boolean concept_directory_containsKey(long cid) {
        return _cptDir_.containsKey(cid);
    }
    
    /**
     * Put new concept into the concept directory. Called from attention dispatcher.
     * @param cid
     * @param cpt 
     */
    public synchronized void put_in_concept_directory(long cid, Concept cpt) {
        _cptDir_.put(cid, cpt);
    }

    /**
     * Getter.
     * @return  
     */
    public AttnDispatcherLoop get_attn_dispatcher () {
        return attnDisp;
    }
    
    @Override
    public synchronized void request_termination() {
        
        // terminate the caldron hierarchy, if exists
        if
                (caldronList != null)
        {
            for(Caldron caldron : caldronList)
                if 
                        (caldron.isAlive())
                {
                    try {
                        caldron.request_termination();
                        caldron.join();
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
        
        // May be process the message in the ancesstor
        if      
                (super._defaultProc_(msg))
            return true;

        if      // a line from console has come?
                (msg instanceof Msg_ConsoleToAttnCircle)
        {   // put it to the concept "line_of_chat_string_prem" and invoke the reasoning
            
            // Check if the caldron initialized
            if      // is it the first message?
                    (_head_ == 0)
            {   //yes: grow the seed
                Neuron seed = (Neuron)get_cpt(DynCptName.console_chat_seed_nrn.name());
                _head_ = seed.get_cid();
                
                _reasoning_();
            }
            
            String_prem lineOfChat = (String_prem)get_cpt(DynCptName.line_of_chat_string_prem.name());
            lineOfChat.set_text(((Msg_ConsoleToAttnCircle) msg).text);
            lineOfChat.set_activation(1);
            
            _reasoning_();
            
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
    
    /** Attention dispatcher. Parent. */
    private final AttnDispatcherLoop attnDisp;
    
    /** Possible set of child caldrons . */
    private List<Caldron> caldronList;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--
    
    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
