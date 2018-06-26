package concepts.dyn;

import attention.ConceptNameSpace;
import concepts.dyn.parts.PropertyIface;
import concepts.dyn.parts.EffectIface;
import auxiliary.ActionSelector;
import auxiliary.Premise;
import chris.Glob;
import concepts.*;
import concepts.dyn.parts.ActionIface;
import concepts.dyn.parts.ActivationIface;
import java.util.Arrays;
import concepts.dyn.parts.EvaluationIface;
import concepts.dyn.parts.PremiseIface;

/**
 * It is a concept capable of reasoning, i.e. calculating activation as the weighted sum of premises.
 * The same way it determines successors and their activations. 
 * @author su
 */
public class Neuron extends DynamicConcept implements EvaluationIface, ActionIface, PremiseIface, EffectIface, PropertyIface {

    /**
     * Default constructor.
     */
    public Neuron() {
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
        Neuron clon = (Neuron)super.clone();
        if (propertieS != null) clon.propertieS = Arrays.copyOf(propertieS, propertieS.length);
        if (effectS != null) clon.effectS = Arrays.copyOf(effectS, effectS.length);
        if (premiseS != null) clon.premiseS = Arrays.copyOf(premiseS, premiseS.length);
        
        return clon;
    }

    /**
     * Do weighing, determine activation, do actions, determine possible effects.
     * As a side effect of the assessment an action of the concept may raise the caldron's
     * flag "stopReasoningRequested".
     * @param caldron a caldron in which this assess takes place.
     */
    @Override
    public void calculate_signum_activation_and_do_actions(ConceptNameSpace caldron) {
        float activation = calculate_signum_activation(caldron);
        long[] actions = get_actions(activation);
        if      // is there actions?
                (actions != null)
            //yes: do actions. after that effects are valid.
            for(long actCid: actions) {
                ((Action)caldron.get_cpt(actCid)).go(caldron);
            }
    }

    /**
     * Calculate weighed sum, normalize it as the signum function.
     * @param caldron
     * @return 
     */
    @Override
    public float calculate_signum_activation(ConceptNameSpace caldron) {
        
        // calculate the weighted sum
        double weightedSum = get_bias();
        for(Premise prem: get_premises()) {
            ActivationIface premCpt = (ActivationIface)caldron.get_cpt(prem.cid);
            float activation = premCpt.get_activation();
            float weight = prem.weight;
            weightedSum += weight*activation;
        }
        
        // normalize
        float a = 0;
        if
                (weightedSum < 0)
            a = -1;
        else if
                (weightedSum > 0)
            a = 1;
        set_activation(a);
        
//        // do the normalization
//        if      // normalization needed is not needed?
//                (weightedSum == -1 || weightedSum == 0 || weightedSum == 1)
//            // no, set raw activation
//            set_activation((float)weightedSum);
//        else 
//            // set normalized activation
//            set_activation((float)((1 - Math.exp(-weightedSum))/(1 + Math.exp(-weightedSum))));
        
        return a;
    }
    
    @Override
    public long[] get_actions(float activation) {
        return actioN.get_actions(activation);
    }
        
    @Override
    public void add_action_range(float lowerBoundary, long[] actions) {
        if
                (actioN == null)
            actioN = new ActionSelector();
        
        actioN.add_action_range(lowerBoundary, actions);
    }

    @Override
    public long get_effect(int index) {
        return effectS[index];
    }

    @Override
    public long[] get_effects() {
        return effectS;
    }

    @Override
    public long add_effect(long cid) {
        effectS = Glob.append_array(effectS, cid);
        return cid;
    }

    @Override
    public void set_effects(long[] propArray) {
        effectS = propArray;
    }

    @Override
    public long get_property(int index) {
        return propertieS[index];
    }

    @Override
    public long[] get_properties() {
        return propertieS;
    }

    @Override
    public long add_property(long cid) {
        propertieS = Glob.append_array(propertieS, cid);
        return cid;
    }

    @Override
    public void set_properties(long[] propArray) {
        propertieS = propArray;
    }

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
            
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%
    
    /** Array of actions. */
    private ActionSelector actioN;
    
    /** Array of possible effects. */
    private long[] effectS;
    
    /** Array of cids, defining pertinent data . The cids are not forbidden to be duplicated in the premises. */
    private long[] propertieS;
    
    /** Array of cids and weights of premises. The cids are not forbidden to be duplicated in the properties. */
    private Premise[] premiseS;
    
    /** The free term of the linear expression. */
    private float biaS;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--
    
    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
