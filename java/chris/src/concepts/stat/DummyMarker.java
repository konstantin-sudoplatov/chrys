package concepts.stat;

import attention.ConceptNameSpace;
import concepts.StaticConcept;

/**
 * Static concept: Dummy marker. It is put into the concept directory for all markers.
 * 
 * @author su
 */
public class DummyMarker extends StaticConcept {
    /** A cid with which put the dummy into the concept directory */
    public long cid;

    @Override
    public long[] go(ConceptNameSpace nameSpace, long[] paramCid, Object extra) {
        throw new UnsupportedOperationException("Never should be called.");
    }
}
