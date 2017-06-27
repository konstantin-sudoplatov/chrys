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

    //##################################################################################################################
    //                                              Private methods, data
    /** Chronologically ordered sequence of assertions. */
    private ArrayList<Assertion> assertionHistory;
    
    /** Chronologically ordered sequence of contexts relating to the assertions. Assertions reference members of this list. */
    private ContextHistory contextHistory;
    
    /** The freshest in the assertionHistory assertion. */
    private Assertion curAssertion;
    
}
