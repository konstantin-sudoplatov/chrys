package concepts.dyn.parts;

/**
 *
 * @author su
 */
public class ActivationImpl implements ActivationIface {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** Constructor. */
    public ActivationImpl() {
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

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data ---%%%---%%%---%%%---%%%---%%%---%%%

    /** Activation. Its normalized (squashed) value is from -1 to 1. Activation is not stored in the DB
      and if the concept is not loaded into a name space(caldron) and explicitely changed it is -1. */
    private float activatioN = -1;

}   // class
