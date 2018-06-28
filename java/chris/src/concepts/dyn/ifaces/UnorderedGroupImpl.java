package concepts.dyn.ifaces;


import concepts.Concept;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of the UnorderedGroupIface.
 * @author su
 */
public class UnorderedGroupImpl implements UnorderedGroupIface {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** 
     * Constructor.
     * @param host
     */ 
    public UnorderedGroupImpl(Concept host) { 
        hosT = host;
    } 

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    @Override
    public int group_size() {
        return memberS.size();
    }

    @Override
    public boolean contains_member(Concept cpt) {
        return memberS.contains(cpt.get_cid());
    }
    
    @Override
    public void add_member(Concept cpt) {
        if (memberS == null) memberS = new HashSet();
        memberS.add(cpt.get_cid());

        if      // concept implements property interface?
                (cpt instanceof PropertyIface)
        //yes: set up the backward link
        ((PropertyIface)cpt).add_property(hosT);
    }
    
    @Override
    public final void set_members(Concept[] concepts) {
        memberS = new HashSet<>();
        for(Concept cpt: concepts) {
            memberS.add(cpt.get_cid());
            if      // concept implements property interface?
                    (cpt instanceof PropertyIface)
            //yes: set up the backward link
            ((PropertyIface)cpt).add_property(hosT);
        }
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

    /** Concept, that contains this implementation and supports related interface. */
    private Concept hosT;
    
    /** Cids, which the group consists of. */
    private Set<Long> memberS;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
