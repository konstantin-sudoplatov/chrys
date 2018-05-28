package concepts.dyn.primitives;

import concepts.dyn.Primitive;

/**
 * A string with a special meaning.
 * @author su
 */
public class MarkedString extends Primitive {
    
    /** Meaning of the text field. It is a specialized marker or a primitive of MarkedString, 
     containing the Marker concept and a text, that specializes it. */
    public final long marker_cid;
    
    /** The value of the primitive */
    public final String text;

    /** 
     * Constructor.
     * @param markerCid meaning of this concept.
     * @param text value of this concept.
     */ 
    public MarkedString(long markerCid, String text) { 
        this.marker_cid = markerCid;
        this.text = text;
    } 
}
