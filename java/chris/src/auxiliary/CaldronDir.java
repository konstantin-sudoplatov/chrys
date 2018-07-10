package auxiliary;

import attention.Caldron;
import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper for the cid-caldron map. Where "cid" is the cid of the seed neuron of 
 * the reasoning branch as an identifier of the caldron. We would need synchronization, 
 * because the map can be concurrently accessed from different caldrons, hence this wrapper.
 * @author su
 */
public class CaldronDir {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** Constructor. */
    public CaldronDir() {
    }

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                            Public
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Get caldir_size.
     * @return 
     */
    synchronized public int caldir_size() {
        return caldronMap.size();
    }
    
    /**
     * Find if a cid in the map.
     * @param cid
     * @return 
     */
    synchronized public boolean caldir_contains_key(Long cid) {
        return caldronMap.containsKey(cid);
    }
    
    /**
     * Get caldron.
     * @param cid
     * @return 
     */
    synchronized public Caldron get_caldron(Long cid) {
        return caldronMap.get(cid);
    }
    
    /**
     * Add an entry to the map.
     * @param cid
     * @param caldron
     * @return previous caldron or null.
     */
    synchronized public Caldron put_caldron(Long cid, Caldron caldron) {
        return caldronMap.put(cid, caldron);
    }
    
    /**
     * Remove entry.
     * @param cid
     * @return removed caldron.
     */
    synchronized public Caldron remove_caldron(Long cid) {
        return caldronMap.remove(cid);
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data ---%%%---%%%---%%%---%%%---%%%---%%%

    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--
    
    private Map<Long, Caldron> caldronMap = new HashMap();
    
    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
   
}   // class
