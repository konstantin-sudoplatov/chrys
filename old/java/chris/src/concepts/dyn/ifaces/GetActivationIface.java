package concepts.dyn.ifaces;

/**
 * Getters and setters for the activation variable.
 * @author su
 */
public interface GetActivationIface {
//    
//    public enum ActivationType {
//        SET,            // do not calculate activation, just set it.
//        AND,            // active (+1) only if all premises are active (>0), else antiactive (-1)
//        OR,             // active (+1) only if at least one of premises is active (>0), else antiactive (-1)
//        WEIGHED_SUM     // weighed and normalized
//    }
    
    public enum NormalizationType {
        NONE,           // no normalization, the value of activation can be any real number
        BIN,            // active +1, antiactive -1
        SGN,            // active +1, antiactive -1, indefinite 0
        ESQUASH         // exponential squashification (1 - Math.exp(-activation))/(1 + Math.exp(-activation)
    }
    
    /**
     * Get normalization type of the concept.
     * @return normalization type
     */
    public NormalizationType get_normalization_type();
    
    /**
     * Getter.
     * @return
     */
    public float get_activation();
//
//    /**
//     * Calculate weighed sum, normalize it according the neuron's type.
//     * @return 
//     */
//    public float calculate_activation();
//    
//    /** 
//     * Normalize activation according to normalization type. 
//     * @return normalized activation
//     */
//    public float normalize_activation();
}
