package concepts.dyn.ifaces;

import auxiliary.Lot;

/**
 * Getters and setters for the array of promises.
 * @author su
 */
public interface LotIface {

    /** Size of the lot list */
    public int size();
    
    /**
     * Getter.
     * @param index
     * @return the lot structure with given index.
     */
    public Lot get_lot(int index);
//
//    /**
//     * Getter.
//     * @return array of lots
//     */
//    public Lot[] get_lots();

    /**
     * Add new member to the array of lots.
     * @param lot
     * @return 
     */
    public Lot add_lot(Lot lot);

    /**
     * Setter.
     * @param lots array of lots.
     */
    public void set_lots(Lot[] lots);

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
