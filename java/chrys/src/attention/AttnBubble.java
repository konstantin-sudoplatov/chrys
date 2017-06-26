package attention;

import concept.Concept;
import concept.stat.SCid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

    /**
     *  Getter.
     * The map is changed from ComDir.
     * @return private concept directory
     */
    public Map<Long, Concept> getPrivDir() {
        return privDir;
    }
    
    //##################################################################################################################
    //                                              Private data
    /** Private concept directory: a concept object by its Id. 
       Here we have only dynamic concepts, that were created in the bubble.
       Concurrency: the map is never updated by this object. It asks ComDir to do the work centrally. 
       It is made concurrent to prevent reading while being updated. */
    private Map<Long, Concept> privDir = new ConcurrentHashMap();
    
    /** List of concepts which serve as a contexts to the caldron. */
    private List<Long> conText = new ArrayList(1);
    
    /** Concept id's, that should be taken into consideration on reasoning. */
    private Set<Long> caldRon = new HashSet();

    //##################################################################################################################
    //                                              Private methods, data
}
