package concepts.dyn.ifaces;

import auxiliary.Premise;

/**
 * Getters and setters for the array of premises.
 * @author su
 */
public interface PremiseIface {

    /**
     * Getter.
     * @param index
     * @return the premise structure with given index.
     */
    public Premise get_premise(int index);

    /**
     * Getter.
     * @return array of premises
     */
    public Premise[] get_premises();

    /**
     * Add a concept to the array of premises.
     * @param premise
     * @return 
     */
    public Premise add_premise(Premise premise);

    /**
     * Setter.
     * @param premiseArray array of premise cids.
     */
    public void set_premises(Premise[] premiseArray);

    /**
     * Getter.
     * @return the bias value. 
     */
    public float get_bias();

    /**
     * Setter.
     * @param bias 
     */
    public void set_bias(float bias);
}
