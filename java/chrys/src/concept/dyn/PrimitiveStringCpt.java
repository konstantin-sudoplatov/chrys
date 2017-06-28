package concept.dyn;

import concept.Concept;

/**
 *                  The simplest dynamic concept. 
 * This is an object, which refers to a single static concept marker, like CptName, or CptType, or Word and holds 
 * a single value of string type, which contains the argument to the static concept.
 * @author su
 */
public class PrimitiveStringCpt extends Concept {
    //##################################################################################################################
    //                                              Public data
    public final long stat_cpt;
    public final String spec_cid;
    //##################################################################################################################
    //                                              Constructors

    /** 
     * Constructor.
     * @param markerCptId marker static concept
     * @param cid specifications for the concept. The marker knows what is it.
     */ 
    public PrimitiveStringCpt(long markerCptId, String cid) { 
        this.stat_cpt = markerCptId;
        this.spec_cid = cid;
    } 
}
