package concepts.dyn;

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
    public Neuron.Premise get_premise(int index);

    /**
     * Getter.
     * @return array of premises
     */
    public Neuron.Premise[] get_premises();

    /**
     * Add a concept to the array of premises.
     * @param premise
     * @return 
     */
    public Neuron.Premise add_premise(Neuron.Premise premise);

    /**
     * Setter.
     * @param premiseArray array of premise cids.
     */
    public void set_premises(Neuron.Premise[] premiseArray);
}
