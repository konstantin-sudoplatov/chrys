package attention;

import java.util.ArrayList;

/**
 * Data for taking decision.
 * @author su
 */
public class Caldron {
    //##################################################################################################################
    //                                              Public data

    //##################################################################################################################
    //                                              Constructors

    /** 
     * Constructor.
     */ 
    public Caldron() { 
        contextHistory = new ContextHistory();
        curAssertion = new Assertion(contextHistory.get_current_context());
        assertionHistory = new ArrayList();
        assertionHistory.add(curAssertion);
    } 

    //##################################################################################################################
    //                                              Public methods

    /**
     * Drag concept into current assertion.
     * @param cid
     */
    public void throw_in_cpt(long cid) {
        curAssertion.add_premise(cid);
    }

    //##################################################################################################################
    //                                              Private methods, data
    /** Chronologically ordered sequence of assertions. */
    private ArrayList<Assertion> assertionHistory;
    
    /** Chronologically ordered sequence of contexts relating to the assertions. Assertions reference members of this list. */
    private ContextHistory contextHistory;
    
    /** The freshest in the assertionHistory assertion. */
    private Assertion curAssertion;
    
}
