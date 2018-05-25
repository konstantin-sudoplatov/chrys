package concepts.dyn.primitives;

import concepts.dyn.Primitive;

/**
 * An type of the host concept.
 * @author su
 */
public class ConceptType extends Primitive {
    
    /** The value of the primitive */
    public final String text;

    /** 
     * Constructor.
     * @param text concept type.
     */ 
    public ConceptType(String text) { 
        this.text = text;
    } 
}
