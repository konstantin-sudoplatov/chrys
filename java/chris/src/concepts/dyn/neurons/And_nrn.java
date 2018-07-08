package concepts.dyn.neurons;

import chris.Glob;
import concepts.Concept;
import concepts.dyn.Neuron;
import concepts.dyn.ifaces.ActivationIface;
import java.util.List;

/**
 * Neuron, that keeps its premises as an array of cids and activates only when all of them 
 * are active.
 * @author su
 */
public class And_nrn extends Neuron {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** Constructor. */
    public And_nrn() {
    }

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                            Public
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    @Override
    public And_nrn clone() {
        And_nrn clone = (And_nrn)super.clone();
        if (premiseCids != null) clone.premiseCids = premiseCids.clone();
        
        return clone;
    }
    
    @Override
    public NormalizationType get_normalization_type() {
        return NormalizationType.BIN;
    }
    
    /**
     * Add new element to the array of premises.
     * @param prem 
     */
    public void add_premise(ActivationIface prem) {
        Glob.append_array(premiseCids, ((Concept)prem).get_cid());
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
        if (debugLevel == 0)
            Glob.add_line(lst, String.format("premiseCids.length = %s", premiseCids.length));
        else if(debugLevel > 0) {
            Glob.add_list_of_lines(lst, "premiseCids[]", premiseCids, debugLevel-1);
        }

        return lst;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data ---%%%---%%%---%%%---%%%---%%%---%%%

    /** Premises. Must implement the ActivationIface interface. */
    private long[] premiseCids;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
   
}   // class
