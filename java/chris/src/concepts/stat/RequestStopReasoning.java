package concepts.stat;

import attention.Caldron;
import attention.ConceptNameSpace;
import concepts.StaticConcept;

/**
 * Requiring the caldron to stop and wait. The caldron would know for what.
 * @author su
 */
public class RequestStopReasoning extends StaticConcept {

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Request the caldron to stop and wait on its _head_ until a change in the premises would
     * allow continuation of reasoning.
     * @param nameSpace caldron, in which thread this function would be invoked.
     */
    @Override
    public void go(ConceptNameSpace nameSpace) {
        ((Caldron)nameSpace).request_stop_reasoning();
    }
}
