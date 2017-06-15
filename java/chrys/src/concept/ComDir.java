package concept;

import concept.stat.SCid;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *                                              Common directory. 
 * This is a wrapper for the common concept map. The common concept map is readable by any bubble flow, but can be written
 * by only one flow in order to exclude the race. That flow is tentatively called Learner because it is supposed to take into
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
     *                          Add an entry to the CPT map.
     * For dynamic concepts first a unique id is generated.
     * @param cpt the concept object to add
     * @return Id of the concept. As side effect the Id is assigned to the concept's cid field.
     */
    public static synchronized long put_cpt(Concept cpt) {
        long cId;
        if
                (cpt.level == Concept.Level.STATIC)
            cId = cpt.getCid();
        else {
            do {
                Random rnd = new Random();
                cId = rnd.nextLong();
                if(cId < 0) cId = -cId;
                cId += Short.MAX_VALUE - Short.MIN_VALUE + 1;
            } while(CPT.containsKey(cId));
            cpt.setCid(cId);
        }
        
        CPT.put(cId, cpt);
        
        return cId;
    }
    
    /**
     *  Check to see if the CPT map contains a key.
     * @param cid the key to check.
     * @return
     */
    public static boolean contains_cpt(long cid) {
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
     *  Create static concept objects and put them into the CPT map.
     */
    public static void generate_static_concepts() {
        for(SCid cidEnum: SCid.values()) {
            String s = cidEnum.name();
            @SuppressWarnings("UnusedAssignment")
            Class cl = null;
            try {
                cl = Class.forName(SCid.STATIC_CONCEPTS_PACKET_NAME + "." + s);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ComDir.class.getName()).log(Level.SEVERE, "Error getting class " + s, ex);
                System.exit(1);
            }
            @SuppressWarnings("UnusedAssignment")
            Constructor cons = null;
            try {
                cons = cl.getConstructor();
            } catch (NoSuchMethodException | SecurityException ex) {
                Logger.getLogger(ComDir.class.getName()).log(Level.SEVERE, "Error getting constractor for " + s, ex);
                System.exit(1);
            }
            try {
                put_cpt((Concept)cons.newInstance());
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(ComDir.class.getName()).log(Level.SEVERE, "Error instantiating " + s, ex);
                System.exit(1);
            }
        }
    }
    
    //##################################################################################################################
    //                                              Private data

    /** Common concept directory: a concept object by concept Id. Can be updated only by the learner attention flow. */
    private static final Map<Long, Concept> CPT = new ConcurrentHashMap();
}
