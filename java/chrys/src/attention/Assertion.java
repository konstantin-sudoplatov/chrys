package attention;

import concept.Concept;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Data for taking decision.
 * @author su
 */
public class Assertion {
    //##################################################################################################################
    //                                              Public data

    //##################################################################################################################
    //                                              Constructors

    /** 
     * Constructor.
     * @param context
     */ 
    public Assertion(ArrayList<Concept> context) { 
        this.conText = context;
    } 
    
    //##################################################################################################################
    //                                              Public methods

    /**
     * Add a new premise to the set.
     * @param premise
     */
    public void add_premise(Concept premise) {
        premiSes.add(premise);
    }
    
    /**
     * Come to a conclusion an a set of premises in a given context.
     * @return
     */
    public Assertion make_conclusion() {
        throw new NotImplementedException();
    }
    
    
    //##################################################################################################################
    //                                              Private methods, data
    /** Time of making a conclusion. */
    private Timestamp timestamp; // = new Timestamp(System.currentTimeMillis());
    
    /** List of concepts which serve as a context for the reasoning. */
    private ArrayList<Concept> conText;
    
    /** Set of concept, which serves as premises to the conclusion */
    private Set<Concept> premiSes = new HashSet();
    
}
