package concepts;

import concepts.dyn.parts.ActivationIface;

/**
 * The dynamic concept is a common predecessor for the primitives and neurons.
 * @author su
 */
abstract public class DynamicConcept extends Concept implements ActivationIface {
    
    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Getter.
     * @return concept identifier. 
     */
    @Override
    public long get_cid() {
        return ciD;
    }

    /**
     * Setter.
     * @param ciD 
     */
    @Override
    public void set_cid(long ciD) {
        this.ciD = ciD;
    }

    /**
     * Getter.
     * @return creation time. 
     */
    public int get_creation_time() {
        return creationTime;
    }

    /**
     * Getter.
     * @return last access time. 
     */
    public int get_last_access_time() {
        return lastAccessTime;
    }

    /**
     * Getter.
     * @return usage counter. 
     */
    public short get_usage_count() {
        return usageCount;
    }

    /**
     * Setter. 
     * @param creationTime
     */
    public void set_creation_time(int creationTime) {
        this.creationTime = creationTime;
    }

    /**
     * Setter. 
     * @param lastAccessTime
     */
    public void set_last_access_time(int lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    /**
     * Setter. 
     * @param usageCount
     */
    public void set_usage_count(short usageCount) {
        this.usageCount = usageCount;
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
        this.activatioN = activation;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%

    /** Concept identifier. */
    private long ciD;
    
    /** Time of creation in seconds since 1970. */
    private int creationTime;
    
    /** Time of the last access in seconds since 1970. */
    private int lastAccessTime;
    
    /** Count of accesses(read or write). If -1, then it is > Short.MAX_VALUE, infinity in a sense. */ 
    private short usageCount;

    /** Activation. Its normalized (squashed) value is from -1 to 1. Activation is not stored in the DB
      and if the concept is not loaded into a name space(caldron) and explicitely changed it is -1. */
    private float activatioN = -1;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
