package concepts.dyn.primitives;

import concepts.dyn.Primitive;

/**
 * A string with a special meaning. Used only when there is no other more specialized primitive, like ConceptIdentifier or
 * ConceptType.
 * @author su
 */
public class Term extends Primitive {

    /** Markers, which are known to the compiler. */
    public enum Marker {
        russian_word_form,     // symbolic name of a concept
        english_word_form,           // symbolic designation of the type of concept
    }
    
    /** Meaning of the text field. It is not constricted to the Marker enum, it can be anything that the static concepts will know how to process. */
    public final Str marker;
    
    /** The value of the primitive */
    public final Str text;

    /** 
     * Constructor.
     * @param marker meaning of the text
     * @param text value of the term.
     */ 
    public Term(Str marker, Str text) { 
        this.marker = marker;
        this.text = text;
    } 
}
