package concept.stat;

import concept.en.SCid;
import concept.StaticConcept;
import concept.ifc.Marker;

/**
 *                                  Static concept: concept name marker.
 * It is a marker of a dynamic name of a dynamic concept. Dynamic names are unknown to the code.
 * @author su
 */
public class MrkDynName extends StaticConcept implements Marker {
    //##################################################################################################################
    //                                              Public types        
    
    //##################################################################################################################
    //                                              Public data

    //##################################################################################################################
    //                                              Constructors

    /** 
     * Constructor.
     */ 
    public MrkDynName()
    {   super(SCid.MrkDynName.ordinal());
    } 

    //##################################################################################################################
    //                                              Public methods
}
