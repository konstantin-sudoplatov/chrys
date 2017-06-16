package concept;

import attention.AttnBubble;
import java.util.Random;



/**
 * Base class for all concepts.
 * @author su
 */
abstract public class Concept {
    //##################################################################################################################
    //                                              Public types        
    
    //##################################################################################################################
    //                                              Public data

    //##################################################################################################################
    //                                              Constructors

    /**
     *                                          Constructor.
     * Only for static concepts.
     * @param cid concept Id.
     */
    public Concept(long cid) {
        this.cid = cid;
    }

    /**
     *                                          Constructor.
     * Only for dynamic concepts.
     */
    public Concept() {}
    
    //##################################################################################################################
    //                                              Public methods

    /**
     *  Shows type of the concept - static/dynamic.
     * Static are hard-coded concepts, dynamic are generated at runtime.
     * @return true - static, false -dynamic
     */
    public boolean is_static() {
        return false;
    }

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
