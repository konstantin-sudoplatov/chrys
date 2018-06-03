package concepts.dyn.primitives;

import concepts.dyn.Primitive;

/**
 * A concept, which meaning is qualified by a marker.
 * @author su
 */
public class MarkedConcept extends Primitive {
    
    /** Meaning of the concept. It is a specialized marker or a primitive of MarkedString, 
     containing the Marker concept and a cid, that specializes it. */
    public final long marker_cid;
    
    /** The concept to be qualified. */
    public final long cid;

    /** 
     * Constructor.
     * @param markerCid meaning of this concept.
     * @param cid the cid of the concept to be qualified.
     */ 
    public MarkedConcept(long markerCid, long cid) { 
        this.marker_cid = markerCid;
        this.cid = cid;
    } 
}
