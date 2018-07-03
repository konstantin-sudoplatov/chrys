package concepts.dyn.premises;

import attention.ConceptNameSpace;
import chris.Crash;
import chris.Glob;
import concepts.Concept;
import concepts.dyn.ifaces.ActivationIface;
import concepts.dyn.ifaces.PropertyIface;
import concepts.dyn.ifaces.PropertyImpl;
import concepts.dyn.primitives.Set_prim;
import java.util.List;

/**
 * Set of cids, all of them must be active for this premise to be active. No calculation of the activation value is needed, activation
 * is calculated dynamically in the get_activation() method.
 * @author su
 */
public class And_prem extends Set_prim implements ActivationIface, PropertyIface {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** 
     * Constructor.
     */ 
    public And_prem() { 
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
   
    @Override
    public float get_activation() {
        float activation = 1;
        ConceptNameSpace caldron = (ConceptNameSpace)Thread.currentThread();
        for (Long cid: get_members()) {
            ActivationIface cpt = (ActivationIface)caldron.get_cpt(cid);
            if      // is it an antiactive concept?
                    (cpt.get_activation() <= 0)
            {   // our activation will be antiactive also
                activation = -1;
                break;
            }
        }
        
        return activation;
    }

    @Override
    public void set_activation(float activation) {
        throw new Crash("Is not realised for this concept.");
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
    public long add_property(Concept cpt) {
        return propertieS.add_property(cpt);
    }

    @Override
    public void set_properties(Concept[] concepts) {
        propertieS.set_properties(concepts);
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
        Glob.add_list_of_lines(lst, propertieS.to_list_of_lines("propertieS", debugLevel));

        return lst;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%
    
    /** Set of cids, defining pertinent data . The cids are not forbidden to be duplicated in the premises. */
    private PropertyImpl propertieS = new PropertyImpl();

    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--
}
