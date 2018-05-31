package concepts.dyn.primitives;

import concepts.dyn.Primitive;

/**
 * A set of concepts(rather their cids) with a special meaning. The meaning is defined by the marker.
 * @author su
 */
public class MarkedCidArray extends Primitive {
    
    /** Meaning of the text field. It is a specialized marker or a primitive of MarkedString, 
     containing the Marker concept and a text, that specializes it. */
    public final long marker_cid;
    
    /** Array of cids. Is it ordered, sorted or something else is specified by the marker. */
    public final long[] cid;

    /** 
     * Constructor.
     * @param markerCid meaning of this concept.
     * @param cidArray set of cids.
     */ 
    public MarkedCidArray(long markerCid, long[] cidArray) { 
        this.marker_cid = markerCid;
        this.cid = cidArray;
    } 
}
