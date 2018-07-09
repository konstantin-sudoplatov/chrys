package concepts.dyn.neurons;

import attention.ConceptNameSpace;
import auxiliary.Effects;
import chris.Crash;
import chris.Glob;
import concepts.dyn.Action;
import concepts.dyn.Neuron;

/**
 * It is a degenerate, capable only of applying its effects without consulting any premises. Its activation is always 1. Ideal for seeds.
 * @author su
 */
public class Uncondidional_nrn extends Neuron {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** 
     * Constructor.
     */ 
    public Uncondidional_nrn() { 
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
    public void add_action(Action action) {
        Effects effs = _ranges_.select_effects(0);
        effs.actions = Glob.append_array(effs.actions, action.get_cid());
    }

    /**
     * add new way to the array of ways.
     * @param way 
     */
    public void add_way(Neuron way) {
        Effects effs = _ranges_.select_effects(0);
        effs.ways = Glob.append_array(effs.ways, way.get_cid());
    }
    
    @Override
    public Effects select_effects(float activation) {
        throw new Crash("Not supported here");
    }
        
    @Override
    public void add_effects(float lowerBoundary, long[] actions, long[] ways) {
        throw new Crash("Not supported here");
    }

    @Override
    public void add_effects(float lowerBoundary, Action action, Neuron way) {
        throw new Crash("Not supported here");
    }

    @Override
    public void add_effects(float lowerBoundary, long[] actions, Neuron way) {
        throw new Crash("Not supported here");
    }

    @Override
    public void add_effects(float lowerBoundary, Action action, long[] ways) {
        throw new Crash("Not supported here");
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
