package concepts.dyn;

import java.util.HashSet;
import java.util.Set;

/**
 * Premise, that bears a set of cids. Only one of them is a selected member of group.
 * @author su
 */
public class Group extends Premise {

    /**
     * Default constructor.
     */
    public Group() {
        
    }
    
    /** 
     * Constructor.
     * @param cids
     */ 
    public Group(long[] cids) { 
        set_members(cids);
    } 
    
    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^
    
    @Override
    public NormalizationType normalization_type() {
        return NormalizationType.BIN;
    }
    
    /**
     * Add new cid to the group. Initialize the group if necessary.
     * @param cid 
     */
    public void add_member(long cid) {
        if (memberS == null) memberS = new HashSet();
        memberS.add(cid);
    }
    
    /**
     * Setter.
     * @param cids array of cids.
     */
    public final void set_members(long[] cids) {
        memberS = new HashSet<>();
        for(long cid: cids) {
            memberS.add(cid);
        }
    }
    
    /**
     * Getter.
     * @return set, that contains cids of the group.
     */
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
