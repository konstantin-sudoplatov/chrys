package concepts.stat;

import attention.Caldron;
import attention.ConceptNameSpace;
import concepts.StaticConcept;

/**
 * Requiring the caldron to send request for the next line of chat.
 * @author su
 */
public class RequestNextLineFromChatter extends StaticConcept {

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Make caldron to send request for getting the next line from chatter.
     * @param nameSpace caldron, in which thread this function would be invoked.
     * @param paramCids not used in here.
     * @param extra not used in here.
     * @return null
     */
    @Override
    public long[] go(ConceptNameSpace nameSpace, long[] paramCids, Object extra) {
        ((Caldron)nameSpace).request_stop_reasoning();
        
        return null;
    }
}
