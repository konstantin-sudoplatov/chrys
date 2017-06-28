package concept.stat;

import concept.en.SCid;
import concept.StaticConcept;

/**
 * Static concept: dynamic type marker. 
 * It identifies a dynamic concept as being of a type, represented by a word.
 * Compiler is unaware of the dynamic types.
 * @author su
 */
public class CptDynType extends StaticConcept {

    //##################################################################################################################
    //                                              Constructors
    public CptDynType()
    {   super(SCid.CptDynType.ordinal());
    }
}
