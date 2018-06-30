package concepts.dyn.premises;

import chris.Glob;
import concepts.Concept;
import concepts.dyn.ifaces.ActivationIface;
import concepts.dyn.ifaces.ActivationImpl;
import concepts.dyn.ifaces.PropertyIface;
import concepts.dyn.ifaces.PropertyImpl;
import concepts.dyn.primitives.String_prim;
import java.util.List;

/**
 * Premise, that bears a string. First used by "DynCptName.line_of_chat_string_prem".
 * If active (+1), the string has a meaningful value, if antiactive (-1), then string is
 * undefined.
 * @author su
 */
public class String_prem extends String_prim implements ActivationIface, PropertyIface {

    /** 
     * Constructor.
     * @param string
     */ 
    public String_prem(String string) { super(string); } 
    
    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    @Override
    public ActivationIface.NormalizationType get_normalization_type() {
        return activatioN.get_normalization_type();
    }
   
    @Override
    public float get_activation() {
        return activatioN.get_activation();
    }

    @Override
    public void set_activation(float activation) {
        activatioN.set_activation(activation);
    }

    @Override
    public float normalize_activation() {
        return activatioN.normalize_activation();
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
        Glob.add_list_of_lines(lst, activatioN.to_list_of_lines("activatioN", debugLevel));
        Glob.add_list_of_lines(lst, propertieS.to_list_of_lines("propertieS", debugLevel));

        return lst;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%

    /** Activation.  */
    private ActivationImpl activatioN = new ActivationImpl(ActivationIface.NormalizationType.BIN);
    
    /** Set of cids, defining pertinent data . The cids are not forbidden to be duplicated in the premises. */
    private PropertyImpl propertieS = new PropertyImpl();
}
