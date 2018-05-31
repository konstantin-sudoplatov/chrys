package attention;

import concepts.Concept;

/**
 * Concept name space is an object, that contains a concept directory. For example, the attention dispatcher loop contains the common
 * concept directory and bubble attention loops contain local directories. Each must realize this interface, so concepts could
 * be retrieved from right name space.
 * @author su
 */
public interface ConceptNameSpace {

    /**
     * Get a concept from current name space.
     * @param cid
     * @return
     */
    public Concept get_cpt(long cid);
}
