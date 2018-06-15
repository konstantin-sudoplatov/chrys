package concepts.dyn;

import attention.Caldron;
import auxiliary.ActionSelector;
import auxiliary.Premise;
import chris.Glob;
import concepts.*;
import java.util.Arrays;

/**
 * It is a concept capable of reasoning, i.e. calculating activation as the weighted sum of premises.
 * The same way it determines successors and their activations. 
 * @author su
 */
public class Neuron extends DynamicConcept implements AssessmentIface, EffectIface, PropertyIface {

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
    
    @Override
    public long[] get_actions(float activation) {
        return actioN.get_actions(activation);
    }
        
    @Override
    public void append_action_ranges(float lowerBoundary, long[] actions) {
        if
                (actioN == null)
            actioN = new ActionSelector();
        
        actioN.append_action_ranges(lowerBoundary, actions);
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
