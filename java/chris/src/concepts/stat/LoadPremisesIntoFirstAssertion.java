package concepts.stat;

import attention.Assertion;
import attention.ConceptNameSpace;
import concepts.StaticConcept;
import concepts.dyn.Neuron;

/**
 * Preparing caldron for the first assertion.
 * @author su
 */
public class LoadPremisesIntoFirstAssertion extends StaticConcept {

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Unwrap initial concepts from the DynCptNameEnum.initial_concepts_for_start_of_console_chat concept into the assertion object.
     * @param nameSpace attention bubble, which contains the assertion object.
     * @param paramCid array of cids, first element of which is the DynCptNameEnum.initial_concepts_for_start_of_console_chat concept.
     * @param extra the assertion object.
     * @return 
     */
    @Override
    public long[] go(ConceptNameSpace nameSpace, long[] paramCid, Object extra) {
        Assertion assertion = (Assertion)extra;     // the assertion object
        Neuron nr = (Neuron)nameSpace.get_cpt(paramCid[0]);     // the DynCptNameEnum.initial_concepts_for_start_of_console_chat concept
        
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
