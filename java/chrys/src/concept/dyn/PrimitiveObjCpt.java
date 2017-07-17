package concept.dyn;

import concept.Concept;

/**
 *                  The simple dynamic concept. 
 * This is a dynamic object, which refers to a single static concept and contains a parameter for it. Parameter is an object
 * of class Object, and the static concept knows what it really is. This is a mechanism of forking of a single static
 * concept into a set of dynamic concepts for which the static concept becomes its type.
 * @author su
 */
public class PrimitiveObjCpt extends Concept {
    //##################################################################################################################
    //                                              Public data
    public final long stat_cpt_cid;
    public final Object spec;
    //##################################################################################################################
    //                                              Constructors

    /** 
     * Constructor.
     * @param statCpt static concept ID
     * @param spec specifications for the concept. The marker knows what it is.
     */ 
    public PrimitiveObjCpt(long statCpt, Object spec) { 
        this.stat_cpt_cid = statCpt;
        this.spec = spec;
    } 
}
