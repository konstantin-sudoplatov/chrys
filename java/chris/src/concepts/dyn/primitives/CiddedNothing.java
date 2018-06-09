package concepts.dyn.primitives;

import concepts.dyn.Primitive;

/**
 * Cidded nothing. It contains a single property concept as a cid. Meaning of the property can be 
 * anything. For example, the property can be a marker from the StatCptEnum markers, then 
 * the nest would become a MarkedNothig.
 * @author su
 */
public class CiddedNothing extends Primitive {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** Constructor.
     * @param nestedCid 
     */
    public CiddedNothing(long nestedCid) {
        this.nestedCid = nestedCid;
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
    public long get_nested_cid() {
        return nestedCid;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data ---%%%---%%%---%%%---%%%---%%%---%%%

    /** Cid of the contained concept */
    private long nestedCid;

}   // class
