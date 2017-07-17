package concept.dyn;

import concept.Concept;

/**
 *                  The simplest dynamic concept. 
 * This is an object, which refers to a single static concept marker, like CptName, or CptType, or Word and holds 
 * a single value of string type, which contains the argument to the static concept.
 * Initially this type of dynamic concept was needed for creating vocabularies.
 * @author su
 */
public class PrimitiveStringCpt extends Concept {
    //##################################################################################################################
    //                                              Public data
    public final long stat_cpt_cid;
    public final String str;
    //##################################################################################################################
    //                                              Constructors

    /** 
     * Constructor.
     * @param statCpt static concept ID
     * @param str string
     */ 
    public PrimitiveStringCpt(long statCpt, String str) { 
        this.stat_cpt_cid = statCpt;
        this.str = str;
    } 
}
