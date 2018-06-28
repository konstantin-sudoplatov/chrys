package concepts.dyn.ifaces;

import concepts.Concept;

/**
 * Getters and setters for the array of properties.
 * @author su
 */
public interface PropertyIface {
    
    /**
     * Get property size.
     * @return 
     */
    public int property_size();
//
//    /**
//     * Getter.
//     * @param index
//     * @return property cid with a given index in the array.
//     */
//    public long get_property(int index);

    /**
     * Getter.
     * @return array of property cids
     */
    public long[] get_properties();

    /**
     * Add a concept to the property array.
     * @param cpt
     * @return 
     */
    public long add_property(Concept cpt);

    /**
     * Setter.
     * @param concepts array of properties.
     */
    public void set_properties(Concept[] concepts);
}
