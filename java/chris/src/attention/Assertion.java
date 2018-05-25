package attention;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * Data for taking decision.
 * It represents a single step in reasoning.
 * @author su
 */
public class Assertion {
    //##################################################################################################################
    //                                              Public data

    //##################################################################################################################
    //                                              Constructors

    /** 
     * Constructor.
     */ 
    public Assertion() { 
    } 
    
    //##################################################################################################################
    //                                              Public methods

    /**
     * Add a new premise to the set.
     * @param premise id of a premise concept
     */
    public void add_premise(long premise) {
        premiSes.add(premise);
    }
    
    /**
     * Come to a conclusion an a set of premises in a given context.
     * @return
     */
    public Assertion make_conclusion() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    //##################################################################################################################
    //                                              Private methods, data

    /** Time of making a conclusion. */
    private final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    
    /** Set of concept, which serves as premises to the conclusion */
    private final Set<Long> premiSes = new HashSet();
    
}
