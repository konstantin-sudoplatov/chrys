package concept;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Data store for the attention flow thread(s). 
 * It holds all data, which an attention flow needs to do its reasoning, first of all the concept references.
 * @author su
 */
public class AttentionBubble {
    //##################################################################################################################
    //                                              Public types        
    
    //##################################################################################################################
    //                                              Public data
    
    //##################################################################################################################
    //                                              Constructors

    /** 
     *  Constructor.
     * Inserts itself into ComDir.ATB map.
     */ 
    @SuppressWarnings("LeakingThisInConstructor")
    public AttentionBubble() { 
    } 

    //##################################################################################################################
    //                                              Public methods

    public long getBid() {
        return bid;
    }

    public void setBid(long bid) {
        this.bid = bid;
    }

    public Map<Long, Concept> getPrivDir() {
        return privDir;
    }

    public void setPrivDir(Map<Long, Concept> privDir) {
        this.privDir = privDir;
    }
    
    //##################################################################################################################
    //                                              Protected data

    //##################################################################################################################
    //                                              Protected methods

    //##################################################################################################################
    //                                              Private data
    /** Bubble Id. Initialized by an illegal ID to show it is not yet generated. */
    private long bid = -1;

    /** Private concept directory: a concept object by its Id. */
    private Map<Long, Concept> privDir = new HashMap();

    //##################################################################################################################
    //                                              Private methods, data
}
