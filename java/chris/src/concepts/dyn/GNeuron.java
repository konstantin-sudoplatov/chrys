package concepts.dyn;

import concepts.dyn.ifaces.GlobalConcept;

/**
 * Global neuron.
 * @author su
 */
public class GNeuron extends Neuron implements GlobalConcept {

    /** 
     * Constructor.
     * @param activType activation type
     * @param normType normalization type
     */ 
    public GNeuron(ActivationType activType, NormalizationType normType) { super(activType, normType); } 
}
