package concepts.dyn.primitives;

import concepts.dyn.Primitive;

/**
 * An empty primitive with a special meaning, marked nothing (as opposed, say, marked string). 
 * Type of the primitive is given by the marker, but the unique meaning is its cid or, if exists,
 * a concept name. For example, marker can be "color", and the concept name "red". Than that 
 * concept would mean "color: red". Or, if it is a nameless concept, than its meaning would be 
 * just "color: &lt;cid&gt;".
 * @author su
 */
public class MarkedNil extends Primitive {
    
    /** Type of the primitive. It is a specialized marker or a primitive of MarkedString, 
     containing the Marker concept and a text, that specializes it. */
    public final long marker_cid;

    /** 
     * Constructor.
     * @param markerCid type or class of this concept.
     */ 
    public MarkedNil(long markerCid) { 
        this.marker_cid = markerCid;
    } 
}
