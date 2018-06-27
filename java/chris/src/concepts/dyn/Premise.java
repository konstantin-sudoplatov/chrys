package concepts.dyn;

import chris.Glob;
import concepts.DynamicConcept;
import concepts.dyn.ifaces.ActivationIface;
import concepts.dyn.ifaces.PropertyIface;

/**
 * Ancestor of all premises.
 * @author su
 */
abstract public class Premise extends DynamicConcept implements ActivationIface, PropertyIface {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** Constructor. */
    public Premise() {
    }

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                            Public
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^
    
    /**
     * Getter.
     * @return
     */
    @Override
    public float get_activation() {
        return activatioN;
    }

    /**
     * Setter.
     * @param activation
     */
    @Override
    public void set_activation(float activation) {
        this.activatioN = activation;
    }

    @Override
    public long get_property(int index) {
        return propertieS[index];
    }

    @Override
    public long[] get_properties() {
        return propertieS;
    }

    @Override
    public long add_property(long cid) {
        propertieS = Glob.append_array(propertieS, cid);
        return cid;
    }

    @Override
    public void set_properties(long[] propArray) {
        propertieS = propArray;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data ---%%%---%%%---%%%---%%%---%%%---%%%

    /** Activation. Its normalized (squashed) value is from -1 to 1. Activation is not stored in the DB
      and if the concept is not loaded into a name space(caldron) and explicitely changed it is -1. */
    private float activatioN = -1;
    
    /** Array of cids, defining pertinent data . The cids are not forbidden to be duplicated in the premises. */
    private long[] propertieS;

    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
   
}   // class
