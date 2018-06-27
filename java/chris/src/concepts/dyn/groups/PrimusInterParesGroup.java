package concepts.dyn.groups;

import chris.Crash;
import concepts.dyn.Group;
import concepts.dyn.ifaces.ActivationIface.NormalizationType;
import java.util.Set;

/**
 * Premise, that bears a set of cids. Only one of them is a selected member of group.
 * @author su
 */
public final class PrimusInterParesGroup extends Group {

    /**
     * Default constructor.
     */
    public PrimusInterParesGroup() {
        
    }
    
    /** 
     * Constructor.
     * @param cids
     * @param activeCid
     */ 
    public PrimusInterParesGroup(long[] cids, long activeCid) { 
        set_members(cids);
        set_primus(activeCid);
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
     * Getter.
     * @return cid of the primus 
     */
    public long get_primus() {
        return primusCid;
    }

    /**
     * Setter. As a side effect it sets the activation field to +1. It is the only way to
     * set up activation for this concept (on creation it is -1).
     * @param cid 
     */
    public void set_primus(long cid) {
        Set<Long> members = get_members();
        if      // activeCid is not in the set?
                (!members.contains(cid))
            throw new Crash("primusCid " + cid + " is not in the set " + members.getClass().getName());
        
        primusCid = cid;
        super.set_activation(1);
    }

    /**
     * Disable the activation setter. Activation sets up in the set_primus() method.
     * @param activation
     */
    @Override
    public void set_activation(float activation) {
        throw new Crash("The activation field sets up in the set_primus_cid() method.");
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%
    
    /** The active cid. The rest of the cids are antiactive. */
    private long primusCid;
    
}
