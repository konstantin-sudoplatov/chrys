package concepts.dyn;

import concepts.*;
import java.util.Arrays;

/**
 * Base class for the working dynamic concepts as an opposite to primitives.
 * @author su
 */
public class Neuron extends DynamicConcept {
    
    /** A pair of likelihood of a concept to become an effect and its cid */
    public class EffectCandidate {
        public float attraction;    // <count of making the cid an effect>/<count of assertions> 
        public long cid;
    }
    
    /** Structure of premises. A pair of weight of a concept and its cid. */
    public class Premise {
        public float weight;    // Weight with which this cid takes part in the weighted sum.
        public long cid;
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
        this.propertY = props;
    }
    
    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    @Override
    public Neuron clone() {
        Neuron clon = (Neuron)super.clone();
        if (propertY != null) clon.propertY = Arrays.copyOf(propertY, propertY.length);
        if (effectCanditate != null) clon.effectCanditate = Arrays.copyOf(effectCanditate, effectCanditate.length);
        if (premisE != null) clon.premisE = Arrays.copyOf(premisE, premisE.length);
        
        return clon;
    }
    
    /**
     * Add a concept to the property array.
     * @param cid
     * @return 
     */
    public long add_property(long cid) {
        propertY = Arrays.copyOf(propertY, propertY.length+1);
        propertY[propertY.length-1] = cid;
        return cid;
    }
            
    
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%
    
    /** Array of cids, defining pertinent data . The cids are not forbidden to be duplicated in the premises. */
    private long[] propertY;
    
    /** Array of possible effects. */
    private EffectCandidate[] effectCanditate;
    
    /** Array of cids and weights of premises. The cids are not forbidden to be duplicated in the metadata. */
    private Premise[] premisE;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
