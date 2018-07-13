package attention;

import chris.BaseMessage;
import chris.BaseMessageLoop;
import chris.Crash;
import chris.Glob;
import concepts.Concept;
import concepts.StatCptName;
import concepts.StaticAction;
import concepts.dyn.Neuron;
import java.util.HashMap;
import java.util.Map;

/**
 * The reasoning is taking place in a caldron. Caldrons are organized in a hierarchy.
 * The main caldron is the attention bubble, it can contain subcaldrons.
 * @author su
 */
public class Caldron extends BaseMessageLoop implements ConceptNameSpace {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** 
     * Constructor.
     * @param seed main seed of the caldron. It serves as caldron's identifier.
     * @param parent parent caldron. Null for main caldron, which is supposed to be an attention bubble loop.
     * @param attnCircle attention circle as a root of the caldron tree
     */ 
    @SuppressWarnings("LeakingThisInConstructor")
    public Caldron(Neuron seed, Caldron parent, AttnCircle attnCircle) {
        super();
        seeD = seed;
        _head_ = seed.get_cid();
        parenT = parent;
        this.attnCircle = attnCircle;
        if      // is it an ordinary caldron (not an attention circle)?
                (!(this instanceof AttnCircle))
            //yes: put itself into the caldron map
            get_attn_circle().get_attn_dispatcher().put_caldron(seed, this);
        else {}//no: do nothing, since the dispatcher is unknown yet. We will do it in the descendant.

        if      //is am I me?
            (this.getClass() == Caldron.class)
        //yes: constructor finished, kick the reasoning
        this.put_in_queue_with_priority(new Msg_DoReasoningOnBranch());    // put ahead of the possible console lines
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
    public synchronized Concept load_cpt(long cid) {
        Concept cpt = _cptDir_.get(cid);
        if      // no such concept in the local directory?
                (cpt == null)
        {   // get it from parent, put in local directory and return
            //  In the root of hierarchy (class AttnCircle) the processing never gets here, because we are overriden. So, here
            // we don't check them for a global or static
            cpt = parenT.load_cpt(cid).clone();  
            cpt.set_name_space(this);
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
    public synchronized Concept load_cpt(String cptName) {
        Long cid = Glob.named.name_cid.get(cptName);
        if (cid != null) 
            return load_cpt(cid);
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
    public final synchronized AttnCircle get_attn_circle() {
        if      // is this caldron an attention circle?
                (this instanceof AttnCircle)
            return (AttnCircle)this;
        else
            return attnCircle;
    }
    
    /**
     * Raise flag requestStopReasoning.
     */
    public synchronized final void request_stop_reasoning() {
        requestStopReasoning = true;
    }
    
    /**
     * Get the seed of concept.
     * @return 
     */
    public final Neuron get_seed() {
        return seeD;
    }
    
    @Override
    public synchronized void request_termination() {
        // get itself out of the caldron map
        get_attn_circle().get_attn_dispatcher().remove_caldron(get_seed());
        
        super.request_termination();
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
    protected synchronized void _reasoning_() {
        while(true) {
System.out.printf("caldron = %s, _head_ = %s\n", this, load_cpt(_head_).conceptName);
            // Do the assessment
            long[] heads = ((Neuron)load_cpt(_head_)).calculate_activation_and_do_actions(this);

            // May be we have to wait
            if      //no new head?
                    (requestStopReasoning || heads == null || heads.length == 0)
            {   // finish the reasoning
                requestStopReasoning = false;
                break;
            }
            
            // get new head
            _head_ = heads[0];
            
            // create new caldrons for the rest of the heads
            for(int i=1; i<heads.length; i++) {
                Thread thread = new Caldron((Neuron)load_cpt(heads[i]), this, get_attn_circle());
                childreN = (Caldron[])Glob.append_array(childreN, thread);
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
                (msg instanceof Msg_DoReasoningOnBranch)
        {
            _reasoning_();
            return true;
        }
        else if
                (msg instanceof Msg_NotifyBranch)
        {   // activate specified in the message peg and do reasoning
            ((StaticAction)load_cpt(StatCptName.Activate_stat.name())).go(this, new long[] {((Msg_NotifyBranch) msg).peg_cid}, null);
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

    /** The seed of the branch. */
    private final Neuron seeD;
    
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
