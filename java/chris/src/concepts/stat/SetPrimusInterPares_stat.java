package concepts.stat;

import attention.ConceptNameSpace;
import concepts.Concept;
import concepts.StaticAction;
import concepts.dyn.premises.PrimusInterPares_prem;

/**
 * Load a concept into the name space and set it with a concrete primus.
 * @author su
 */
public class SetPrimusInterPares_stat extends StaticAction {

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Load a concept into the name space and set it with a concrete primus.
     * @param nameSpace caldron, in which thread this function would be invoked.
     * @param paramCids [0] - primus inter pares cid, [1] - primus concept's cid.
     * @param extra null
     * @return null
     */
    @Override
    public long[] go(ConceptNameSpace nameSpace, long[] paramCids, Object extra) {
        
        Concept primusCpt = nameSpace.get_cpt(paramCids[1]);
        ((PrimusInterPares_prem)nameSpace.get_cpt(paramCids[0])).set_primus(primusCpt);
        
        return null;
    }
}
