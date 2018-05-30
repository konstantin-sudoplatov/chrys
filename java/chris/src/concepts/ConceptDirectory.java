package concepts;

import java.util.HashMap;
import java.util.Map;

/**
 * Concept directory structure. Used for the common directory and bubble directories. It is a bare minimum, just a structure 
 * without methods or synchronization. All that is provided by a wrapper class, like a bubble or bubble dispatcher.
 * @author su
 */
public class ConceptDirectory {
    
    /** Concept by cid directory: a map of a concepts by their cids. */
    public final Map<Long, Concept> cid_cpt = new HashMap<>();

    /** Cid by concept name directory. */
    public final Map<String, Long> name_cid = new HashMap<>();

    /** Concept name by cid directory. */
    public final Map<Long, String> cid_name = new HashMap<>();
}
