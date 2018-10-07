package concepts.stat;

import attention.Caldron;
import attention.ConceptNameSpace;
import concepts.StaticAction;

/**
 * Request a caldron to stop reasoning.
 * @author su
 */
public class CaldronStopAndWait_stat extends StaticAction {

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Request caldron to stop reasoning.
     * @param nameSpace caldron, in which thread this function would be invoked.
     * @param paramCids null
     * @param extra null
     * @return null
     */
    @Override
    public long[] go(ConceptNameSpace nameSpace, long[] paramCids, Object extra) {
        
        ((Caldron)nameSpace).request_stop_reasoning();
        
        return null;
    }
}
