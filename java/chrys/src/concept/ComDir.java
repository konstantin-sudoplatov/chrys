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
 * Common directory. This is a directory to common concept map, and attention bubbles map and other shared single pieces of data.
 * @author su
 */
public class ComDir {
    
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

    /**
     * Add an entry to the ATB map.
     * @param ab the bubble object to add.
     */
    public static synchronized void put_atb(AttentionBubble ab) {
        long bId;
        do {
            Random rnd = new Random();
            bId = rnd.nextLong();
            if(bId < 0) bId = -bId;
        } while(ATB.containsKey(bId));
        ab.setBid(bId);
        
        ATB.put(bId, ab);
    }
    
    /**
     *  Check to see if the ATB map contains a key.
     * @param bid the key to check.
     * @return
     */
    public static boolean contains_key_in_atb(long bid) {
        return ATB.containsKey(bid);
    }
    
    /**
     *  Get attention bubble object by Id.
     * @param bid bubble Id
     * @return
     */
    public static AttentionBubble get_atb(long bid) {
        return ATB.get(bid);
    }

    //##################################################################################################################
    //                                              Private data

    /** Common concept directory: a concept object by concept Id. Can be updated only by the learner attention flow. */
    private static final Map<Long, Concept> CPT = new ConcurrentHashMap();
    
    /** Attention bubbles: an attention bubble by the bubble Id. */
    private static final Map<Long, AttentionBubble> ATB = new ConcurrentHashMap();
}
