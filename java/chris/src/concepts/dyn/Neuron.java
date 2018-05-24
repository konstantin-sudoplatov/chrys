package concepts.dyn;

import concepts.*;

/**
 * Base class for the working dynamic concepts as an opposite to primitives.
 * @author su
 */
abstract public class Neuron extends DynamicConcept {
    
    /** Meaning of bits in the flag field. */
    public enum Flag {
        
    }
    
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
     *                      Constructor.
     * @param cid
     */
    public Neuron(long cid) 
    {   super(cid);
    }
    
    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%
    
    /** Bit flags, according to the Flag enum.  */
    private int flaG;
    
    /** Time of creation in seconds since 1970. */
    private int createD;
    
    /** Count of usage(read or write). If -1, then it is > Short.MAX_VALUE. */ 
    private short usageCount;
    
    /** Array of cids, defining meta data. The cids are not forbidden to be duplicated in the premises. */
    long[] metA;
    
    /** Array of possible effects. */
    EffectCandidate[] effectCanditate;
    
    /** Array of cids and weights of premises. The cids are not forbidden to be duplicated in the metadata. */
    Premise[] premisE;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
