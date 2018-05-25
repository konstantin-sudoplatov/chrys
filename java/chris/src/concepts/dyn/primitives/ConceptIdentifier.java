package concepts.dyn.primitives;

import concepts.dyn.Primitive;

/**
 * An identifier of the host concept.
 * @author su
 */
public class ConceptIdentifier extends Primitive {
    
    /** The value of the primitive */
    public final String text;

    /** 
     * Constructor.
     * @param text concept identifier.
     */ 
    public ConceptIdentifier(String text) { 
        this.text = text;
    } 
}
