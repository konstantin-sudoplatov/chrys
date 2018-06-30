package concepts.dyn;

import attention.ConceptNameSpace;
import concepts.dyn.ifaces.EffectIface;
import concepts.dyn.ifaces.ActionRangeImpl;
import auxiliary.Premise;
import chris.Glob;
import concepts.*;
import concepts.dyn.ifaces.ActivationIface;
import java.util.Arrays;
import concepts.dyn.ifaces.EvaluationIface;
import concepts.dyn.ifaces.PremiseIface;
import concepts.dyn.ifaces.ActionRangeIface;
import concepts.dyn.ifaces.ActivationImpl;
import concepts.dyn.ifaces.EffectImpl;
import java.util.List;

/**
 * It is a concept capable of reasoning, i.e. calculating activation as the weighted sum of premises.
 * The same way it determines successors and their activations. 
 * @author su
 */
abstract public class Neuron extends DynamicConcept implements ActivationIface, EvaluationIface, ActionRangeIface, PremiseIface, EffectIface {

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
        if (effectS != null) clone.effectS = (EffectImpl)effectS.clone();
        if (premiseS != null) clone.premiseS = Arrays.copyOf(premiseS, premiseS.length);
        
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
     */
    @Override
    public void calculate_activation_and_do_actions(ConceptNameSpace caldron) {
        float activation = calculate_activation(caldron);
        long[] actions = get_action_range(activation);
        if      // is there actions?
                (actions != null)
            //yes: do actions. after that effects are valid.
            for(long actCid: actions) {
                ((Action)caldron.get_cpt(actCid)).go(caldron);
            }
    }

    /**
     * Calculate weighed sum, normalize it according the neuron's type.
     * @param caldron
     * @return 
     */
    @Override
    public float calculate_activation(ConceptNameSpace caldron) {
        
        // calculate the weighted sum
        double weightedSum = get_bias();
        for(Premise prem: get_premises()) {
            ActivationIface premCpt = (ActivationIface)caldron.get_cpt(prem.cid);
            float activation = premCpt.get_activation();
            float weight = prem.weight;
            weightedSum += weight*activation;
        }
        activatioN.set_activation((float)weightedSum);
        
        normalize_activation();
        
        return get_activation();
    }
    
    @Override
    public long[] get_action_range(float activation) {
        return actioN.get_action_range(activation);
    }
        
    @Override
    public void add_action_range(float lowerBoundary, long[] actions) {
        actioN.add_action_range(lowerBoundary, actions);
    }

    @Override
    public int effect_size() {
        return effectS.effect_size();
    }
    
    @Override
    public long get_effect(int index) {
        return effectS.get_effect(index);
    }

    @Override
    public long[] get_effects() {
        return effectS.get_effects();
    }

    @Override
    public long add_effect(Concept cpt) {
        return effectS.add_effect(cpt);
    }

    @Override
    public void set_effects(Concept[] concepts) {
        effectS.set_effects(concepts);
    }
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
    public Premise get_premise(int index) {
        return premiseS[index];
    }

    @Override
    public Premise[] get_premises() {
        return premiseS;
    }

    @Override
    public Premise add_premise(Premise premise) {
        premiseS = (Premise[])Glob.append_array(premiseS, premise);
        return premise;
    }

    @Override
    public void set_premises(Premise[] premiseArray) {
        premiseS = premiseArray;
    }

    @Override
    public float get_bias() {
        return biaS;
    }

    @Override
    public void set_bias(float bias) {
        biaS = bias;
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
        Glob.add_list_of_lines(lst, actioN.to_list_of_lines("actioN", debugLevel));
        Glob.add_list_of_lines(lst, effectS.to_list_of_lines("effectS", debugLevel));
        for(Premise prm: premiseS)
            Glob.add_list_of_lines(lst, prm.to_list_of_lines("premiseS", debugLevel));
        Glob.add_line(lst, String.format("biaS = %s", biaS));

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
    private ActivationImpl activatioN;
    
    /** Array of actions. */
    private ActionRangeImpl actioN = new ActionRangeImpl();
    
    /** Array of possible effects. */
    private EffectImpl effectS = new EffectImpl();
    
//    /** Array of cids, defining pertinent data . The cids are not forbidden to be duplicated in the premises. */
//    private long[] propertieS;
    
    /** Array of cids and weights of premises. The cids are not forbidden to be duplicated in the properties. */
    private Premise[] premiseS;
    
    /** The free term of the linear expression. */
    private float biaS;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--
    
    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
