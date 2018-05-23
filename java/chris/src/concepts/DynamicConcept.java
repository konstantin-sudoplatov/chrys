package concepts;

/**
 * Base class for dynamic concepts.
 * @author su
 */
abstract public class DynamicConcept extends Concept {

    /**
     *                      Constructor.
     * @param cid
     */
    public DynamicConcept(long cid) 
    {   super();   
        this.ciD = cid;
    }
    
    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Getter.
     * @return concept identifier. 
     */
    public long getCiD() {
        return ciD;
    }

    /**
     * Setter.
     * @param ciD 
     */
    public void setCiD(long ciD) {
        this.ciD = ciD;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%
    /** Concept identifier */
    private long ciD;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
