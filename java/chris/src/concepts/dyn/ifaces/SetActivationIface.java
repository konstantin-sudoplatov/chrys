package concepts.dyn.ifaces;

/**
 * Binary activation (NormalizationType.BIN).
 * @author su
 */
public interface SetActivationIface extends GetActivationIface {
 
    /**
     * Set activation to +1.
     */
    public void activate();
    
    /**
     * Set activation to -1.
     */
    public void antiactivate();

    /**
     * Set arbitrary activation.
     * @param activation
     */
    public void set_activation(float activation);
}
