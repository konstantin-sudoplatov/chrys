package concepts.dyn.primitives.specializations;

import concepts.StatCptEnum;
import concepts.dyn.primitives.CiddedArray;

/**
 * Unordered group of cids.
 * @author su
 */
public class UnorderedGroup extends CiddedArray {
    
    /**
     * Constructor.
     * @param cidArray 
     */
    public UnorderedGroup(long[] cidArray) {
        super(StatCptEnum.Mrk_UnorderedListOfCids.ordinal(), cidArray);
    }
}
