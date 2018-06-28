package concepts;

import attention.ConceptNameSpace;

/**
 * Base class for static concepts.
 * @author su
 */
abstract public class StaticAction extends Concept {

    /**
     * Do processing for a static concept.
     * @param nameSpace name space to get concepts from
     * @param paramCids
     * @param extra
     * @return 
     */
    public abstract long[] go(ConceptNameSpace nameSpace, long[] paramCids, Object extra);
}
