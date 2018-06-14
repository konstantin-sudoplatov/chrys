package attention;

import chris.BaseMessage;
import chris.Crash;
import chris.Glob;
import concepts.Concept;
import concepts.ConceptDirectory;
import concepts.DynCptNameEnum;
import concepts.dyn.ActivationIface;
import java.util.List;
import concepts.dyn.AssessmentIface;

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
     */ 
    public AttnCircle(AttnDispatcherLoop attnDisp) 
    {   super(null);    // null for being a main caldron
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
            
        Concept cpt = cptDir.cid_cpt.get(cid);
        if      // is there entry in the local concept directory?
                (cpt != null)
            // yes: return the concept
            return cpt;
        else {  // no: load the concept from the common directory and return it
            attnDisp.copy_cpt_to_circle(cid, this);
            return cptDir.cid_cpt.get(cid);
        }
    }
    
    /**
     * Get a local concept by name, may be load it initially.
     * @param cptName
     * @return the concept
     * @throws Crash if not found
     */
    public synchronized Concept get_cpt(String cptName) {
        long cid;
        if      // isn't the name in local directory?
                (!cptDir.name_cid.containsKey(cptName)) 
        {   // load the concept
            cid = attnDisp.copy_cpt_to_circle(cptName, this);
        }
        else // return its cid
            cid = cptDir.name_cid.get(cptName);
        
        return cptDir.cid_cpt.get(cid);
    }

    /**
     * Load a concept by cid from common to local directory. The name directories are updated too, if it is a named concept.
     * @param cid
     * @return cid
     * @throws Crash if not found
     */
    public synchronized long load_cpt(long cid) {

        if      // isn't the concept in local directory?
                (!cptDir.cid_cpt.containsKey(cid)) 
            // load it
            attnDisp.copy_cpt_to_circle(cid, this);

        return cid;
    }
    
    /**
     * Load concept by cid and set up the activation value.
     * @param cid
     * @param activation
     * @return cid
     */
    public synchronized long load_cpt(long cid, float activation) {
        ((ActivationIface)get_cpt(cid)).set_activation(activation);

        return cid;
    }
    
    /**
     * Load a concept by name from common to local directory. name_cid of the local directory is updated with the name and cid.
     * @param cptName
     * @return cid
     */
    public synchronized long load_cpt(String cptName) {
        if      // isn't the name in local directory?
                (!cptDir.name_cid.containsKey(cptName)) 
        {   // load the concept
            long cid = attnDisp.copy_cpt_to_circle(cptName, this);
            return cid;
        }
        else // return its cid
            return cptDir.name_cid.get(cptName);
    }

    /**
     * Load concept by name and set up the activation value.
     * @param cptName
     * @param activation
     * @return cid
    */
    public synchronized long load_cpt(String cptName, float activation) {
        long cid = load_cpt(cptName);
        ((ActivationIface)get_cpt(cid)).set_activation(activation);
        
        return cid;
    }
    
    /** 
     * Test if the concept directory contains a concept.
     * @param cid
     * @return true/false
     */
    public synchronized boolean cid_cpt_containsKey(long cid) {
        return cptDir.cid_cpt.containsKey(cid);
    }
    
    /** 
     * Test if the reverse name directory contains a concept.
     * @param cid
     * @return true/false
     */
    public synchronized boolean cid_name_containsKey(long cid) {
        return cptDir.cid_name.containsKey(cid);
    }

    /** 
     * Test if the name directory contains a name of concept.
     * @param name
     * @return true/false
     */
    public synchronized boolean name_cid_containsKey(String name) {
        return cptDir.name_cid.containsKey(name);
    }
    
    /**
     * Put new concept into the concept directory.
     * @param cid
     * @param cpt 
     */
    public synchronized void put_in_cid_cpt(long cid, Concept cpt) {
        cptDir.cid_cpt.put(cid, cpt);
    }
    
    /**
     * Put new concept into the front and reverse name directories.
     * @param name
     * @param cid
     */
    public synchronized void put_in_name_dirs(String name, long cid) {
        cptDir.name_cid.put(name, cid);
        cptDir.cid_name.put(cid, name);
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
                    (_head_ == null)
            {   //no: start it
                initialSetupWithConsole();  
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
    private List<Caldron> caldronList;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    /**
     * Get all the premises and effects ready for the first assessment, add specifics for chatting via console.
     */
    private void initialSetupWithConsole() {
        initialSetup();
        
        // set up the caldron head as the next line loader
        _head_ = (AssessmentIface)get_cpt(load_cpt(DynCptNameEnum.wait_for_the_line_from_chatter_nrn.name()));
    }
    
    /**
     * Get all the premises and effects ready for the first assessment.
     */
    private void initialSetup() {
        
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
