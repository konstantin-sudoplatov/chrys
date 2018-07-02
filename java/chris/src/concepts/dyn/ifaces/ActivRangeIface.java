package concepts.dyn.ifaces;

import auxiliary.Effects;

/**
 * Getters and setters for the array of activation ranges.
 * @author su
 */
public interface ActivRangeIface {

    /**
     * Get arrays of action and ways cids, corresponding to the given activation value.
     * @param activation activation value
     * @return effect structure. It can consists of nulls or empty arrays but cannot be null itself.
     * @throws Crash() for uninitialized action selector or for nonexistent range.
     */
    public Effects get_effects(float activation);
    
    /**
     * Add new element to the activation ranges array. The new boundary must be less or equal to the last existing boundary. One boundary
     * cannot be repeated more than twice.
     * @param lowerBoundary this boundary delimits the range from the last existing boundary including to this value excluding.
     * @param actions array of action cids
     * @param ways array of way cids
     */
    public void add_effects(float lowerBoundary, long[] actions, long[] ways);

    /** 
     * Ditto.
     * @param lowerBoundary
     * @param action single action cid
     * @param way single way cid
     */
    public void add_effects(float lowerBoundary, long action, long way);

    /** 
     * Ditto.
     * @param lowerBoundary
     * @param actions array of action cids
     * @param way single way cid
     */
    public void add_effects(float lowerBoundary, long[] actions, long way);

    /** 
     * Ditto.
     * @param lowerBoundary
     * @param action single action cid
     * @param ways array of way cids
     */
    public void add_effects(float lowerBoundary, long action, long[] ways);

}
