package attention;

import concept.Concept;
import concept.en.SCid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
//        return cpt;
//    }
    
    //##################################################################################################################
    //                                              Protected data

    //##################################################################################################################
    //                                              Protected methods

    /**
     *  Put a static or dynamic concept into the caldron.
     * @param cid concept id as long
     */
    protected void _throwInCaldron_(long cid) {
        caldRon.throw_in_cpt(cid);
    }

    /**
     *  Getter.
     * The map is changed from ComDir. We do not remove or add from here.
     * @return private concept directory
     */
    public Map<Long, Concept> getCpt() {
        return cpt;
    }
    
    //##################################################################################################################
    //                                              Private data
    
    /** Private concept directory: a concept object by its Id. 
       Here we have only dynamic concepts, that were created in the bubble.
       Concurrency: the map is never updated by this object. It asks ComDir to do the work centrally. 
       It is made concurrent to prevent reading while being updated. */
    private Map<Long, Concept> cpt = new ConcurrentHashMap();
    
    /** private concept name directory: a map of cid's by the concept names. A concept not necessarily has a name. Names here 
        can be only dynamic. All static names are in the ComDir.CPN */
    private Map<String, Long> cpn = new ConcurrentHashMap();
    
    /** List of concepts which serve as a contexts to the caldron. */
    private List<Long> conText = new ArrayList(1);
    
    /** Concept id's, that should be taken into consideration on reasoning. */
    private Caldron caldRon = new Caldron();

    //##################################################################################################################
    //                                              Private methods, data
}
