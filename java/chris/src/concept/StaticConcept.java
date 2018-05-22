package concept;

/**
 * Base class for static concepts.
 * @author su
 */
abstract public class StaticConcept extends Concept {
    //##################################################################################################################
    //                                              Public data

    //##################################################################################################################
    //                                              Constructors

    /**
     *                      Constructor.
     * @param cid
     */
    public StaticConcept(long cid) 
    {   super(cid);
    }
    
    //##################################################################################################################
    //                                              Public methods

    /**
     *  Shows type of the concept - static/dynamic
     * @return true - static, false -dynamic
     */
    @Override
    public final boolean is_static() {
        return false;
    }
    
    //##################################################################################################################
    //                                              Private methods, data
}
