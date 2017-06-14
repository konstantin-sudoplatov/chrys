package attention;

import concept.Concept;
import java.util.HashMap;
import java.util.Map;

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
//        return _privDir_;
//    }
    
    //##################################################################################################################
    //                                              Protected data

    /** Private concept directory: a concept object by its Id. */
    protected Map<Long, Concept> _privDir_ = new HashMap();

    //##################################################################################################################
    //                                              Protected methods

    //##################################################################################################################
    //                                              Private data

    //##################################################################################################################
    //                                              Private methods, data
}
