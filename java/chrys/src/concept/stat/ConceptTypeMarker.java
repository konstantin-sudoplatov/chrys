package concept.stat;

import concept.StaticConcept;

/**
 * Static concept: identifies a dynamic concept as being of certain type. Serves as a prefix to another dynamic concept, 
 * which holds the actual type.
 * @author su
 */
public class ConceptTypeMarker extends StaticConcept {

    //##################################################################################################################
    //                                              Constructors
    public ConceptTypeMarker()
    {   super(SCid.ConceptTypeMarker.ordinal());
    }
}
