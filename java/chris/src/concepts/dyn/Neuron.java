package concepts.dyn;

import attention.ConceptNameSpace;
import concepts.dyn.ifaces.EffectIface;
import auxiliary.ActionSelector;
import auxiliary.Premise;
import chris.Glob;
import concepts.*;
import concepts.dyn.ifaces.ActivationIface;
import java.util.Arrays;
import concepts.dyn.ifaces.EvaluationIface;
import concepts.dyn.ifaces.PremiseIface;
import concepts.dyn.ifaces.ActionRangeIface;

/**
 * It is a concept capable of reasoning, i.e. calculating activation as the weighted sum of premises.
 * The same way it determines successors and their activations. 
 * @author su
 */
abstract public class Neuron extends DynamicConcept implements ActivationIface, EvaluationIface, ActionRangeIface, PremiseIface, EffectIface {

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
//        if (propertieS != null) clon.propertieS = Arrays.copyOf(propertieS, propertieS.length);
        if (effectS != null) clon.effectS = Arrays.copyOf(effectS, effectS.length);
        if (premiseS != null) clon.premiseS = Arrays.copyOf(premiseS, premiseS.length);
        
        return clon;
    }
   
    /**
     * Getter.
     * @return
     */
    @Override
    public float get_activation() {
        return activatioN;
    }

    /**
     * Setter.
     * @param activation
     */
    @Override
    public void set_activation(float activation) {
        activatioN = activation;
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
        activatioN = (float)weightedSum;
        
        _normalize_();
        
        return get_activation();
    }
    
    @Override
    public long[] get_action_range(float activation) {
        return actioN.get_action_range(activation);
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
    public long add_effect(Concept cpt) {
        effectS = Glob.append_array(effectS, cpt.get_cid());
        return cpt.get_cid();
    }

    @Override
    public void set_effects(Concept[] concepts) {
        effectS = new long[concepts.length];
        for(int i=0; i<concepts.length; i++)
            effectS[i] = concepts[i].get_cid();
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
    
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //      Protected    Protected    Protected    Protected    Protected    Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$--- protected data $$$---$$$---$$$---$$$---$$$---$$$--

    //---$$$---$$$---$$$---$$$---$$$--- protected methods ---$$$---$$$---$$$---$$$---$$$---
     
    /**
     * Normalize activation according to its normalization type.
     */
    abstract protected void _normalize_();
    
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%

    /** Activation. Its normalized (squashed) value is from -1 to 1. Activation is not stored in the DB
      and if the concept is not loaded into a name space(caldron) and explicitely changed it is -1. */
    private float activatioN = -1;
    
    /** Array of actions. */
    private ActionSelector actioN;
    
    /** Array of possible effects. */
    private long[] effectS;
    
//    /** Array of cids, defining pertinent data . The cids are not forbidden to be duplicated in the premises. */
//    private long[] propertieS;
    
    /** Array of cids and weights of premises. The cids are not forbidden to be duplicated in the properties. */
    private Premise[] premiseS;
    
    /** The free term of the linear expression. */
    private float biaS;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--
    
    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
