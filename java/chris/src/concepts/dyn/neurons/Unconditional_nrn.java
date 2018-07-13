package concepts.dyn.neurons;

import attention.ConceptNameSpace;
import concepts.dyn.Action;
import concepts.dyn.Neuron;

/**
 * It is a degenerate, capable only of applying its effects without consulting any premises. Its activation is always 1. Ideal for seeds.
 * @author su
 */
public class Unconditional_nrn extends Neuron {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** 
     * Constructor.
     */ 
    public Unconditional_nrn() { 
        _ranges_.add_effects(Float.NEGATIVE_INFINITY, new long[0], new long[0]);    // placeholder
        _activation_ = 1;
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

    /**
     * Add new action to the array of actions.
     * @param action 
     */
    public void append_action(Action action) {
        _ranges_.append_action(0, action);
    }

    /**
     * Add new branch to the array of branches.
     * @param way 
     */
    public void append_branch(Neuron way) {
        _ranges_.append_branch(0, way);
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
        return _activation_;
    }
}
