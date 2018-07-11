package concepts.stat;

import attention.Caldron;
import attention.ConceptNameSpace;
import concepts.StaticAction;
import concepts.dyn.premises.Peg_prem;

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
     *      [0] - seed of a caldron, that is going to be checked. The seed serves as the caldron's identifier.
     *      [1] - peg, which is going to be activated if the caldron exists, and anactivated if it doesn't.
     * @param extra null
     * @return null
     */
    @Override
    public long[] go(ConceptNameSpace nameSpace, long[] paramCids, Object extra) {
        if      // is the seed present in the seed-caldron map in the dispatcher?
                (((Caldron)nameSpace).get_attn_circle().get_attn_dispatcher().caldir_contains_key(paramCids[0]))
            //yes: activate the peg
            ((Peg_prem)nameSpace.load_cpt(paramCids[1])).activate();
        else //no: anactivate it
            ((Peg_prem)nameSpace.load_cpt(paramCids[1])).antiactivate();
        
        return null;
    }
}
