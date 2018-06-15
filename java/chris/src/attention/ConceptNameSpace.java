package attention;

import chris.Crash;
import concepts.Concept;

/**
 * Concept name space is an object, that contains a concept directory. For example, the attention dispatcher loop contains the common
 * concept directory and bubble attention loops contain local directories. Each must realize this interface, so concepts could
 * be retrieved from right name space.
 * @author su
 */
public interface ConceptNameSpace {

    /**
     * Get a concept from current name space. If the concept is not in the current name space yet, it gets cloned into it.
     * @param cid
     * @return concept object
     */
    public Concept get_cpt(long cid);
    
    /**
     * Get a local concept by name. If the concept is not in the current name space yet, it gets cloned into it.
     * @param cptName
     * @return the concept
     * @throws Crash if not found
     */
    public Concept get_cpt(String cptName);
}
