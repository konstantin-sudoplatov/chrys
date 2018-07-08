package attention;

import chris.BaseMessage;
import chris.BaseMessageLoop;
import chris.Crash;
import chris.Glob;
import concepts.Concept;
import concepts.DynCptName;
import concepts.dyn.Neuron;
import java.util.HashMap;
import java.util.Map;

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
     * Get local concept by cid, may be load it initially. In the root of hierarchy (class AttnCircle) this method is overriden.
     * This method is synchronized since it can be concurrently called from different branches of the caldron tree.
     * @param cid
     * @return the concept
     * @throws Crash if not found
     */
    @Override
    public synchronized Concept get_cpt(long cid) {
        Concept cpt = _cptDir_.get(cid);
        if      // no such concept in the local directory?
                (cpt == null)
        {   // get it from parent, put in local directory and return
            //  In the root of hierarchy (class AttnCircle) the processing never gets here, because we are overriden. So, here
            // we don't check them for a global or static
            cpt = parenT.get_cpt(cid).clone();     
            _cptDir_.put(cid, cpt);
            return cpt;
        }
        else {// concept found, return it
            return cpt; 
        }
    }
    
    /**
     * Get a local concept by name, may be load it initially.
     * This method is synchronized since it can be concurrently called from different branches of the caldron tree.
     * @param cptName
     * @return the concept
     * @throws Crash if not found
     */
    @Override
    public synchronized Concept get_cpt(String cptName) {
        Long cid = Glob.named.name_cid.get(cptName);
        if (cid != null) 
            return get_cpt(cid);
        else 
            throw new Crash("Now such concept: name = " + cptName);
    }
    
    @Override
    public synchronized boolean cpt_exists(long cid) {
        if      // does it exist locally?
                (_cptDir_.containsKey(cid))
            // yes
            return true;
        else // find in predecessors. We never get here if this caldron is the attention circle,
            // since this method is overriden.
            return parenT.cpt_exists(cid);
    }
    
    @Override
    public synchronized boolean cpt_exists(String cptName) {
        Long cid = Glob.named.name_cid.get(cptName);
        if (cid != null)
            return Caldron.this.cpt_exists(cid);
        else
            return false;
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
     * Raise flag requestStopReasoning.
     */
    public void request_stop_reasoning() {
        requestStopReasoning = true;
    }
    
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //      Protected    Protected    Protected    Protected    Protected    Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$--- protected data $$$---$$$---$$$---$$$---$$$---$$$--

    /** Local concept directory. */
    protected final Map<Long, Concept> _cptDir_ = new HashMap();
    
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
//Glob.println(Glob.here_count++, "here_count");
//Glob.println(get_cpt(_head_), "head before actions", 10);
//Glob.println(get_cpt(DynCptName.chat_media_prem.name()), "_reasoning_()", 2);
            // Do the assessment
            long[] heads = ((Neuron)get_cpt(_head_)).calculate_activation_and_do_actions(this);

            // May be we have to wait
            if      //no new head?
                    (requestStopReasoning || heads == null || heads.length == 0)
            {   // finish the reasoning
                requestStopReasoning = false;
                break;
            }
            
            // get new head
            _head_ = heads[0];
//Glob.print("heads", heads, 6);
//Glob.println(get_cpt(_head_), "head after actions");
            
            // create new caldrons for the rest of the heads
            for(int i=1; i<heads.length; i++) {
                Thread thread = new Thread(this);
                Glob.append_array(childreN, thread);
                thread.start();
            }
        }
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
                (msg instanceof Msg_DoReasoningOnCaldron)
        {
            _reasoning_();
            return true;
        }
        else 
            return false;
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
    
    /** If this flag raised, reasoning will stop and caldron will wait on the neuron. */
    private boolean requestStopReasoning;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--
    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
