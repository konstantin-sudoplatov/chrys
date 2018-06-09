package concepts.dyn.primitives.specializations;

import concepts.dyn.primitives.CiddedNothing;

/**
 * An empty primitive with a special meaning, marked nothing (as opposed, say, marked string). 
 * Type of the primitive is given by the marker, but the unique meaning is its cid or, if exists,
 * a concept name. For example, marker can be "color", and this concept named "red". Than in 
 * would mean "color: red". Or, if it is a nameless concept, than its meaning would be 
 * just "color: &lt;cid&gt;".
 * <p> As the marker can serve just any concept, but most typical case is one of the Mrk_... concepts 
 * from StatCptEnum.
 * 
 * @depricated I will try to use CiddedNothing instead to avoid multiplying entities.
 * @author su
 */
@Deprecated
abstract public class MarkedNothing extends CiddedNothing {

    /** 
     * Constructor.
     * @param markerCid cid of a concept, that serves as a marker. It can be one of the Mrk_... static
     * concepts from StatCptEnum, or just any static or dynamic concept.
     */ 
    public MarkedNothing(long markerCid) 
    { 
        super(markerCid);
    } 

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                            Public
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Getter.
     * @return cid of the marker concept. 
     */
    public long get_marker_cid() {
        return get_nested_cid();
    }
}
