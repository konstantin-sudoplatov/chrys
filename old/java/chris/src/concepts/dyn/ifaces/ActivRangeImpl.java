package concepts.dyn.ifaces;

import auxiliary.ActiveRange;
import auxiliary.Effects;
import chris.Crash;
import chris.Glob;
import concepts.dyn.Action;
import concepts.dyn.Neuron;
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
        if (rangeS != null) 
            clone.rangeS = (ActiveRange[])rangeS.clone();
        
        return clone;
    }

    @Override
    public Effects select_effects(float activation) {
        if      // not initialized yet?
                (rangeS == null)
            throw new Crash("Trying to get an action cid from uninitialized ActionSelector.");
        
        // find the first matching range
        for (ActiveRange r : rangeS) {
            if (activation > r.range) {
                return r.effects;
            }
        }
        
        // no matching range. it is illegal.
            throw new Crash(String.format("Trying to get an action cid from nonexistent range. activation = %s, concept:\n%s",
                    activation, Glob.list_to_listln(this.to_list_of_lines("", 10))));
    }
    
    @Override
    public void add_effects(float lowerBoundary, long[] actions, long[] ways) {
        if
                (rangeS == null)
            rangeS = new ActiveRange[0];
        
        if      // is the new lower boundary bigger than the existing?
                (rangeS.length > 0 && lowerBoundary > rangeS[rangeS.length-1].range)
            throw new Crash("New boundary " + lowerBoundary + " is bigger than the last existing " + rangeS[rangeS.length-1].range);
        if      // is the new lower boundary equal to the last but one?
                (rangeS.length > 1 && lowerBoundary == rangeS[rangeS.length-2].range)
            throw new Crash("New boundary " + lowerBoundary + " is equal to the last but one.");
        
        rangeS = (ActiveRange[])Glob.append_array(rangeS, new ActiveRange(lowerBoundary, actions, ways));
    }

    @Override
    public void add_effects(float lowerBoundary, Action action, Neuron branch) {
        add_effects(lowerBoundary, new long[] {action.get_cid()}, new long[] {branch.get_cid()});
    }

    @Override
    public void add_effects(float lowerBoundary, long[] actions, Neuron branch) {
        add_effects(lowerBoundary, actions, new long[] {branch.get_cid()});
    }

    @Override
    public void add_effects(float lowerBoundary, Action action, long[] branches) {
        add_effects(lowerBoundary, new long[] {action.get_cid()}, branches);
    }

    @Override
    public void add_effects(float lowerBoundary, Action action) {
        add_effects(lowerBoundary, new long[] {action.get_cid()}, (long[])null);
    }

    @Override
    public void append_action(float activation, Action action) {
        Effects effs = select_effects(activation);
        effs.actions = Glob.append_array(effs.actions, action.get_cid());
    }

    @Override
    public void append_branch(float activation, Neuron branch) {
        Effects effs = select_effects(activation);
        effs.branches = Glob.append_array(effs.branches, branch.get_cid());
    }

    /**
     * Getter.
     * @return 
     */
    public ActiveRange[] get_ranges() {
        return rangeS;
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
//        for(ActiveRange rng: rangeS)
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
     * <p>new ActiveRange[]{new ActiveRange(0, new long[]{cid1, cid2}), new ActiveRange(-10, new long[] {cid3})}: 
 Float.MAX_VALUE >= activation > 0: cid1; cid2, 0>= activation > -10: cid3, -10 >= activation >= Float.MIN_VALUE: nothing
     */
    private ActiveRange[] rangeS;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
