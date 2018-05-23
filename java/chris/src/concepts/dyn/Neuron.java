package concepts.dyn;

import concepts.*;

/**
 * Base class for working dynamic concepts.
 * @author su
 */
abstract public class Neuron extends DynamicConcept {
    
    /** Meaning of bits in the flag field. */
    public enum Flag {
        
    }
    
    /** Structure of premise. */
    public class Premise {
        float weight;
        long cid;
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
    
    /** Time of creation in seconds since 1970 */
    private int createD;
    
    /** Count of usage(read or write). If -1, then it is > Short.MAX_VALUE. */ 
    private short usageCount;
    
    /** Array of cids, defining metadata. The cids are not forbidden to be duplicated in the premises. */
    long[] metA;
    
    /** Array of cids of possible effects. */
    long[] effecT;
    
    /** Array of cids and weights of premises. The cids are not forbidden to be duplicated in the metadata. */
    Premise[] premisE;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
