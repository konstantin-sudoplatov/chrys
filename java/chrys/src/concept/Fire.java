package concept;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This object will make a concept a logic unit, i.e. it can be fired by combination of other concepts (premises)
 * and its firing can result in firing a set of others concepts (effects).
 * @author su
 */
public class Fire {
    
    /** Set of effects.  */
    public Set<Long> effects = new HashSet(1);
    
    /** Map of premises: Map<cid, weight> - pairs of concepts and their weights in takin decision of firing this concept. */
    public Map<Long, Float> premises = new HashMap(1);
}
