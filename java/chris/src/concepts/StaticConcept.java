package concepts;

import attention.ConceptNameSpace;

/**
 * Base class for static concepts.
 * @author su
 */
abstract public class StaticConcept extends Concept {

    /**
     * Do processing for a static concept.
     * @param nameSpace name space to get concepts from
     * @param paramCid array of cids of parameter concepts
     * @param extra possible supplementary parameters, can be null.
     * @return array of cids, can be null.
     */
    public abstract long[] go(ConceptNameSpace nameSpace, long[] paramCid, Object extra);
}
