package concepts.dyn.ifaces;

import concepts.Concept;

/**
 * Interface for an ordered group.
 * @author su
 */
public interface OrderedGroupIface extends GroupIface {
    
    /**
     * Add new cid to the end of the group. Initialize the group if necessary.
     * @param cpt Appended concept. If the concept support the PropertyIface, backward links from the concept to the group 
     * (via property list) will be organized.
     */
    public void append_member(Concept cpt);
}
