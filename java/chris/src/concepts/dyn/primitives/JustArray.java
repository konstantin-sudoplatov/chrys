package concepts.dyn.primitives;

import chris.Glob;
import concepts.dyn.Primitive;

/**
 * An array of cids.
 * @author su
 */
public class JustArray extends Primitive {

    /** 
     * Constructor.
     * @param cidArray set of cids.
     */ 
    public JustArray(long[] cidArray) 
    { 
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
    public long add_cid(long cid) {
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
