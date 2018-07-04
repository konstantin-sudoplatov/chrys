package concepts.stat;

import attention.ConceptNameSpace;
import concepts.StaticAction;
import concepts.dyn.ifaces.ActivationIface;

/**
 * Load a concept into the name space and antiactivate it.
 * @author su
 */
public class Antiactivate_stat extends StaticAction {

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Load a concept into the name space and antiactivate it.
     * @param nameSpace caldron, in which thread this function would be invoked.
     * @param paramCids [0] - concept's cid.
     * @param extra null
     * @return null
     */
    @Override
    public long[] go(ConceptNameSpace nameSpace, long[] paramCids, Object extra) {
        
        ((ActivationIface)nameSpace.get_cpt(paramCids[0])).set_activation(-1);
        
        return null;
    }
}
