package concepts;

/**
 * Activation structure.
 * @author su
 */
public class Activation {
    public static enum ActivationType {
        presence,       // +1: 100% present, -1: 100% absent, 0: superposition of 50% present and 50% absent.
    }
    
    /** Type, one of the ActivationType enum. */
    public ActivationType type;
    
    /** Value from 1 to -1.  */
    public float activation;
}
