package concepts.dyn;

import concepts.*;
import java.util.Arrays;

/**
 * A set of premises, vector of weights, activation and set of effects.
 * @author su
 */
public class Neuron extends DynamicConcept {
    
    /** Structure of premises. A pair of weight of a concept and its cid. */
    public static class Premise {
        public float weight;    // Weight with which this cid takes part in the weighted sum.
        public long cid;
        public Premise(float weight, long cid) {
            this.weight = weight;
            this.cid = cid;
        }
    }

    /**
     * Default constructor.
     */
    public Neuron() {
    }

    /**
     * Constructor
     * @param props array of cids of concept properties. 
     */
    public Neuron(long[] props) {
        this.propertyCid = props;
    }
    
    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    @Override
    public Neuron clone() {
        Neuron clon = (Neuron)super.clone();
        if (actionCid != null) clon.actionCid = Arrays.copyOf(actionCid, actionCid.length);
        if (propertyCid != null) clon.propertyCid = Arrays.copyOf(propertyCid, propertyCid.length);
        if (effectCid != null) clon.effectCid = Arrays.copyOf(effectCid, effectCid.length);
        if (premisE != null) clon.premisE = Arrays.copyOf(premisE, premisE.length);
        
        return clon;
    }
    
    /**
     * Getter.
     * @return
     */
    public float get_activation() {
        return activatioN;
    }

    /**
     * Setter.
     * @param activation
     */
    public void set_activation(float activation) {
        this.activatioN = activation;
    }

    /**
     * Getter.
     * @return
     */
    public long[] get_action_cid() {
        return actionCid;
    }

    /**
     * Setter.
     * @param actionCid
     */
    public void set_action_cid(long[] actionCid) {
        this.actionCid = actionCid;
    }

    /**
     * Getter.
     * @return
     */
    public long[] get_effect_cid() {
        return effectCid;
    }

    /**
     * Setter.
     * @param effectCid
     */
    public void set_effect_cid(long[] effectCid) {
        this.effectCid = effectCid;
    }

    /**
     * Getter.
     * @return
     */
    public long[] get_property_cid() {
        return propertyCid;
    }

    /**
     * Setter.
     * @param propertyCid
     */
    public void set_property_cid(long[] propertyCid) {
        this.propertyCid = propertyCid;
    }

    /**
     * Getter.
     * @return
     */
    public Premise[] get_premise() {
        return premisE;
    }

    /**
     * Setter.
     * @param premise
     */
    public void set_premise(Premise[] premise) {
        this.premisE = premise;
    }
//    /**
//     * Add a concept to the property array.
//     * @param cid
//     * @return 
//     */
//    public long add_property(long cid) {
//        propertyCid = Arrays.copyOf(propertyCid, propertyCid.length+1);
//        propertyCid[propertyCid.length-1] = cid;
//        return cid;
//    }

    public float get_bias() {
        return biaS;
    }

    public void set_bias(float bias) {
        this.biaS = bias;
    }
            
    
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%

    /** Activation value. */
    private float activatioN;
    
    /** Array of actions. */
    private long[] actionCid;
    
    /** Array of possible effects. */
    private long[] effectCid;
    
    /** Array of cids, defining pertinent data . The cids are not forbidden to be duplicated in the premises. */
    private long[] propertyCid;
    
    /** Array of cids and weights of premises. The cids are not forbidden to be duplicated in the metadata. */
    private Premise[] premisE;
    
    /** The free term of the linear expression. */
    private float biaS;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
