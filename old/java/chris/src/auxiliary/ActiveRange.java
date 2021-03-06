package auxiliary;

import chris.Crash;
import chris.Glob;
import java.util.List;

/**
 * List of ranges and corresponding them lists of actions.
 * @author su
 */
public class ActiveRange implements Cloneable {
    
    /** Lower exclusive boundary of the range of activation. */
    public float range;
    
    /** Effects structure. */
    public Effects effects;

    /**
     * Constructor.
     * @param range lower exclusive boundary of the range, where these actions are valid
     * @param actions array of cids of actions. Empty array or null - no actions, just continue processing.
     * @param branches array of cids of branches. Empty array or null - no branches, processing should wait. The first branch is
     * always root - current caldron in which processing takes place. If there are other branches, there will be formed new
     * caldrons for them.
     */
    public ActiveRange(float range, long[] actions, long[] branches) {
        this.range = range;
        effects = new Effects(actions, branches);
    }

    @Override
    public ActiveRange clone() {
        ActiveRange clone = null;
        try {
            clone = (ActiveRange)super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new Crash("Cloning a concept failed.");
        }
        clone.effects = (Effects)effects.clone();
        
        return clone;
    }

    /**
     * Create list of lines, which shows the object's content. For debugging. Invoked from Glob.print().
     * @param note printed in the first line just after the object type.
     * @param debugLevel 0 - the shortest, 2 - the fullest
     * @return list of lines, describing this object.
     */
    public List<String> to_list_of_lines(String note, Integer debugLevel) {
        List<String> lst = Glob.create_list_of_lines(this, note, debugLevel);
        Glob.add_line(lst, String.format("range = %s", range));
        Glob.add_list_of_lines(lst, effects.to_list_of_lines("effects", debugLevel-1));

        return lst;
    }
    public List<String> to_list_of_lines() {
        return to_list_of_lines("", 2);
    }
}
