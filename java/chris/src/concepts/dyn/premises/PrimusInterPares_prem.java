package concepts.dyn.premises;

import chris.Crash;
import chris.Glob;
import concepts.Concept;
import concepts.dyn.Primitive;
import concepts.dyn.ifaces.GetActivationIface.NormalizationType;
import concepts.dyn.primitives.Set_prim;
import java.util.List;
import concepts.dyn.ifaces.GetActivationIface;

/**
 * Premise, that bears a set of cids. Only one of them is a selected member of group and
 * then this premise gets active (before the selection it is antiactive). To avoid ambiguity
 * member concepts should not implement the activation interface (must be primitives).
 * @author su
 */
public final class PrimusInterPares_prem extends Set_prim implements GetActivationIface {

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
    public PrimusInterPares_prem(Primitive[] cpt, Primitive primus) { 
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
    
    @Override
    public List<String> to_list_of_lines(String note, Integer debugLevel) {
        List<String> lst = super.to_list_of_lines(note, debugLevel);
        if (debugLevel == 0) {
            Glob.add_line(lst, String.format("get_activation() = %s", get_activation()));
            Glob.append_last_line(lst, String.format(", primusCid = %s", primusCid));
        }
        else if (debugLevel > 0) {
            if      // does the concept exist?
                    (name_space.cpt_exists(primusCid))
            {
                Glob.add_line(lst, String.format("get_activation() = %s", get_activation()));
                Glob.add_list_of_lines(lst, name_space.load_cpt(primusCid).
                        to_list_of_lines("primusCid", debugLevel-1));
            }
            else 
                Glob.add_line(lst, String.format("get_activation() = %s, primusCid = null", get_activation()));
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
