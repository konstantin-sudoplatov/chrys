package concepts.dyn.ifaces;

import concepts.Concept;

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
     * @param cpt
     * @return added cid
     */
    public long add_effect(Concept cpt);

    /**
     * Setter.
     * @param concepts array of effect cids.
     */
    public void set_effects(Concept[] concepts);
}
