package concepts.dyn.premises;

import chris.Crash;
import chris.Glob;
import concepts.Concept;
import concepts.dyn.ifaces.ActivationIface;
import concepts.dyn.ifaces.ActivationImpl;
import concepts.dyn.primitives.Set_prim;
import java.util.List;

/**
 * Set of cids, at least one of them must be active for this premise to be active. No calculation of the activation value is needed, activation
 * is calculated dynamically in the get_activation() method.
 *
 * @author su
 */
public class Or_prem extends Set_prim implements ActivationIface {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** 
     * Constructor.
     */ 
    public Or_prem() { 
    } 

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^
    
    @Override
    public boolean add_member(Concept cpt) {
        boolean r = super.add_member(cpt);
        if (r) {
            calculate_activation();
            return true;
        }
        else
            return false;
    }

    @Override
    public boolean remove_member(Concept cpt) {
        
        boolean r = super.remove_member(cpt);
        if (r) {
            calculate_activation();
            return true;
        }
        else
            return false;
    }

    @Override
    public NormalizationType get_normalization_type() {
        return NormalizationType.BIN;
    }
   
    @Override
    public float get_activation() {
        return activatioN.get_activation();
    }

    @Override
    public void set_activation(float activation) {
        throw new Crash("Is not realised for this concept.");
    }

    @Override
    public float calculate_activation() {
        activatioN.set_activation(-1);
        for (Long cid: get_members()) {
            ActivationIface cpt = (ActivationIface)this.get_name_space().get_cpt(cid);
            if      // is it an active concept?
                    (cpt.get_activation() > 0)
            {   // our activation will be active also
                activatioN.set_activation(1);
                break;
            }
        }
        
        return activatioN.get_activation();
    }

    @Override
    public float normalize_activation() {
        throw new Crash("Is not realised for this concept.");
    }

    /**
     * Create list of lines, which shows the object's content. For debugging. Invoked from Glob.print().
     * @param note printed in the first line just after the object type.
     * @param debugLevel 0 - the shortest, 2 - the fullest
     * @return list of lines, describing this object.
     */
    @Override
    public List<String> to_list_of_lines(String note, Integer debugLevel) {
        List<String> lst = super.to_list_of_lines(note, debugLevel);
        Glob.append_last_line(lst, String.format("get_activation() = %s", get_activation()));

        return lst;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%
    
    /** Activation.  */
    private ActivationImpl activatioN = new ActivationImpl(this, ActivationType.SET, NormalizationType.BIN);

    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--
}
