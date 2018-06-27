package concepts.dyn.ifaces;

/**
 * Getters and setters for the array of actions.
 * @author su
 */
public interface ActionIface {

    /**
     * Get array of action cids, corresponding to the given activation value.
     * @param activation activation value
     * @return array of cids range of which fits the activation. null for the empty list.
     * @throws Crash() for uninitialized action selector or for nonexistent range.
     */
    public long[] get_actions(float activation);
    
    /**
     * Add new element to the ranges array. The new boundary must be less or equal to the last existing boundary. One boundary
     * cannot be repeated more than twice.
     * @param lowerBoundary this boundary delimits the range from the last existing boundary including to this value excluding.
     * @param actions list of cids of actions. null for the empty list.
     */
    public void add_action_range(float lowerBoundary, long[] actions);
}
