package concepts;

import attention.ConceptNameSpace;
import chris.Crash;

/**
 * Base class for static concepts.
 * @author su
 */
abstract public class StaticAction extends Concept {

    /**
     * Do processing for a static concept.
     * @param nameSpace name space to get concepts from
     * @param paramCids
     * @param extra
     * @return 
     */
    public abstract long[] go(ConceptNameSpace nameSpace, long[] paramCids, Object extra);

    @Override
    public long get_cid() {
        return StatCptName.valueOf(this.getClass().getSimpleName()).ordinal();
    }
    
    /**
     * Dummy setter.
     * @param ciD 
     */
    public void set_cid(long ciD) {
        throw new Crash("You cannot set cid for a static concept.");
    }
}
