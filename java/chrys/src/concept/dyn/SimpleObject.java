package concept.dyn;

import concept.Concept;
import concept.StaticConcept;
import concept.stat.ConceptType;

/**
 *                  The simplest dynamic concept. 
 * This is an object of certain type with certain specs as a string. The specs are determined by the type static concept.
 * @author su
 */
public class SimpleObject extends Concept {
    //##################################################################################################################
    //                                              Public data
    public final StaticConcept object_type;
    public final String object_specs;
    //##################################################################################################################
    //                                              Constructors

    /** 
     * Constructor.
     * @param objectType type of the concept
     * @param objectSpecs specifications for the concept
     */ 
    public SimpleObject(ConceptType objectType, String objectSpecs) { 
        object_type = objectType;
        object_specs = objectSpecs;
    } 
}
