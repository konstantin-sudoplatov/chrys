package concepts.dyn;

/**
 * Getters and setters for the array of actions.
 * @author su
 */
public interface ActionIface {

    /**
     * Getter.
     * @param index
     * @return action cid with a given index in the array.
     */
    public long get_action(int index);

    /**
     * Getter.
     * @return array of action cids
     */
    public long[] get_actions();

    /**
     * Add a concept to the action array.
     * @param cid
     * @return 
     */
    public long add_action(long cid);

    /**
     * Setter.
     * @param actionArray array of action cids.
     */
    public void set_actions(long[] actionArray);
}
