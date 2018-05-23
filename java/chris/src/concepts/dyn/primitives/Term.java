package concepts.dyn.primitives;

import concepts.dyn.Primitive;

/**
 * A string as a group of symbols without special symbols and white spaces. Can represent words, parts of words, numbers
 * and other symbolic names.
 * @author su
 */
public class Term extends Primitive {

    public final String text;

    /** 
     * Constructor.
     * @param cid
     * @param text string of symbols without white spaces.
     */ 
    public Term(long cid, String text) { 
        super(cid);
        this.text = text;
    } 
}
