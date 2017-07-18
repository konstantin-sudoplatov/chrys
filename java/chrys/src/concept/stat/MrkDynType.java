package concept.stat;

import concept.en.SCid;
import concept.StaticConcept;
import concept.ifc.Marker;

/**
 * Static concept: dynamic type marker. 
 * It identifies a dynamic concept as being of a type, represented by a word.
 * Compiler is unaware of the dynamic types.
 * @author su
 */
public class MrkDynType extends StaticConcept implements Marker {

    //##################################################################################################################
    //                                              Constructors
    public MrkDynType()
    {   super(SCid.MrkDynType.ordinal());
    }
}
