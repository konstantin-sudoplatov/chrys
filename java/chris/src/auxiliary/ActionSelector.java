package auxiliary;

import chris.Crash;
import chris.Glob;
import concepts.dyn.ifaces.ActionIface;

/**
 * A set of ranges and corresponding them lists of actions.
 * @author su
 */
public class ActionSelector implements ActionIface {
    
    /**
     * Range and corresponding actions.
     */
    public static class Range {
        public float range;
        public long[] actions;

        /**
         * Constructor.
         * @param range lower boundary of the range, where these actions are valid
         * @param actions array of cids of actions
         */
        public Range(float range, long[] actions) {
            this.range = range;
            this.actions = actions;
        }
        
    }

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** 
     * Constructor.
     */ 
    public ActionSelector() { 
    } 

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    @Override
    public long[] get_actions(float activation) {
        if      // not initialized yet?
                (rangeS == null)
            throw new Crash("Trying to get an action cid from uninitialized ActionSelector.");
        
        // find the first matching range
        for (Range r : rangeS) {
            if (activation > r.range) {
                return r.actions;
            }
        }
        
        // no matching range. it is illegal.
            throw new Crash("Trying to get an action cid from nonexistent range.");
    }
    
    @Override
    public void add_action_range(float lowerBoundary, long[] actions) {
        if
                (rangeS == null)
            rangeS = new Range[0];
        
        if      // is the new lower boundary bigger than the existing?
                (rangeS.length > 0 && lowerBoundary > rangeS[rangeS.length-1].range)
            throw new Crash("New boundary " + lowerBoundary + " is bigger than the last existing " + rangeS[rangeS.length-1].range);
        if      // is the new lower boundary equal to the last but one?
                (rangeS.length > 1 && lowerBoundary == rangeS[rangeS.length-2].range)
            throw new Crash("New boundary " + lowerBoundary + " is equal to the last but one.");
        
        rangeS = (Range[])Glob.append_array(rangeS, new Range(lowerBoundary, actions));
    }
    
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%

    /** 
     * Ranges are sorted in descending order, the high boundary including the low excluding. All that is lower than the
     * lowest boundary is range without actions. Examples:
     * <p>new Range[]{new Range(0, new long[]{cid1, cid2}), new Range(-10, new long[] {cid3})}: 
     * Float.MAX_VALUE >= activation > 0: cid1; cid2, 0>= activation > -10: cid3, -10 >= activation >= Float.MIN_VALUE: nothing
     */
    private Range[] rangeS;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
