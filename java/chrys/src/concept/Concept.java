package concept;



/**
 * Base class for all concepts.
 * @author su
 */
public class Concept {
    //##################################################################################################################
    //                                              Public types        
    /** Defined in code or dynamically generated. */
    public enum Level {
        /** Hard-coded concept as opposed to dynamically generated one.*/
        STATIC,
        /** Dynamically generated as opposed to hard-coded one. */
        DYNAMIC
    }
    
    //##################################################################################################################
    //                                              Public data
    /** Type of concept: Concept.Level.STATIC for hard-coded, Concept.Level.DYNAMIC for generated at runtime */
    public final Level level;

    //##################################################################################################################
    //                                              Constructors

    /**
     *                                          Constructor.
     * Only for dynamic concepts.
     */
    public Concept() {
        this.cid = -1;      // invalid. will be changed when the concept is inserted into a directory.
        this.level = Level.DYNAMIC;
    }

    /**
     *                                          Constructor.
     * Intended to be used only for static concepts. Using for dynamic concepts is discouraged.
     * @param cid concept Id.
     * @param level type of concept: Concept.Level.STATIC for hard-coded, Concept.Level.DYNAMIC for generated at runtime.
     */
    public Concept(long cid, Level level) {
        this.cid = cid;
        this.level = level;
    }
    
    //##################################################################################################################
    //                                              Public methods

    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    //##################################################################################################################
    //                                              Protected data

    //##################################################################################################################
    //                                              Protected data
    /** Concept Id. 0-65535 for static, 65536-Long.MAX_VALUE for dynamic. */
    private long cid;

    //##################################################################################################################
    //                                              Private methods, data
}
