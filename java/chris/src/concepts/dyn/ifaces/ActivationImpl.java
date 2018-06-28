package concepts.dyn.ifaces;

/**
 * Implementation of the ActivationIface.
 * @author su
 */
public class ActivationImpl implements ActivationIface {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** 
     * Constructor.
     * @param normType
     */ 
    public ActivationImpl(ActivationIface.NormalizationType normType) { 
         this.normType = normType; 
    } 

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    @Override
    public NormalizationType get_normalization_type() {
        return normType;
    }

    @Override
    public float get_activation() {
        return activatioN;
    }

    @Override
    public void set_activation(float activation) {
        activatioN = activation;
    }

    @Override
    public float normalize_activation() {
        switch(normType) {
            case BIN:
                if (activatioN > 0)
                    activatioN = 1;
                else 
                    activatioN = -1;
                break;

            case SGN:    
                if (activatioN > 0)
                    activatioN = 1;
                else if (activatioN < 0)
                    activatioN = -1;
                else
                    activatioN = 0;
                break;
            
            case ESQUASH:
                activatioN = ((float)((1 - Math.exp(-activatioN))/(1 + Math.exp(-activatioN))));
                break;
        }

        return activatioN;
    }

    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //      Protected    Protected    Protected    Protected    Protected    Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$--- protected data $$$---$$$---$$$---$$$---$$$---$$$--

    //---$$$---$$$---$$$---$$$---$$$--- protected methods ---$$$---$$$---$$$---$$$---$$$---

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%
    
    /** Normalization type. */
    private final ActivationIface.NormalizationType normType;
    
    /** Activation value. */
    private float activatioN = -1;

    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
