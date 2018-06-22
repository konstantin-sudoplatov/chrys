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
     */
    public abstract void go(ConceptNameSpace nameSpace);
}
