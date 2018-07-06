package concepts.dyn.premises;

import chris.Crash;
import chris.Glob;
import concepts.Concept;
import concepts.dyn.ifaces.ActivationIface;
import concepts.dyn.ifaces.ActivationIface.NormalizationType;
import concepts.dyn.primitives.Set_prim;
import java.util.List;

/**
 * Premise, that bears a set of cids. Only one of them is a selected member of group.
 * @author su
 */
public final class PrimusInterPares_prem extends Set_prim implements ActivationIface {

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
    public float calculate_activation() {
        throw new Crash("Is not realised for this concept.");
    }

    @Override
    public float normalize_activation() {
        throw new Crash("Is not realised for this concept.");
    }
    
    @Override
    public List<String> to_list_of_lines(String note, Integer debugLevel) {
        List<String> lst = super.to_list_of_lines(note, debugLevel);
        if (debugLevel == 0) {
            Glob.add_line(lst, String.format("primusCid = %s", primusCid));
        }
        else if (debugLevel > 0) {
            Glob.add_list_of_lines(lst, get_name_space().get_cpt(primusCid).
                    to_list_of_lines("primusCid", debugLevel-1));
        }

        return lst;
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
