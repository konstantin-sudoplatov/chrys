package concept.dyn;

import concept.Concept;

/**
 *                  The simplest dynamic concept. 
 * This is an object, which refers to a single static concept marker, like ConceptName, or ConceptType, or Word and holds 
 * a single value like String, or cid of a concept that holds the value. Type of the value is determined by the static concept.
 * @author su
 */
public class PrimitiveCidCpt extends Concept {
    //##################################################################################################################
    //                                              Public data
    public final long stat_cpt;
    public final long spec_cid;
    //##################################################################################################################
    //                                              Constructors

    /** 
     * Constructor.
     * @param markerCptId marker static concept
     * @param spec specifications for the concept. The marker knows what it is.
     */ 
    public PrimitiveCidCpt(long markerCptId, long spec) { 
        this.stat_cpt = markerCptId;
        this.spec_cid = spec;
    } 
}
