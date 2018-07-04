package concepts.dyn.premises;

import attention.ConceptNameSpace;
import chris.Crash;
import concepts.Concept;
import concepts.dyn.ifaces.ActivationIface;
import concepts.dyn.ifaces.ActivationIface.NormalizationType;
import concepts.dyn.ifaces.ActivationImpl;
import concepts.dyn.ifaces.PropertyIface;
import concepts.dyn.ifaces.PropertyImpl;
import concepts.dyn.primitives.Set_prim;

/**
 * Premise, that bears a set of cids. Only one of them is a selected member of group.
 * @author su
 */
public final class PrimusInterPares_prem extends Set_prim implements ActivationIface, PropertyIface {

    /**
     * Default constructor.
     */
    public PrimusInterPares_prem() {
        
    }
    
    /** 
     * Constructor.
     * @param cpt
     * @param primus
     */ 
    public PrimusInterPares_prem(Concept[] cpt, Concept primus) { 
        set_members(cpt);
        set_primus(primus);
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
     * @param cpt 
     */
    public void set_primus(Concept cpt) {
        if      // activeCid is not in the set?
                (!contains_member(cpt))
            throw new Crash("primusCid " + cpt.get_cid() + " is not a member of the group.");
        
        primusCid = cpt.get_cid();
    }

    @Override
    public NormalizationType get_normalization_type() {
        return NormalizationType.BIN;
    }
   
    @Override
    public float get_activation() {
        return primusCid == 0? -1: 1;
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
    public float calculate_activation(ConceptNameSpace caldron) {
        throw new Crash("Is not realised for this concept.");
    }

    @Override
    public float normalize_activation() {
        throw new Crash("Is not realised for this concept.");
    }
    
    @Override
    public int property_size() {
            return propertieS.property_size();
    }

    @Override
    public long[] get_properties() {
        return propertieS.get_properties();
    }

    @Override
    public boolean add_property(Concept cpt) {
        return propertieS.add_property(cpt);
    }

    @Override
    public boolean remove_property(Concept cpt) {
        return propertieS.remove_property(cpt);
    }

    @Override
    public void set_properties(Concept[] concepts) {
        propertieS.set_properties(concepts);
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%
    
    /** The active cid. The rest of the cids are antiactive. */
    private long primusCid;
    
    /** Set of cids, defining pertinent data . The cids are not forbidden to be duplicated in the premises. */
    private PropertyImpl propertieS = new PropertyImpl();
}
