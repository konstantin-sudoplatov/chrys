package concepts.dyn.ifaces;

import concepts.Concept;
import java.util.Set;

/**
 *
 * @author su
 */
public interface UnorderedGroupIface {
    
    /**
     * Add new cid to the group. Initialize the group if necessary.
     * @param cpt Added concept. The concept should support the PropertyIface, since the implementation probably would want 
     * to organize a backward reference from the concept to the group (via property list).
     */
    public void add_member(Concept cpt);
    
    /**
     * Setter.
     * @param concepts array of cids.
     */
    public void set_members(Concept[] concepts);
    
    /**
     * Getter.
     * @return set, that contains cids of the group.
     */
    public Set<Long> get_members();
}
