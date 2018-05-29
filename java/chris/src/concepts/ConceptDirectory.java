package concepts;

import java.util.HashMap;
import java.util.Map;

/**
 * Concept directory structure. Used for the common directory and bubble directories. It is a bare minimum, just a structure 
 * without methods or synchronization. All that is provided by a wrapper class, like a bubble or the bubble dispatcher.
 * @author su
 */
public class ConceptDirectory {
    
    /** Concept directory: a map of a concept by its cid. */
    public final Map<Long, Concept> cid_dir = new HashMap<>();

    /** Concept name directory: a map of a cid by the concept name. */
    public final Map<String, Long> name_dir = new HashMap<>();
}
