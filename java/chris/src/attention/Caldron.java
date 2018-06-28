package attention;

import chris.BaseMessageLoop;
import chris.Crash;
import chris.Glob;
import concepts.Concept;
import concepts.dyn.ifaces.EffectIface;
import concepts.dyn.ifaces.EvaluationIface;

/**
 * The reasoning is taking place in a caldron. Caldrons are organized in a hierarchy.
 * The main caldron is the attention bubble, it can contain subcaldrons.
 * @author su
 */
abstract public class Caldron extends BaseMessageLoop implements ConceptNameSpace {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** 
     * Constructor.
     * @param parent parent caldron. Null for main caldron, which is supposed to be an attention bubble loop.
     * @param attnCircle attention circle as a root of the caldron tree
     */ 
    public Caldron(Caldron parent, AttnCircle attnCircle) 
    {   super();
        parenT = parent;
        this.attnCircle = attnCircle;
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
        return parenT.get_cpt(cid);
    }
    
    /**
     * Get a local concept by name, may be load it initially.
     * @param cptName
     * @return the concept
     * @throws Crash if not found
     */
    @Override
    public synchronized Concept get_cpt(String cptName) {
        return parenT.get_cpt(cptName);
    }

    @Override
    public AttnCircle get_attn_circle() {
        if      // is this caldron an attention circle?
                (attnCircle == null)
            return (AttnCircle)this;
        else
            return attnCircle;
    }
    
    /**
     * Raise the stopReasoningRequested flag to make this caldron thread wait on its _head_ concept.
     */
    public synchronized void request_stop_reasoning() {
        stopReasoningRequested = true;
    }
    
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //      Protected    Protected    Protected    Protected    Protected    Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$--- protected data $$$---$$$---$$$---$$$---$$$---$$$--
    
    /** The dynamic concept, currently doing assertion. */
    protected long _head_;

    //---$$$---$$$---$$$---$$$---$$$--- protected methods ---$$$---$$$---$$$---$$$---$$$---
    
    /**
     * Does the cycle of assessments. When the assessment chain cannot be continued, for it has to wait for something, results
     * from other caldrons or a reaction of the chatter for example, this function returns and this loop goes to processing
     * the events or waits if the event queue is empty.
     */
    protected void _reasoning_() {
        while(true) {
            
            // Do the assessment
            ((EvaluationIface)get_cpt(_head_)).calculate_activation_and_do_actions(this);
            
            // Check if in the course of assessment (one of the actions of the _head_ concept)
            // had been raised the stoppage flag
            if      //do we have to wait?
                    (stopReasoningRequested)
            {   // finish the reasoning
                stopReasoningRequested = false;
                break;
            }
            
            // get new head
            long[] heads = ((EffectIface)get_cpt(_head_)).get_effects();
            if
                    (heads == null || heads.length == 0)
                throw new Crash("The head of the caldron neuron " + _head_ + " has no effects.");
            _head_ = heads[0];
            
            // create new caldrons for the rest of the heads
            for(int i=1; i<heads.length; i++) {
                Thread thread = new Thread(this);
                Glob.append_array(childreN, thread);
                thread.start();
            }
        }
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%

    /** Parent caldron. null if it is the attention circle. */
    private final Caldron parenT;

    /** The root of the caldron tree. null if it is the attention circle. */
    private final AttnCircle attnCircle;
    
    /** Children caldrons. */
    private Caldron[] childreN;
    
    /**  */
    private boolean stopReasoningRequested;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--
    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
