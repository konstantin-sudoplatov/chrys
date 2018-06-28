package concepts.dyn;

import concepts.Concept;
import concepts.dyn.ifaces.PropertyIface;
import concepts.dyn.ifaces.UnorderedGroupIface;
import java.util.HashSet;
import java.util.Set;

/**
 * Premise, that bears a set of cids. Only one of them is a selected member of group.
 * @author su
 */
abstract public class UnorderedGroup extends Premise implements UnorderedGroupIface {

    /**
     * Default constructor.
     */
    public UnorderedGroup() {
        
    }
//    
//    /** 
//     * Constructor.
//     * @param concepts
//     */ 
//    public UnorderedGroup(Concept[] concepts) { 
//        set_members(concepts);
//    } 
    
    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^
    
    @Override
    public NormalizationType normalization_type() {
        return NormalizationType.BIN;
    }
    
    @Override
    public void add_member(Concept cpt) {
        if (memberS == null) memberS = new HashSet();
        memberS.add(cpt.get_cid());
        ((PropertyIface)cpt).add_property(this);
    }
    
    @Override
    public final void set_members(Concept[] concepts) {
        memberS = new HashSet<>();
        for(Concept cpt: concepts) {
            memberS.add(cpt.get_cid());
            ((PropertyIface)cpt).add_property(this);
        }
    }
    
    @Override
    public Set<Long> get_members() {
        return memberS;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%
    
    /** Cids, which the group consists of. */
    private Set<Long> memberS;
    
}
