package concepts.dyn.ifaces;

/**
 * Getters and setters for the array of properties.
 * @author su
 */
public interface PropertyIface {

    /**
     * Getter.
     * @param index
     * @return property cid with a given index in the array.
     */
    public long get_property(int index);

    /**
     * Getter.
     * @return array of property cids
     */
    public long[] get_properties();

    /**
     * Add a concept to the property array.
     * @param cid
     * @return 
     */
    public long add_property(long cid);

    /**
     * Setter.
     * @param propArray array of property cids.
     */
    public void set_properties(long[] propArray);
}
