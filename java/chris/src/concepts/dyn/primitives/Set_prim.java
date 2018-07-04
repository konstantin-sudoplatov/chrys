package concepts.dyn.primitives;


import chris.Glob;
import concepts.Concept;
import concepts.dyn.Primitive;
import concepts.dyn.ifaces.PropertyIface;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author su
 */
public class Set_prim extends Primitive {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** 
     * Constructor.
     */ 
    public Set_prim() { 
    } 

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Get group size.
     * @return number of members or null if group is not initialized.
     */
    public int group_size() {
        return memberS.size();
    }

    /**
     * Check if the concept's cid is present in the group.
     * @param cpt
     * @return true/false
     */
    public boolean contains_member(Concept cpt) {
        return memberS.contains(cpt.get_cid());
    }
    
    /**
     * Add new cid to the group. Initialize the group if necessary.
     * @param cpt Added concept. If the concept support the PropertyIface, backward links from the concept to the group 
     * (via property list) will be organized.
     * @return true/false
     */
    public boolean add_member(Concept cpt) {
        if (memberS == null) memberS = new HashSet();
        memberS.add(cpt.get_cid());

        if      // concept implements property interface?
                (cpt instanceof PropertyIface)
            //yes: set up the backward link
            ((PropertyIface)cpt).add_property(this);
        
        return true;
    }
    
    public boolean remove_member(Concept cpt) {
        if (memberS == null)
            return false;
        
        if      // concept implements property interface?
                (cpt instanceof PropertyIface)
            //yes: set up the backward link
            ((PropertyIface)cpt).remove_property(this);
                
        return memberS.remove(cpt.get_cid());
    }
    
    /**
     * Setter.
     * @param concepts array of cids.
     */
    public void set_members(Concept[] concepts) {
        memberS = new HashSet<>();
        for(Concept cpt: concepts) {
            memberS.add(cpt.get_cid());
            if      // concept implements property interface?
                    (cpt instanceof PropertyIface)
            //yes: set up the backward link
            ((PropertyIface)cpt).add_property(this);
        }
    }
    
    /** 
     * Get set as an array.
     * @return array of cids.
     */
    public Long[] get_members() {
        return (Long[])memberS.toArray();
    }

    /**
     * Create list of lines, which shows the object's content. For debugging. Invoked from Glob.print().
     * @param note printed in the first line just after the object type.
     * @param debugLevel 0 - the shortest, 2 - the fullest
     * @return list of lines, describing this object.
     */
    @Override
    public List<String> to_list_of_lines(String note, Integer debugLevel) {
        List<String> lst = super.to_list_of_lines(note, debugLevel);
        Glob.add_line(lst, String.format("group_size() = %s", group_size()));
        if (debugLevel > 1) {
            Glob.add_line(lst, String.format("cids: "));
            for(Long cid: memberS)
                Glob.append_last_line(lst, String.format("%s; ", cid));
        }

        return lst;
    }

    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //      Protected    Protected    Protected    Protected    Protected    Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$--- protected data $$$---$$$---$$$---$$$---$$$---$$$--

    //---$$$---$$$---$$$---$$$---$$$--- protected methods ---$$$---$$$---$$$---$$$---$$$---

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%
    
    /** Cids, which the group consists of. */
    private Set<Long> memberS;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
