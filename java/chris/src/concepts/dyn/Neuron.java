package concepts.dyn;

import attention.ConceptNameSpace;
import auxiliary.Effects;
import concepts.dyn.ifaces.ActivRangeImpl;
import chris.Glob;
import concepts.*;
import java.util.List;
import concepts.dyn.ifaces.ActivRangeIface;
import concepts.dyn.ifaces.GetActivationIface;

/**
 * It is a concept capable of reasoning, i.e. calculating activation as the weighted sum of premises.
 * The same way it determines successors and their activations. 
 * @author su
 */
public abstract class Neuron extends DynamicConcept implements GetActivationIface, ActivRangeIface {

    /**
     * Default constructor.
     */
    public Neuron() 
    {
    }
    
    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    @Override
    public Neuron clone() {
        Neuron clone = (Neuron)super.clone();
//        if (propertieS != null) clon.propertieS = Arrays.copyOf(propertieS, propertieS.length);
        if (_ranges_ != null) clone._ranges_ = (ActivRangeImpl)_ranges_.clone();
        
        return clone;
    }

    @Override
    public abstract NormalizationType get_normalization_type();
    
    @Override
    public float get_activation() {
        return _activation_;
    }

    /**
     * Do weighing, determine activation, do actions, determine possible effects.
     * As a side effect of the assessment an action of the concept may raise the caldron's
     * flag "stopReasoningRequested".
     * @param caldron a caldron in which this assess takes place.
     * @return array of ways
     */
    public long[] calculate_activation_and_do_actions(ConceptNameSpace caldron) {
        
        // Check that this neuron belongs to the caldron
        assert this.name_space != caldron: "Wrong caldron for this neuron";
        
        float activation = _calculateActivation_(caldron);
        Effects effects = select_effects(activation);
        if      // are there actions?
                (effects != null && effects.actions != null)
            //yes: do actions. after that effects are valid.
            for(long actCid: effects.actions) {
                ((Action)caldron.load_cpt(actCid)).go(caldron);
            }
        
        if      // are there any effects?
                (effects != null)
            //yes: return branches
            return effects.branches;
        else //no: return null, the caldron will be waiting
            return null;
    }
    
    @Override
    public Effects select_effects(float activation) {
        return _ranges_.select_effects(activation);
    }
        
    @Override
    public void add_effects(float lowerBoundary, long[] actions, long[] ways) {
        _ranges_.add_effects(lowerBoundary, actions, ways);
    }

    @Override
    public void add_effects(float lowerBoundary, Action action, Neuron way) {
        _ranges_.add_effects(lowerBoundary, action, way);
    }

    @Override
    public void add_effects(float lowerBoundary, long[] actions, Neuron way) {
        _ranges_.add_effects(lowerBoundary, actions, way);
    }

    @Override
    public void add_effects(float lowerBoundary, Action action, long[] ways) {
        _ranges_.add_effects(lowerBoundary, action, ways);
    }

    @Override
    public void add_effects(float lowerBoundary, Action action) {
        _ranges_.add_effects(lowerBoundary, action);
    }

    @Override
    public void append_action(float activation, Action action) {
        _ranges_.append_action(activation, action);
    }

    @Override
    public void append_branch(float activation, Neuron way) {
        _ranges_.append_branch(activation, way);
    }

//    @Override
//    public long get_property(int index) {
//        return propertieS[index];
//    }
//
//    @Override
//    public long[] get_properties() {
//        return propertieS;
//    }
//
//    @Override
//    public long add_property(long cid) {
//        propertieS = Glob.append_array(propertieS, cid);
//        return cid;
//    }
//
//    @Override
//    public void set_properties(long[] propArray) {
//        propertieS = propArray;
//    }
        
    /**
     * Create list of lines, which shows the object's content. For debugging. Invoked from Glob.print().
     * @param note printed in the first line just after the object type.
     * @param debugLevel 0 - the shortest, 2 - the fullest
     * @return list of lines, describing this object.
     */
    @Override
    public List<String> to_list_of_lines(String note, Integer debugLevel) {
        List<String> lst = super.to_list_of_lines(note, debugLevel);
        Glob.append_last_line(lst, String.format(", _activation_ = %s", _activation_));
        if(debugLevel > 0) {
            Glob.add_list_of_lines(lst, _ranges_.to_list_of_lines("rangeS", debugLevel-1));
        }

        return lst;
    }
    
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //      Protected    Protected    Protected    Protected    Protected    Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$--- protected data $$$---$$$---$$$---$$$---$$$---$$$--

    /** Activation.  */
    protected float _activation_ = -1;
    
    /** Array of actions. */
    protected ActivRangeImpl _ranges_ = new ActivRangeImpl();
    
    //---$$$---$$$---$$$---$$$---$$$--- protected methods ---$$$---$$$---$$$---$$$---$$$---

    /**
     * Calculate it.
     * @param caldron
     * @return 
     */
    protected abstract float _calculateActivation_(ConceptNameSpace caldron);
    
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%
    
//    /** Array of cids, defining pertinent data . The cids are not forbidden to be duplicated in the premises. */
//    private long[] propertieS;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--
    
    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
