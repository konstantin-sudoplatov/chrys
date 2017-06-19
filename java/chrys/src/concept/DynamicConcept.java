package concept;

import java.util.List;
import java.util.Map;

/**
 *
 * @author su
 */
public class DynamicConcept extends Concept {
    //##################################################################################################################
    //                                              Public data
    public Map<Long, List> what;

    //##################################################################################################################
    //                                              Constructors

    /** 
     * Constructor.
     * @param what
     */ 
    public DynamicConcept(Map<Long, List> what) {
        this.what = what;
    } 

    //##################################################################################################################
    //                                              Public methods

    //##################################################################################################################
    //                                              Private methods, data
}
