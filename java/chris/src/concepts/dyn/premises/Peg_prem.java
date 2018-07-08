package concepts.dyn.premises;

import chris.Glob;
import concepts.dyn.Primitive;
import concepts.dyn.ifaces.ActivationIface.NormalizationType;
import concepts.dyn.ifaces.BinActivationIface;
import java.util.List;

/**
 * The simplest premise. It is only the cid, that bears information for this concept.
 * @author su
 */
public class Peg_prem extends Primitive implements BinActivationIface {

    /** 
     * Constructor.
     */ 
    public Peg_prem() { 
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
        return activatioN;
    }

    @Override
    public void activate() {
        activatioN = 1;
    }

    @Override
    public void antiactivate() {
        activatioN = -1;
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
        Glob.append_last_line(lst, String.format("activatioN", activatioN));

        return lst;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%

    /** Activation.  */
    private float activatioN = -1;  // antiactive until set otherwise.
}
