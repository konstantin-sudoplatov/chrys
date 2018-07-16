package concepts.dyn.ifaces;

import concepts.Concept;

/**
 * This interface marks concepts, changes to which do not need to be reflected in the database. They matters only at the processing
 * time.
 * @author su
 */
public interface TransientIface {

    /**
     * Copy transient data from given concept.
     * Must be overriden in the descendants, that have such data.
     * @param src source concept. Class of the source must be the same or to be a related class without the transient interface,
     * so that copying had sense.
     */
    public void follow(Concept src);
    
}
