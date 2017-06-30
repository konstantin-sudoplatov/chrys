package concept.dyn;

import concept.Concept;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author su
 */
public class DynamicConcept extends Concept {
    //##################################################################################################################
    //                                              Public data
    /** Related concepts. */
    public final Map<Long, List<Long>> heap;

    //##################################################################################################################
    //                                              Constructors

    /**
     * Constructor.
     * Construct concept with empty heap.
     */
    public DynamicConcept() {
        heap = new HashMap(2);
    }
    
    /** 
     * Constructor.
     * Construct concept with a given heap.
     * @param heap
     */ 
    public DynamicConcept(Map<Long, List<Long>> heap) {
        this.heap = heap;
    } 

    //##################################################################################################################
    //                                              Public methods

    /**
     * Add a concept to the heap.
     * @param cptId id of the concept
     * @param args list of arguments to the concept. Elements of the list are id's of other concepts.
     */
    public void add_cpt(Long cptId, List<Long> args) {
        heap.put(cptId, args);
    }

    //##################################################################################################################
    //                                              Private methods, data
}
