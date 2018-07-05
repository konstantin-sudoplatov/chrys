package concepts.dyn.ifaces;

import auxiliary.ActivRange;
import auxiliary.Effects;
import chris.Crash;
import chris.Glob;
import java.util.List;

/**
 * A set of ranges and corresponding them lists of actions.
 * @author su
 */
public class ActivRangeImpl implements ActivRangeIface, Cloneable {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** 
     * Constructor.
     */ 
    public ActivRangeImpl() { 
    } 

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    @Override
    public ActivRangeImpl clone() {
        ActivRangeImpl clone = null;
        try {
            clone = (ActivRangeImpl)super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new Crash("Cloning a concept failed.");
        }
        clone.rangeS = (ActivRange[])rangeS.clone();
        
        return clone;
    }

    @Override
    public Effects get_effects(float activation) {
        if      // not initialized yet?
                (rangeS == null)
            throw new Crash("Trying to get an action cid from uninitialized ActionSelector.");
        
        // find the first matching range
        for (ActivRange r : rangeS) {
            if (activation > r.range) {
                return r.effects;
            }
        }
        
        // no matching range. it is illegal.
            throw new Crash("Trying to get an action cid from nonexistent range.");
    }
    
    @Override
    public void add_effects(float lowerBoundary, long[] actions, long[] ways) {
        if
                (rangeS == null)
            rangeS = new ActivRange[0];
        
        if      // is the new lower boundary bigger than the existing?
                (rangeS.length > 0 && lowerBoundary > rangeS[rangeS.length-1].range)
            throw new Crash("New boundary " + lowerBoundary + " is bigger than the last existing " + rangeS[rangeS.length-1].range);
        if      // is the new lower boundary equal to the last but one?
                (rangeS.length > 1 && lowerBoundary == rangeS[rangeS.length-2].range)
            throw new Crash("New boundary " + lowerBoundary + " is equal to the last but one.");
        
        rangeS = (ActivRange[])Glob.append_array(rangeS, new ActivRange(lowerBoundary, actions, ways));
    }

    @Override
    public void add_effects(float lowerBoundary, long action, long way) {
        add_effects(lowerBoundary, new long[] {action}, new long[] {way});
    }

    @Override
    public void add_effects(float lowerBoundary, long[] actions, long way) {
        add_effects(lowerBoundary, actions, new long[] {way});
    }

    @Override
    public void add_effects(float lowerBoundary, long action, long[] ways) {
        add_effects(lowerBoundary, new long[] {action}, ways);
    }

    /**
     * Create list of lines, which shows the object's content. For debugging. Invoked from Glob.print().
     * @param note printed in the first line just after the object type.
     * @param debugLevel 0 - the shortest, 2 - the fullest
     * @return list of lines, describing this object.
     */
    public List<String> to_list_of_lines(String note, Integer debugLevel) {
        List<String> lst = Glob.create_list_of_lines(this, note, debugLevel);
        Glob.add_list_of_lines(lst, "rangeS", rangeS, debugLevel-1);
//        for(ActivRange rng: rangeS)
//            Glob.add_list_of_lines(lst, rng.to_list_of_lines("rangeS", debugLevel-1));

        return lst;
    }
    public List<String> to_list_of_lines() {
        return to_list_of_lines("", 2);
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
     * <p>new ActivRange[]{new ActivRange(0, new long[]{cid1, cid2}), new ActivRange(-10, new long[] {cid3})}: 
 Float.MAX_VALUE >= activation > 0: cid1; cid2, 0>= activation > -10: cid3, -10 >= activation >= Float.MIN_VALUE: nothing
     */
    private ActivRange[] rangeS;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
