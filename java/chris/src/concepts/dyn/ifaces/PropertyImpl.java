package concepts.dyn.ifaces;

import chris.Glob;
import concepts.Concept;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author su
 */
public class PropertyImpl implements PropertyIface {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** 
     * Constructor.
     */ 
    public PropertyImpl() { 
    } 

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^
    
    @Override
    public int property_size() {
        if (propertieS == null) 
            return 0;
        else
            return propertieS.size();
    }

    @Override
    public long[] get_properties() {
        Long[] a = new Long[propertieS.size()];
        propertieS.toArray();
        long[] aa = new long[propertieS.size()];
        for(int i=0; i<a.length; i++)
            aa[i] = a[i];
        
        return aa;
    }

    @Override
    public long add_property(Concept cpt) {
        if(propertieS == null) propertieS = new HashSet();
        propertieS.add(cpt.get_cid());
        return cpt.get_cid();
    }

    @Override
    public void set_properties(Concept[] concepts) {
        if(propertieS == null) propertieS = new HashSet();
        for(Concept cpt: concepts)
            propertieS.add(cpt.get_cid());
    }

    /**
     * Create list of lines, which shows the object's content. For debugging. Invoked from Glob.print().
     * @param note printed in the first line just after the object type.
     * @param debugLevel 0 - the shortest, 2 - the fullest
     * @return list of lines, describing this object.
     */
    public List<String> to_list_of_lines(String note, Integer debugLevel) {
        List<String> lst = Glob.create_list_of_lines(this, note);
        Glob.add_line(lst, String.format("property_size() = %s", property_size()));
        if (debugLevel > 0) {
            Glob.add_line(lst, String.format("cids: "));
            for(Long cid: propertieS)
                Glob.append_last_line(lst, String.format("%s; ", cid));
        }
        
        return lst;
    }
    public List<String> to_list_of_lines() {
        return to_list_of_lines("", 2);
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
    
    /** Set of cids, defining pertinent data . The cids are not forbidden to be duplicated in the premises. */
    private Set<Long> propertieS;

    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
