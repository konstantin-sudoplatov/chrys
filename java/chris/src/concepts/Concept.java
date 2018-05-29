package concepts;

import chris.Crash;

/**
 * Base class for all concepts.
 * @author su
 */
abstract public class Concept implements Cloneable {
    @Override
    public Concept clone() {
        try {
            return (Concept)super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new Crash("Cloning a concept failed.");
        }
    }
}
