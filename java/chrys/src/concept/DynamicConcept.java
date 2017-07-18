package concept;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author su
 */
abstract public class DynamicConcept extends Concept {
    //##################################################################################################################
    //                                              Public data
    /** Gives concept processing capabilities. */
    public final Fire fire = new Fire();
    
    /** Gives concept data capabilities. */
    public final Map<Long, List<Long>> properties = new HashMap(0);

    //##################################################################################################################
    //                                              Constructors

    //##################################################################################################################
    //                                              Public methods
    //##################################################################################################################
    //                                              Private methods, data
}
