package concept.stat;

import concept.en.SCid;
import concept.StaticConcept;

/**
 *                                  Static concept: concept name marker.
 * It is a marker of a static name of a dynamic concept. Static names are known at compile time and all of them can be found in the Snm enum.
 * @author su
 */
public class CptStatName extends StaticConcept {
    //##################################################################################################################
    //                                              Public types        
    
    //##################################################################################################################
    //                                              Public data

    //##################################################################################################################
    //                                              Constructors

    /** 
     * Constructor.
     */ 
    public CptStatName()
    {   super(SCid.CptStatName.ordinal());
    } 

    //##################################################################################################################
    //                                              Public methods
}
