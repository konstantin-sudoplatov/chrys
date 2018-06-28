package concepts.dyn.ifaces;

import concepts.Concept;

/**
 * Interface for an unordered group.
 * @author su
 */
public interface UnorderedGroupIface extends GroupIface {
    
    /**
     * Add new cid to the group. Initialize the group if necessary.
     * @param cpt Added concept. If the concept support the PropertyIface, backward links from the concept to the group 
     * (via property list) will be organized.
     */
    public void add_member(Concept cpt);
}
