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
     * @param statCptId marker static concept
     * @param str specifications for the concept. The marker will know what is it.
     */ 
    public PrimitiveStringCpt(long statCptId, String str) { 
        this.stat_cpt = statCptId;
        this.spec_cid = str;
    } 
}
