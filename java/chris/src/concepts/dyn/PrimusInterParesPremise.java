package concepts.dyn;

import chris.Crash;
import java.util.HashSet;
import java.util.Set;

/**
 * Premise, that bears a set of cids. Only one of them is a selected member of group.
 * @author su
 */
public final class PrimusInterParesPremise extends BasePremise {

    /**
     * Default constructor.
     */
    public PrimusInterParesPremise() {
        
    }
    
    /** 
     * Constructor.
     * @param cids
     * @param activeCid
     */ 
    public PrimusInterParesPremise(long[] cids, long activeCid) { 
        set_group(cids);
        set_primus(activeCid);
    } 
    
    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^
    
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
        if      // activeCid is not in the set?
                (!grouP.contains(primusCid))
            throw new Crash("activeCid " + primusCid + " is not in the set " + grouP.getClass().getName());
        
        primusCid = cid;
        super.set_activation(1);
    }
    
    /**
     * Setter.
     * @param cids array of cids.
     */
    public void set_group(long[] cids) {
        grouP = new HashSet<>();
        for(long cid: cids) {
            grouP.add(cid);
        }
    }
    
    /**
     * Getter.
     * @return set, that contains cids of the group.
     */
    public Set<Long> get_group() {
        return grouP;
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
    
    /** Cids, which the group consists of. */
    private Set<Long> grouP;
    
    /** The active cid. The rest of the cids are antiactive. */
    private long primusCid;
}
