package concepts.dyn.primitives.special;

import concepts.StatCptEnum;
import concepts.dyn.primitives.MarkedConcept;

/**
 * Marker, which meaning is specified by an arbitrary concept (or rather, by its cid).
 * @author su
 */
public class Unimarker extends MarkedConcept {

    /** 
     * Constructor.
     * @param cid the cid of the qualifying concept.
     */ 
    public Unimarker(long cid) 
    { 
        super(StatCptEnum.Mrk_Unimarker.ordinal(), cid);
    } 
}
