package attention;

import concept.Concept;
import concept.stat.SCid;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Data store for the attention flow thread(s). 
 * It holds all data, which an attention flow needs to do its reasoning, first of all the concept references.
 * @author su
 */
abstract public class AttnBubble implements Runnable {

    //##################################################################################################################
    //                                              Public types        
    
    //##################################################################################################################
    //                                              Public data
    
    //##################################################################################################################
    //                                              Constructors

    //##################################################################################################################
    //                                              Public methods

//    public long getBid() {
//        return bid;
//    }
//
//    public void setBid(long bid) {
//        this.bid = bid;
//    }
//
//    public Map<Long, Concept> getPrivDir() {
//        return privDir;
//    }
    
    //##################################################################################################################
    //                                              Protected data

    //##################################################################################################################
    //                                              Protected methods

    /**
     *  Find and put a static or dynamic concept into the caldron.
     * @param cid concept id as long
     */
    protected void _lightCpt_(long cid) {
        caldRon.add(cid);
    }

    /**
     *  Find and put a static concept into the caldron.
     * @param cid concept id as enum
     */
    protected void _lightCpt_(SCid cid) {
        _lightCpt_(cid.ordinal());
    }
    
    //##################################################################################################################
    //                                              Private data
    /** Private concept directory: a concept object by its Id. Here we have only dynamic concepts, that were created in the bubble. */
    private Map<Long, Concept> privDir = new HashMap();
    
    /** Concept id's, that should be taken into consideration on reasoning. */
    private Set<Long> caldRon = new HashSet();

    //##################################################################################################################
    //                                              Private methods, data
}
