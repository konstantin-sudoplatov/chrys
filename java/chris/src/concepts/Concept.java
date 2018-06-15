package concepts;

import chris.Crash;

/**
 * Base class for all concepts.
 * @author su
 */
abstract public class Concept implements Cloneable {
    @Override
    public Concept clone() {
        try {
            return (Concept)super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new Crash("Cloning a concept failed.");
        }
    }

    /**
     * Dummy getter. You will be able to it use on dynamic concepts without conversion them to the dynamic concept type.
     * @return 
     */
    public long get_cid() {
        throw new UnsupportedOperationException("get_cid() is not supported for static concepts.");
    }

    /**
     * Dummy setter. You will be able to it use on dynamic concepts without conversion them to the dynamic concept type.
     * @param ciD 
     */
    public void set_cid(long ciD) {
    }
}
