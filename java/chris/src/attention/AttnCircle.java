package attention;

import chris.BaseMessage;
import chris.Crash;
import chris.Glob;
import concepts.Concept;
import concepts.DynCptNameEnum;
import concepts.dyn.ActivationIface;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

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
     * @param circleType example: DynCptNameEnum.it_is_console_chat_prem
     */ 
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public AttnCircle(AttnDispatcherLoop attnDisp, DynCptNameEnum circleType) 
    {   super(null, null);    // null for being a main caldron
        this.attnDisp = attnDisp;
        
        // The circle specifics
        ((ActivationIface)get_cpt(DynCptNameEnum.it_is_console_chat_prem.name())).set_activation(1);
        
        // Prepare the first assessment
        initialSetup();
        
        // Do the first reasoning
        _reasoning_();
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
            
        Concept cpt = cptDir.get(cid);
        if      // found in the local concept directory?
                (cpt != null)
            // yes: return the concept
            return cpt;
        else {  // no: load the concept from the common directory and return it
            attnDisp.copy_cpt_to_circle(cid, this);
            return cptDir.get(cid);
        }
    }
    
    /**
     * Get a local concept by name, may be load it initially.
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
//
//    /**
//     * Load a concept by cid from common to local directory. The name directories are updated too, if it is a named concept.
//     * @param cid
//     * @return cid
//     * @throws Crash if not found
//     */
//    public synchronized long load_cpt(long cid) {
//
//        if      // isn't the concept in local directory?
//                (!cptDir.containsKey(cid)) 
//            // load it
//            attnDisp.copy_cpt_to_circle(cid, this);
//
//        return cid;
//    }
//    
//    /**
//     * Load concept by cid and set up the activation value.
//     * @param cid
//     * @param activation
//     * @return cid
//     */
//    public synchronized long load_cpt(long cid, float activation) {
//        ((ActivationIface)get_cpt(cid)).set_activation(activation);
//
//        return cid;
//    }
//    
//    /**
//     * Load a concept by name from common to local directory. name_cid of the local directory is updated with the name and cid.
//     * @param cptName
//     * @return cid
//     */
//    public synchronized long load_cpt(String cptName) {
//        if      // isn't the name in local directory?
//                (!Glob.named.name_cid.containsKey(cptName)) 
//        {   // load the concept
//            long cid = attnDisp.copy_cpt_to_circle(cptName, this);
//            return cid;
//        }
//        else // return its cid
//            return Glob.named.name_cid.get(cptName);
//    }
//
//    /**
//     * Load concept by name and set up the activation value.
//     * @param cptName
//     * @param activation
//     * @return cid
//    */
//    public synchronized long load_cpt(String cptName, float activation) {
//        long cid = load_cpt(cptName);
//        ((ActivationIface)get_cpt(cid)).set_activation(activation);
//        
//        return cid;
//    }
    
    /** 
     * Test if the concept directory contains a concept.
     * @param cid
     * @return true/false
     */
    public synchronized boolean concept_directory_containsKey(long cid) {
        return cptDir.containsKey(cid);
    }
    
//    /** 
//     * Test if the reverse name directory contains a concept.
//     * @param cid
//     * @return true/false
//     */
//    public synchronized boolean cid_name_containsKey(long cid) {
//        return Glob.named.cid_name.containsKey(cid);
//    }
//
//    /** 
//     * Test if the name directory contains a name of concept.
//     * @param name
//     * @return true/false
//     */
//    public synchronized boolean name_cid_containsKey(String name) {
//        return Glob.named.name_cid.containsKey(name);
//    }
    
    /**
     * Put new concept into the concept directory.
     * @param cid
     * @param cpt 
     */
    public synchronized void put_in_concept_directory(long cid, Concept cpt) {
        cptDir.put(cid, cpt);
    }

    @Override
    public synchronized void request_termination() {
        
        // terminate the caldron hierarchy, if exists
        if
                (caldronList != null)
        {
            for(Caldron caldron : caldronList)
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
                    (_head_ == 0)
            {   //no: start it
                  
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

    /** Local concept directory. */
    private final Map<Long, Concept> cptDir = new HashMap();
    
    /** Attention dispatcher. Parent. */
    private final AttnDispatcherLoop attnDisp;
    
    /** Possible set of child caldrons . */
    private List<Caldron> caldronList;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--
    
    /**
     * Get all the premises and effect ready for the first assessment.
     */
    private void initialSetup() {

        // set up premises
        ((ActivationIface)get_cpt(DynCptNameEnum.chat_prem.name())).set_activation(1);
        ((ActivationIface)get_cpt(DynCptNameEnum.chatter_unknown_prem.name())).set_activation(1);
        ((ActivationIface)get_cpt(DynCptNameEnum.next_line_of_chat_has_come_prem.name())).set_activation(-1);
        
        // set up the caldron head as the next line loader
        _head_ = get_cpt(DynCptNameEnum.wait_for_the_line_from_chatter_nrn.name()).get_cid();

        // set up its premises, effects and the action of getting the next line
        
        
        // Create and fill new current assertion
//        _head_ = new Assertion();
//        long cid = load_cpt(DynCptNameEnum.console_chat_seed.name());
//        StaticConcept stat = (StaticConcept)get_cpt(StatCptEnum.LoadPremisesIntoFirstAssertion.ordinal());
//        stat.go(this, new long[] {cid}, _head_);
        
        // Set up its premises
//        load_cpt(DynCptNameEnum.chat_prem.name());
//        _head_.add_premise(DynCptNameEnum.chat_prem.ordinal());
//        _head_.add_premise(DynCptNameEnum.it_is_console_chat_prem.ordinal());
//        _head_.add_premise(DynCptNameEnum.it_is_the_first_line_of_chat_prem.ordinal());
//        _head_.add_premise(DynCptNameEnum.line_of_chat.ordinal());
//        _head_.add_premise(Glob.attn_disp_loop.add_cpt(new JustString(msg.) ));
    }
    
    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}