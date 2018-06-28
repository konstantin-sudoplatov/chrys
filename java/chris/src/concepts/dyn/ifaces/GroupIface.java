package concepts.dyn.ifaces;

import concepts.Concept;

/**
 * Interface for a group of cids.
 * @author su
 */
public interface GroupIface {
    
    /**
     * Get group size.
     * @return number of members or null if group is not initialized.
     */
    public int group_size();
    
    /**
     * Check if the concept's cid is present in the group.
     * @param cpt
     * @return true/false
     */
    public boolean contains_member(Concept cpt);
    
    /**
     * Setter.
     * @param concepts array of cids.
     */
    public void set_members(Concept[] concepts);
}
