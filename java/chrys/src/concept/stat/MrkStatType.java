package concept.stat;

import concept.en.SCid;
import concept.StaticConcept;

/**
 * Static concept: static type marker.
 * It identifies a dynamic concept as being of a static type. All static types are represented by static concepts and so are
 * known at the compile time.
 * @author su
 */
public class MrkStatType extends StaticConcept {

    //##################################################################################################################
    //                                              Constructors
    public MrkStatType()
    {   super(SCid.MrkStatType.ordinal());
    }
}
