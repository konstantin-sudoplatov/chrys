package concepts.dyn;

import attention.ConceptNameSpace;
import auxiliary.Effects;
import concepts.dyn.ifaces.ActivRangeImpl;
import auxiliary.Lot;
import chris.BaseMessageLoop;
import chris.Crash;
import chris.Glob;
import concepts.*;
import concepts.dyn.ifaces.ActivationIface;
import java.util.List;
import concepts.dyn.ifaces.LotIface;
import concepts.dyn.ifaces.ActivRangeIface;
import concepts.dyn.ifaces.LotImpl;

/**
 * It is a concept capable of reasoning, i.e. calculating activation as the weighted sum of premises.
 * The same way it determines successors and their activations. 
 * @author su
 */
public abstract class Neuron extends DynamicConcept implements ActivationIface, ActivRangeIface, LotIface {

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
        if (rangeS != null) clone.rangeS = (ActivRangeImpl)rangeS.clone();
        
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
        if (this.get_name_space() != caldron)
            throw new Crash(String.format("Wrong caldron for this neuron.\n%s\n%s\nNeuron:\n%s", 
                    Glob.list_to_listln(((BaseMessageLoop)caldron).to_list_of_lines("must be", 10)),
                    Glob.list_to_listln(((BaseMessageLoop)this.get_name_space()).to_list_of_lines("really is", 10)),
                    Glob.list_to_listln(this.to_list_of_lines("", 10)))
            );
        
        float activation = _calculateActivation_(caldron);
        Effects effects = get_effects(activation);
        if      // is there actions?
                (effects != null && effects.actions != null)
            //yes: do actions. after that effects are valid.
            for(long actCid: effects.actions) {
                ((Action)caldron.get_cpt(actCid)).go(caldron);
            }
        
        if
                (effects == null)
            return null;
        else
            return effects.ways;
    }
    
    @Override
    public Effects get_effects(float activation) {
        return rangeS.get_effects(activation);
    }
        
    @Override
    public void add_effects(float lowerBoundary, long[] actions, long[] ways) {
        rangeS.add_effects(lowerBoundary, actions, ways);
    }

    @Override
    public void add_effects(float lowerBoundary, long action, long way) {
        rangeS.add_effects(lowerBoundary, action, way);
    }

    @Override
    public void add_effects(float lowerBoundary, long[] actions, long way) {
        rangeS.add_effects(lowerBoundary, actions, way);
    }

    @Override
    public void add_effects(float lowerBoundary, long action, long[] ways) {
        rangeS.add_effects(lowerBoundary, action, ways);
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
            Glob.add_list_of_lines(lst, rangeS.to_list_of_lines("rangeS", debugLevel-1));
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
    
    /** Array of actions. */
    private ActivRangeImpl rangeS = new ActivRangeImpl();
    
//    /** Array of cids, defining pertinent data . The cids are not forbidden to be duplicated in the premises. */
//    private long[] propertieS;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--
    
    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
