package concepts.dyn.primitives;

import attention.Caldron;
import chris.Glob;
import concepts.Concept;
import concepts.dyn.Primitive;
import concepts.dyn.ifaces.PropertyIface;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author su
 */
public class List_prim extends Primitive {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** 
     * Constructor.
     */ 
    public List_prim() { 
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
     * Add new cid to the end of the group. Initialize the group if necessary.
     * @param cpt Appended concept. If the concept support the PropertyIface, backward links from the concept to the group 
     * (via property list) will be organized.
     */
    public void append_member(Concept cpt) {
        if (memberS == null) memberS = new ArrayList();
        memberS.add(cpt.get_cid());

        if      // concept implements property interface?
                (cpt instanceof PropertyIface)
        //yes: set up the backward link
        ((PropertyIface)cpt).add_property(this);
    }
    
    /**
     * Setter.
     * @param concepts array of cids.
     */
    public final void set_members(Concept[] concepts) {
        memberS = new ArrayList();
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
    public final Long[] get_members() {
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
        if (debugLevel == 1) {
            Glob.add_line(lst, String.format("cids: "));
            for(Long cid: memberS)
                Glob.append_last_line(lst, String.format("%s; ", cid));
        }
        else if (debugLevel > 1) {
            for(Long cid: memberS) {
                Glob.add_line(lst, String.format("cid: %s; ", cid));
                Caldron c = (Caldron)Thread.currentThread();
                Glob.add_list_of_lines(lst, c.load_cpt(cid).to_list_of_lines("", debugLevel-1));
            }
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
    private List<Long> memberS;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
