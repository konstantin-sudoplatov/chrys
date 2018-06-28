package concepts.dyn.premises;

import chris.Crash;
import concepts.Concept;
import concepts.dyn.Premise;
import concepts.dyn.ifaces.ActivationIface.NormalizationType;
import concepts.dyn.ifaces.UnorderedGroupIface;
import concepts.dyn.ifaces.UnorderedGroupImpl;

/**
 * Premise, that bears a set of cids. Only one of them is a selected member of group.
 * @author su
 */
public final class PrimusInterParesPremise extends Premise implements UnorderedGroupIface {

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
    public NormalizationType get_normalization_type() {
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
        if      // activeCid is not in the set?
                (!contains_member(cpt))
            throw new Crash("primusCid " + cpt.get_cid() + " is not a member of the group.");
        
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

    @Override
    public int group_size() {
        return grouP.group_size();
    }

    @Override
    public boolean contains_member(Concept cpt) {
        return grouP.contains_member(cpt);
    }

    @Override
    public void add_member(Concept cpt) {
        grouP.add_member(cpt);
    }

    @Override
    public void set_members(Concept[] concepts) {
        grouP.set_members(concepts);
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%
    
    /** The active cid. The rest of the cids are antiactive. */
    private long primusCid;
    
    /** Group, that contains all members of this premise. */
    private UnorderedGroupImpl grouP = new UnorderedGroupImpl(this);
}
