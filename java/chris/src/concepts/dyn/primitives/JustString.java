package concepts.dyn.primitives;

import concepts.dyn.Primitive;

/**
 * Any string of text without any limitations.
 * @author su
 */
public class JustString extends Primitive {

    /** The value of the primitive */
    public final JustString text;

    /** 
     * Constructor.
     * @param text string of symbols.
     */ 
    public JustString(JustString text) { 
        this.text = text;
    } 
}
