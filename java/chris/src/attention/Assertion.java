package attention;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
     * @param cid cid of a premise.
     * @return added cid
     */
    public long add_premise(long cid) {
        premiseS.add(cid);
        return cid;
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
    private final List<Long> premiseS = new ArrayList();
    
}
