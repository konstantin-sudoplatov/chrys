package concepts.dyn.premises;

import chris.Crash;
import chris.Glob;
import concepts.dyn.primitives.String_prim;
import java.util.List;
import concepts.dyn.ifaces.GetActivationIface;

/**
 * Premise, that bears a string. First used by "DynCptName.line_of_chat_string_prem".
 * If active (+1), the string has a meaningful value, if antiactive (-1), then string is
 * undefined.
 * @author su
 */
public class String_prem extends String_prim implements GetActivationIface{

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
    public GetActivationIface.NormalizationType get_normalization_type() {
        return NormalizationType.BIN;
    }
   
    @Override
    public float get_activation() {
        return get_string() == null? -1: 1;
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
        Glob.append_last_line(lst, String.format(", get_activation() = %s", get_activation()));

        return lst;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%
}
