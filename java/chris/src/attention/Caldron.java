package attention;

import auxiliary.ActiveRange;
import auxiliary.Effects;
import chris.BaseMessage;
import chris.BaseMessageLoop;
import chris.Crash;
import chris.Glob;
import concepts.Concept;
import concepts.DCN;
import concepts.SCN;
import concepts.StaticAction;
import concepts.dyn.Action;
import concepts.dyn.LogicNeuron;
import concepts.dyn.Neuron;
import concepts.dyn.ifaces.GetActivationIface;
import concepts.dyn.ifaces.TransientIface;
import concepts.dyn.neurons.WeighedSum_nrn;
import concepts.dyn.primitives.String_prim;
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
        seedCid = seed.get_cid();
        _head_ = seed.get_cid();
        parenT = parent;
        this.attnCircle = attnCircle;
        if      // is it an ordinary caldron (not an attention circle)?
                (!(this instanceof AttnCircle))
            //yes: put itself into the caldron map
            get_attn_circle().get_attn_dispatcher().put_caldron(seedCid, this);
        else {}//no: do nothing, since the dispatcher is unknown yet. We will do it in the descendant.

        if      //is it me?
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
            assert true: cpt.name_space = this;
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
    public final long get_seed_cid() {
        return seedCid;
    }
    
    @Override
    public synchronized void request_termination() {
        // get itself out of the caldron map
        get_attn_circle().get_attn_dispatcher().remove_caldron(get_seed_cid());
        
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
if (debugPrint) printAtTheBeginning();
            // Do the assessment
            long[] heads = ((Neuron)load_cpt(_head_)).calculate_activation_and_do_actions(this);
if (debugPrint) printAfterCalculatingActivationAndDoingActions();

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
if (debugPrint) printBeforeStopAndWait();
    }
    private final static boolean debugPrint = true;     // Controls debug printing.

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
        else if //notification has come?
                (msg instanceof Msg_NotifyBranch)
        {   // activate specified in the message peg, take telegram if provided, and do reasoning
            Msg_NotifyBranch note = (Msg_NotifyBranch)msg;
            if      // is there a telegram?
                    (note.telegram != null)
            {//yes: get instance of the telegram from our coldron and copy into it contents of the telegram
                Concept cpt = load_cpt(((Concept)note.telegram).get_cid());
                ((TransientIface)cpt).follow((Concept)note.telegram);
            }
            // activate the peg
            ((StaticAction)load_cpt(SCN.Activate_stat.name())).go(this, new long[] {note.peg_cid}, null);
            _reasoning_();
            return true;
        }
        else if
                // a line from console has come?
                (msg instanceof Msg_ConsoleToAttnCircle)
        {   // put it to the concept "line_from_chatter_strprim", activate the 
            // "loop_notifies_console_branch_next_line_come_peg" peg and invoke the reasoning
            String_prim lineOfChat = (String_prim)load_cpt(DCN.line_from_chatter_strprim.name());
            lineOfChat.set_string(((Msg_ConsoleToAttnCircle) msg).text);
            StaticAction activateStatActn = (StaticAction)load_cpt(SCN.Activate_stat.name());
            activateStatActn.go(this, new long[]{Glob.named.name_cid.get(DCN.loop_notifies_console_branch_next_line_come_peg.name())}, null);
            
            _reasoning_();
            
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

    /** The seed of the branch. */
    private final long seedCid;
    
    /** Parent caldron. null if it is the attention circle. */
    private final Caldron parenT;

    /** The root of the caldron tree. null if it is the attention circle. */
    private final AttnCircle attnCircle;
    
    /** Children caldrons. */
    private Caldron[] childreN;
    
    /** If this flag raised, reasoning will stop and caldron will wait on the neuron. */
    private boolean requestStopReasoning;
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--
    
    /**
     * Debug print.
     */
    private synchronized void printAtTheBeginning() {
        String shortCaldronName = load_cpt(this.seedCid).concept_name;
        shortCaldronName = shortCaldronName.substring(0, shortCaldronName.indexOf("_"));
        System.out.printf("%s, before reasoning: ", shortCaldronName );
        Concept cpt = load_cpt(_head_);
        System.out.printf("_head_ = %s\n", cpt.concept_name);
        
        // Print out premises and its activations
        if
                (cpt instanceof LogicNeuron)
        {
            LogicNeuron logicCpt = (LogicNeuron)cpt;
            
            System.out.printf("    Premises:\n");
            for(long cid: logicCpt.get_premises()) {
                Concept c = load_cpt(cid);
                String cptName = c.concept_name;
                if (cptName == null) cptName = String.format("%s", cid);
                float activation = ((GetActivationIface)c).get_activation();
                System.out.printf("        %s, activation = %s\n", cptName, activation);
            }
        }
        else if
                (cpt instanceof WeighedSum_nrn)
        {
            WeighedSum_nrn weighedCpt = (WeighedSum_nrn)cpt;
            System.out.printf("Premises: not realized yet\n");
        }
        
        // Print ranges
        System.out.println("    Ranges:");
        Neuron nrn = (Neuron)cpt;
        for (ActiveRange range: nrn.get_ranges()) {
            // Print actions
            System.out.printf("        %s\n            Actions: ", range.range);
            if (range.effects.actions != null) {
                for(long actCid: range.effects.actions) {
                    Action act = (Action)load_cpt(actCid);
                    System.out.printf("%s; ", act.concept_name);
                }
                System.out.println();
            }
            else
                System.out.println("null");
            
            // Print branches
            System.out.print("            Branches: ");
            if (range.effects.branches != null) {
                for(long brCid: range.effects.branches) {
                    Neuron br = (Neuron)load_cpt(brCid);
                    System.out.printf("%s; ", br.concept_name);
                }
                System.out.println();
            }
            else
                System.out.println("null");
        }
        
        System.out.println();
    }
    
    public synchronized void printAfterCalculatingActivationAndDoingActions() {
        String shortCaldronName = load_cpt(this.seedCid).concept_name;
        shortCaldronName = shortCaldronName.substring(0, shortCaldronName.indexOf("_"));
        System.out.printf("%s, after reasoning: ", shortCaldronName );
        Neuron nrn = (Neuron)load_cpt(_head_);
        float activation = nrn.get_activation();
        System.out.printf("_head_ = %s. Activation %s\n", nrn.concept_name, activation);

        // Print out selected actions and branches
        System.out.println("    Selected:");
        Effects selectedEffects = nrn.select_effects(activation);
        System.out.print("        Actions: ");
        if (selectedEffects.actions != null) {
            for(long actCid: selectedEffects.actions) {
                Action act = (Action)load_cpt(actCid);
                System.out.printf("%s; ", act.concept_name);
            }
            System.out.println();
        }
        else
            System.out.println("null");

        // Print branches
        System.out.print("        Branches: ");
        if (selectedEffects.branches != null) {
            for(long brCid: selectedEffects.branches) {
                Neuron br = (Neuron)load_cpt(brCid);
                System.out.printf("%s; ", br.concept_name);
            }
            System.out.println();
        }
        else
            System.out.println("null");
        
        System.out.println();
    }
    
    public synchronized void printBeforeStopAndWait() {
        String shortCaldronName = load_cpt(this.seedCid).concept_name;
        shortCaldronName = shortCaldronName.substring(0, shortCaldronName.indexOf("_"));
        System.out.printf("%s, leaving: ", shortCaldronName );
        Neuron nrn = (Neuron)load_cpt(_head_);
        System.out.printf("_head_ = %s\n\n", nrn.concept_name);
    }
    
    public synchronized String shortNameOfCaldron() {
        String s = load_cpt(this.seedCid).concept_name;
        return s.substring(0, s.indexOf("_"));
    }
    
    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
