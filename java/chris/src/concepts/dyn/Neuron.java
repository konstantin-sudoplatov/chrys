package concepts.dyn;

import attention.ConceptNameSpace;
import auxiliary.Effects;
import concepts.dyn.ifaces.ActivRangeImpl;
import auxiliary.Lot;
import chris.Glob;
import concepts.*;
import concepts.dyn.ifaces.ActivationIface;
import concepts.dyn.ifaces.ActivationImpl;
import java.util.List;
import concepts.dyn.ifaces.LotIface;
import concepts.dyn.ifaces.ActivRangeIface;
import concepts.dyn.ifaces.LotImpl;

/**
 * It is a concept capable of reasoning, i.e. calculating activation as the weighted sum of premises.
 * The same way it determines successors and their activations. 
 * @author su
 */
abstract public class Neuron extends DynamicConcept implements ActivationIface, ActivRangeIface, LotIface {

    /**
     * Default constructor.
     * @param normType normalization type
     */
    public Neuron(ActivationIface.NormalizationType normType) 
    {
        activatioN = new ActivationImpl(normType);
    }

//    /**
//     * Constructor
//     * @param props array of cids of concept properties. 
//     */
//    public Neuron(long[] props) {
//        this.propertieS = props;
//    }
    
    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    @Override
    public Neuron clone() {
        Neuron clone = (Neuron)super.clone();
//        if (propertieS != null) clon.propertieS = Arrays.copyOf(propertieS, propertieS.length);
        if (activatioN != null) clone.activatioN = (ActivationImpl)activatioN.clone();
        if (rangeS != null) clone.rangeS = (ActivRangeImpl)rangeS.clone();
        if (lotS != null) clone.lotS = (LotImpl)lotS.clone();
        
        return clone;
    }

    @Override
    public NormalizationType get_normalization_type() {
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

    /**
     * Do weighing, determine activation, do actions, determine possible effects.
     * As a side effect of the assessment an action of the concept may raise the caldron's
     * flag "stopReasoningRequested".
     * @param caldron a caldron in which this assess takes place.
     * @return array of ways
     */
    public long[] calculate_activation_and_do_actions(ConceptNameSpace caldron) {
        float activation = calculate_activation(caldron);
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

    /**
     * Calculate weighed sum, normalize it according the neuron's type.
     * @param caldron
     * @return 
     */
    public float calculate_activation(ConceptNameSpace caldron) {
        
        // calculate the weighted sum
        double weightedSum = get_bias();
        for(Lot lot: get_lot()) {
            ActivationIface premCpt = (ActivationIface)caldron.get_cpt(lot.cid);
            float activation = premCpt.get_activation();
            float weight = lot.weight;
            weightedSum += weight*activation;
        }
        activatioN.set_activation((float)weightedSum);
        
        normalize_activation();
        
        return get_activation();
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
//    public int effect_size() {
//        return effectS.effect_size();
//    }
//    
//    @Override
//    public long get_effect(int index) {
//        return effectS.get_effect(index);
//    }
//
//    @Override
//    public long[] get_effects() {
//        return effectS.get_effects();
//    }
//
//    @Override
//    public long add_effect(Concept cpt) {
//        return effectS.add_effect(cpt);
//    }
//
//    @Override
//    public void set_effects(Concept[] concepts) {
//        effectS.set_effects(concepts);
//    }
//
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

    @Override
    public Lot get_lot(int index) {
        return lotS.get_lot(index);
    }

    @Override
    public Lot[] get_lot() {
        return lotS.get_lot();
    }

    @Override
    public Lot add_lot(Lot lot) {
        return lotS.add_lot(lot);
    }

    @Override
    public void set_lots(Lot[] lots) {
        lotS.set_lots(lots);
    }

    @Override
    public float get_bias() {
        return lotS.get_bias();
    }

    @Override
    public void set_bias(float bias) {
        lotS.set_bias(bias);
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
        Glob.add_list_of_lines(lst, rangeS.to_list_of_lines("rangeS", debugLevel));
        Glob.add_list_of_lines(lst, lotS.to_list_of_lines("lotS", debugLevel));

        return lst;
    }
    
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //      Protected    Protected    Protected    Protected    Protected    Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$--- protected data $$$---$$$---$$$---$$$---$$$---$$$--

    //---$$$---$$$---$$$---$$$---$$$--- protected methods ---$$$---$$$---$$$---$$$---$$$---
    
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%

    /** Activation.  */
    private ActivationImpl activatioN;  // initialized in constructor
    
    /** Array of actions. */
    private ActivRangeImpl rangeS = new ActivRangeImpl();
    
//    /** Array of cids, defining pertinent data . The cids are not forbidden to be duplicated in the premises. */
//    private long[] propertieS;
    
    /** Weights and premises. */
    private LotImpl lotS = new LotImpl();
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--
    
    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
