package concept.stat;

import concept.en.SCid;
import concept.StaticConcept;
import concept.ifc.Marker;

/**
 * Static concept: static type marker.
 * It identifies a dynamic concept as being of a given static type. Actual type is expected as a cid.
 * @author su
 */
public class MrkStatType extends StaticConcept implements Marker {

    //##################################################################################################################
    //                                              Constructors
    public MrkStatType()
    {   super(SCid.MrkStatType.ordinal());
    }
}
