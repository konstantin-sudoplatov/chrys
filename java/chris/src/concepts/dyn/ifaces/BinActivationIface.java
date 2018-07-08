package concepts.dyn.ifaces;

/**
 * Binary activation (NormalizationType.BIN).
 * @author su
 */
public interface BinActivationIface extends ActivationIface {
 
    /**
     * Set activation to +1.
     */
    public void activate();
    
    /**
     * Set activation to -1.
     */
    public void antiactivate();
    
}
