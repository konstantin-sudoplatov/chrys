package concept;

import attention.AttnBubble;
import concept.dyn.PrimitiveStringCpt;
import concept.en.SCid;
import concept.en.SNm;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *                                              Common directory. 
 * <p>This is a wrapper for the common concept map. The common concept map is readable by any bubble flow, but can be written
 * by only one flow in order to exclude the race. That flow is called Teacher because it is supposed to take into
 * consideration reasoning of other bubble flows and their private concepts and translate them to the common concepts to be
 * stored in the concept DB.
 * <p>Bubbles never put or remove concepts from their private directories themselves. They ask this class to do it. That is
 * done to guarantee the uniqueness of cids.
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
     * Create a unique cid, if needed (for dynamic concepts) and put a concept to a target directory.
     * 
     * @param cpt the concept to add
     * @param bubble the bubble, that contains the target directory or null if it is the cpt.
     */
    @SuppressWarnings("UnnecessaryLabelOnContinueStatement")
    public static synchronized void add_cpt(Concept cpt, AttnBubble bubble) {
        // determine cid
        long cid;
        if      
                (cpt.is_static())
            cid = cpt.getCid();
        else {   // generate a unique cid. it is unique to cpt and all privDir's.
            GENERATE_CID:            
            while(true) {
                Random rnd = new Random();
                cid = rnd.nextLong();
                if(cid < 0) cid = -cid;
                cid += Short.MAX_VALUE - Short.MIN_VALUE + 1;
                if      // is in cpt?
                        (ComDir.cpt.containsKey(cid))
                    continue GENERATE_CID;   // generate once more
                for(AttnBubble b: atb) {
                    if      // is in PrivDir?
                            (b.getCpt().containsKey(cid))
                        continue GENERATE_CID;
                }
                break;
            }
        }
        
        // put to target dir
        if
                (bubble == null)
            ComDir.cpt.put(cid, cpt);      // put in cpt
        else
            bubble.getCpt().put(cid, cpt);
    }

    /**
     * Ditto.
     * @param cpt  the concept to add
     */
    public static synchronized void add_cpt(Concept cpt) {
        add_cpt(cpt, null);
    }
        
    /**
     *  Check to see if the cpt map contains a key.
     * @param cid the key to check.
     * @return
     */
    public static synchronized boolean contains_cpt(long cid) {
        return cpt.containsKey(cid);
    }

    /**
     * Add an entry to the atb.
     * @param ab the bubble object to add.
     */
    public static void add_atb(AttnBubble ab) {
        atb.add(ab);
    }
    
    /**
     *  Get common concept object by Id.
     * @param cid concept Id
     * @return
     */
    public static synchronized Concept get_cpt(long cid) {
        return cpt.get(cid);
    }

    /**
     *  Getter.
     * The list is synchronized, so updating would not get in each other's way. But, do not forget to synchronize on the
     * list when iterating:
     *      synchronized(getATB()) {
     *          for (Object o : list) {}
     *      }
     * @return list of bubbles
     */
    public static List<AttnBubble> getAtb() {
        return atb;
    }

    /**
     *  Create static concept objects and put them into the cpt map, and may be some predefined dynamic concepts, if they
        are not still in the DB.
     */
    public static synchronized void load_initial_concepts() {
        
        // Load cpt from DB
        
        // Load CPN from DB
        
        // Create static name dynamic concepts. This is a vocabulary for concept names.
        for(SNm stName: SNm.values()) {
            String cptName = stName.name();
            if      // not created yet?
                    (!cpn.containsKey(cptName))
            {
                Concept c = new PrimitiveStringCpt(SCid.MrkStatName.ordinal(), cptName);
                add_cpt(c);
                cpn.put(cptName, c.getCid());
            }
        }
        
        // Create static concepts.
        for(SCid cidEnum: SCid.values()) {
            String cptName = cidEnum.name();
            @SuppressWarnings("UnusedAssignment")
            Class cl = null;
            try {
                cl = Class.forName(SCid.STATIC_CONCEPTS_PACKET_NAME + "." + cptName);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ComDir.class.getName()).log(Level.SEVERE, "Error getting class " + cptName, ex);
                System.exit(1);
            }
            @SuppressWarnings("UnusedAssignment")
            Constructor cons = null;
            try {
                cons = cl.getConstructor();
            } catch (NoSuchMethodException | SecurityException ex) {
                Logger.getLogger(ComDir.class.getName()).log(Level.SEVERE, "Error getting constractor for " + cptName, ex);
                System.exit(1);
            }
            @SuppressWarnings("UnusedAssignment")
            Concept sCpt = null;
            try {
                sCpt = (Concept)cons.newInstance();
                add_cpt(sCpt);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(ComDir.class.getName()).log(Level.SEVERE, "Error instantiating " + cptName, ex);
                System.exit(1);
            }
            
            // Put the concept's name into cpn
            if      // not created yet?
                    (!cpn.containsKey(cptName))
                cpn.put(cptName, sCpt.getCid());
            
        }
    }
    
    //##################################################################################################################
    //                                              Private data
    /** Common concept directory: a map of concepts by cid's. */
    private static final Map<Long, Concept> cpt = new ConcurrentHashMap();
    
    /** Common concept name directory: a map of cid's by the concept names. A concept not necessarily has a name. Names can be
     static, known at compile time or dynamic. */
    private static final Map<String, Long> cpn = new ConcurrentHashMap();
    
    /** Attention bubbles. Is updated only in this class. */
    private static final List<AttnBubble> atb = Collections.synchronizedList(new ArrayList());
}
