package concepts.dyn.primitives;

import concepts.dyn.Primitive;

/**
 * Any string of text without any limitations.
 * @author su
 */
public class Str extends Primitive {

    /** The value of the primitive */
    public final Str text;

    /** 
     * Constructor.
     * @param text string of symbols.
     */ 
    public Str(Str text) { 
        this.text = text;
    } 
}
