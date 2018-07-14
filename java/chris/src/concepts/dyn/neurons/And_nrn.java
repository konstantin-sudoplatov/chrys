package concepts.dyn.neurons;

import attention.ConceptNameSpace;
import chris.Glob;
import concepts.dyn.LogicNeuron;
import java.util.List;
import concepts.dyn.ifaces.GetActivationIface;

/**
 * Neuron, that keeps its premises as an array of cids and activates only when all of them 
 * are active.
 * @author su
 */
public class And_nrn extends LogicNeuron {

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
    
    /**
     * Create list of lines, which shows the object's content. For debugging. Invoked from Glob.print().
     * @param note printed in the first line just after the object type.
     * @param debugLevel 0 - the shortest, 2 - the fullest
     * @return list of lines, describing this object.
     */
    @Override
    public List<String> to_list_of_lines(String note, Integer debugLevel) {
        List<String> lst = super.to_list_of_lines(note, debugLevel);
        Glob.add_line(lst, String.format("_calculateActivation_ = %s", _calculateActivation_(this.name_space)));

        return lst;
    }
    
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //      Protected    Protected    Protected    Protected    Protected    Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$--- protected data $$$---$$$---$$$---$$$---$$$---$$$--
    
    //---$$$---$$$---$$$---$$$---$$$--- protected methods ---$$$---$$$---$$$---$$$---$$$---

    @Override
    protected float _calculateActivation_(ConceptNameSpace caldron) {
        _activation_ = 1;
        for (long cid: get_premises()) {
            GetActivationIface cpt = (GetActivationIface)caldron.load_cpt(cid);
            if      // is it an antiactive concept?
                    (cpt.get_activation() <= 0)
            {   // our activation will be antiactive also
                _activation_ = -1;
                break;
            }
        }
        
        return _activation_;
    }
}   // class
