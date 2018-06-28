package concepts.dyn.groups;

import chris.Crash;
import concepts.Concept;
import concepts.dyn.UnorderedGroup;
import concepts.dyn.ifaces.ActivationIface.NormalizationType;
import java.util.Set;

/**
 * Premise, that bears a set of cids. Only one of them is a selected member of group.
 * @author su
 */
public final class PrimusInterParesPremise extends UnorderedGroup {

    /**
     * Default constructor.
     */
    public PrimusInterParesPremise() {
        
    }
    
    /** 
     * Constructor.
     * @param cpt
     * @param primus
     */ 
    public PrimusInterParesPremise(Concept[] cpt, Concept primus) { 
        set_members(cpt);
        set_primus(primus);
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
     * @param cpt 
     */
    public void set_primus(Concept cpt) {
        Set<Long> members = get_members();
        if      // activeCid is not in the set?
                (!members.contains(cpt.get_cid()))
            throw new Crash("primusCid " + cpt + " is not in the set " + members.getClass().getName());
        
        primusCid = cpt.get_cid();
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
