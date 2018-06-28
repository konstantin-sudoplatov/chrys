package attention;

import chris.BaseMessage;
import chris.Crash;
import chris.Glob;
import concepts.Concept;
import concepts.DynCptName;
import concepts.dyn.groups.PrimusInterParesPremise;
import concepts.dyn.PremiseString;
import concepts.dyn.ifaces.ActivationIface;
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
     * @param circleType example: DynCptName.it_is_console_chat_prem
     */ 
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public AttnCircle(AttnDispatcherLoop attnDisp, DynCptName circleType) 
    {   super(null, null);    // null for being a main caldron
        this.attnDisp = attnDisp;
        
        // The circle specifics: set up the chat media premise.
        ((PrimusInterParesPremise)get_cpt(DynCptName.chat_media_prem.name())).
                set_primus(get_cpt(DynCptName.it_is_console_chat_prem.name()));
        
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
    
    /** 
     * Test if the concept directory contains a concept.
     * @param cid
     * @return true/false
     */
    public synchronized boolean concept_directory_containsKey(long cid) {
        return cptDir.containsKey(cid);
    }
    
    /**
     * Put new concept into the concept directory.
     * @param cid
     * @param cpt 
     */
    public synchronized void put_in_concept_directory(long cid, Concept cpt) {
        cptDir.put(cid, cpt);
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

        if      // a line from console has come?
                (msg instanceof Msg_ConsoleToAttnCircle)
        {   // put it to the concept "line_of_chat_string_prem" and invoke the reasoning
            PremiseString lineOfChat = (PremiseString)get_cpt(DynCptName.line_of_chat_string_prem.name());
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
        ((ActivationIface)get_cpt(DynCptName.chat_prem.name())).set_activation(1);
        ((ActivationIface)get_cpt(DynCptName.chatter_unknown_prem.name())).set_activation(1);
        ((ActivationIface)get_cpt(DynCptName.line_of_chat_string_prem.name())).set_activation(-1);
        
        // set up the caldron head as the next line loader
        _head_ = get_cpt(DynCptName.wait_for_the_line_from_chatter_nrn.name()).get_cid();
    }
    
    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
