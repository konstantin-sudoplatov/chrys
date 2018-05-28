package starter;

import attention.AttnDispatcherLoop;
import chris.Glob;
import concepts.DynCptNameEnum;
import concepts.StatCptEnum;
import concepts.dyn.Neuron;
import concepts.dyn.primitives.MarkedString;

/**
 * When there is no DB or it is empty, we have to start with something...
 * @author su
 */
final public class Starter {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** 
     * Disable constructor.
    */ 
    private Starter() { 
    } 

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    public static void generate_dynamic_concepts() {
        for(DynCptNameEnum cptName: DynCptNameEnum.values())
            addNamedCpt(cptName.name());
    }
    
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //      Protected    Protected    Protected    Protected    Protected    Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$--- protected data $$$---$$$---$$$---$$$---$$$---$$$--

    //---$$$---$$$---$$$---$$$---$$$--- protected methods ---$$$---$$$---$$$---$$$---$$$---

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%

    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    /**
     * Generate and add a concept, that has an identifier both inside it and set into the common name directory.
     * @param name a symbolic identifier of the concept.
     */
    private static void addNamedCpt(String name) {

        AttnDispatcherLoop atl = Glob.attn_disp_loop;
        long cid = atl.add_cpt(new MarkedString(StatCptEnum.Mrk_ConceptName.ordinal(), name));   // primitive, containing the concept's name
        atl.add_cpt(new Neuron(new long[] {cid}), name);
    }
    
    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
