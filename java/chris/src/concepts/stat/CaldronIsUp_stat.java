package concepts.stat;

import attention.Caldron;
import attention.ConceptNameSpace;
import concepts.StaticAction;
import concepts.dyn.ifaces.SetActivationIface;

/**
 * Check status of a caldron and activate/anactivate a peg.
 * @author su
 */
public class CaldronIsUp_stat extends StaticAction {

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Check status of a caldron and activate/anactivate a peg.
     * @param nameSpace current caldron.
     * @param paramCids 
     *      [0] - seed of a caldron (its cid), that is going to be checked. The seed serves as the caldron's identifier.
     *      [1] - peg, which is going to be activated if the caldron exists, and anactivated if it doesn't.
     * @param extra concept (not cid), to set activation for. It is to be activated if the caldron exists, and anactivated if not.
     * @return null
     */
    @Override
    public long[] go(ConceptNameSpace nameSpace, long[] paramCids, Object extra) {
        if      // is the seed present in the seed-caldron map in the dispatcher?
                (((Caldron)nameSpace).get_attn_circle().get_attn_dispatcher().caldir_contains_key(paramCids[0]))
            //yes: activate the peg
            ((SetActivationIface)extra).activate();
        else //no: anactivate it
            ((SetActivationIface)extra).antiactivate();
        
        return null;
    }
}
