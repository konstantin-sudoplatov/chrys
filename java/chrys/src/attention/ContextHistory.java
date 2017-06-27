package attention;

import concept.Concept;
import java.util.ArrayList;

/**
 *
 * @author su
 */
public class ContextHistory {
    //##################################################################################################################
    //                                              Public data

    //##################################################################################################################
    //                                              Constructors

    /**
     * Empty constructor.
     */
    public ContextHistory() {
        contextHist = new ArrayList(1);
        contextHist.add(new ArrayList(1));
    }
    
    /**
     * Constructor.
     * Put a concept as a single element of the first context in the list.
     * @param cpt
     */
    public ContextHistory(Concept cpt) {
        this();
        contextHist.get(0).add(cpt);
    }

    //##################################################################################################################
    //                                              Public methods
    /**
     * Get the last context in the history.
     * @return
     */
    public ArrayList<Concept> get_current_context() {
        return contextHist.get(contextHist.size()-1);
    }
    
    /**
     * Get a context from the history by index.
     * @param idx
     * @return
     */
    public ArrayList<Concept> get_context(int idx) {
        return contextHist.get(idx);
    }
    
    //##################################################################################################################
    //                                              Private methods, data
    private ArrayList<ArrayList<Concept>> contextHist;
}
