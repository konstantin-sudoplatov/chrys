package concepts.dyn.primitives;

import chris.Glob;

/**
 * An array of cids with a special meaning. The meaning is defined by the nested cid.
 * @author su
 */
public class CiddedArray extends CiddedNothing {

    /**
     * Constructor. Array of cids is not allocated (null).
     * @param nestedCid 
     */
    public CiddedArray(long nestedCid) 
    {
        super(nestedCid);
    } 

    /** 
     * Constructor.
     * @param nestedCid meaning of this concept.
     * @param cidArray set of cids.
     */ 
    public CiddedArray(long nestedCid, long[] cidArray) 
    { 
        super(nestedCid);
        this.cidArray = cidArray;
    } 

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                            Public
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Getter.
     * @return 
     */
    public long[] get_cid_array() {
        return cidArray;
    }

    /**
     * Add a cid to the end of the array.
     * @param cid
     * @return added cid.
     */
    public long append_array(long cid) {
        Glob.append_cid_array(cidArray, cid);
        
        return cid;
    }
    
    /**
     * Setter.
     * @param cidArray 
     */
    public void set_cid_array(long[] cidArray) {
        this.cidArray = cidArray;
    }
    
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data ---%%%---%%%---%%%---%%%---%%%---%%%
    
    /** Array of cids. Is it ordered, sorted or something else is specified by the nested cid. */
    private long[] cidArray;

    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--
}
