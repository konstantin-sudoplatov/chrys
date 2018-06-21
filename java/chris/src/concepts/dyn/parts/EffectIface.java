package concepts.dyn.parts;

/**
 * Getters and setters for the array of effects.
 * @author su
 */
public interface EffectIface {

    /**
     * Getter.
     * @param index
     * @return effect cid with a given index in the array.
     */
    public long get_effect(int index);

    /**
     * Getter.
     * @return array of effect cids
     */
    public long[] get_effects();

    /**
     * Add a concept to the effect array.
     * @param cid
     * @return 
     */
    public long add_effect(long cid);

    /**
     * Setter.
     * @param effectArray array of effect cids.
     */
    public void set_effects(long[] effectArray);
}
