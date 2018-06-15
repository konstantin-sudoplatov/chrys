package auxiliary;

import java.util.HashMap;
import java.util.Map;

/**
 * Provide bidirectional search concept name - cid. Does not change after initialization.
 * @author su
 */
public class NamedConcept {

    /** Cid by concept name directory. */
    public final Map<String, Long> name_cid = new HashMap<>();

    /** Concept name by cid directory. */
    public final Map<Long, String> cid_name = new HashMap<>();
}
