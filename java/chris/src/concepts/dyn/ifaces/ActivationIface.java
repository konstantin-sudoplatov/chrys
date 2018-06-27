package concepts.dyn.ifaces;

/**
 * Getters and setters for the activation variable.
 * @author su
 */
public interface ActivationIface {
    
    public enum NormalizationType {
        BIN,            // active +1, antiactive -1
        SGN,            // active +1, antiactive -1, indefinite 0
        ESQUASH         // exponential squashification (1 - Math.exp(-activation))/(1 + Math.exp(-activation)
    }
    
    /**
     * Get normalization type of the concept.
     * @return normalization type
     */
    public NormalizationType normalization_type();
    
    /**
     * Getter.
     * @return
     */
    public float get_activation();

    /**
     * Setter.
     * @param activation
     */
    public void set_activation(float activation);
}
