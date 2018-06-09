package concepts.dyn.primitives.specializations;

import concepts.StatCptEnum;
import concepts.dyn.primitives.CiddedNothing;

/**
 * The simplest premise, just a marked (cidded) nothing.
 * Just an example of using primitives. Not meant to be implemented.
 * @author su
 */
abstract public class ElementaryPremise extends CiddedNothing {

    /** 
     * Constructor.
     */ 
    public ElementaryPremise() { 
        super(StatCptEnum.Mrk_ElementaryPremise.ordinal());
    } 
}
