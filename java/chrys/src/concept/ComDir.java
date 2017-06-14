/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concept;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 *                                              Common directory. 
 * This is a wrapper for the common concept map. The common concept map is readable by any bubble flow, but can be written
 * by only one flow in order to exclude the race. That flow is tentatively called Leaner because it is supposed to take into
 * consideration reasoning of other bubble flows and their private concepts and translate them to the common concepts to be
 * stored in the concept DB.
 * @author su
 */
public class ComDir {

    //##################################################################################################################
    //                                              Constructors
    
    /**
     *                                      Disable instantiation.
     * This class is supposed to contain only static members, it should not ever be instantiated.
     */
    private ComDir() {}
    
    //##################################################################################################################
    //                                              Public methods
    
    /**
     * Add an entry to the CPT map.
     * @param cpt the concept object to add
     */
    public static synchronized void put_cpt(Concept cpt) {
        long cId;
        do {
            Random rnd = new Random();
            cId = rnd.nextLong();
            if(cId < 0) cId = -cId;
        } while(CPT.containsKey(cId));
        cpt.setCid(cId);
        
        CPT.put(cId, cpt);
    }
    
    /**
     *  Check to see if the CPT map contains a key.
     * @param cid the key to check.
     * @return
     */
    public static boolean contains_key_in_cpt(long cid) {
        return CPT.containsKey(cid);
    }
    
    /**
     *  Get concept object by Id.
     * @param cid concept Id
     * @return
     */
    public static Concept get_cpt(long cid) {
        return CPT.get(cid);
    }

    //##################################################################################################################
    //                                              Private data

    /** Common concept directory: a concept object by concept Id. Can be updated only by the learner attention flow. */
    private static final Map<Long, Concept> CPT = new ConcurrentHashMap();
}
