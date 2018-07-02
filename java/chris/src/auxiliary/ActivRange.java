package auxiliary;

import chris.Crash;
import chris.Glob;
import java.util.List;

/**
 * List of ranges and corresponding them lists of actions.
 * @author su
 */
public class ActivRange implements Cloneable {
    
    /** Lower exclusive boundary of the range of activation. */
    public float range;
    
    /** Effects structure. */
    public Effects effects;

    /**
     * Constructor.
     * @param range lower exclusive boundary of the range, where these actions are valid
     * @param actions array of cids of actions. Empty array or null - no actions, just continue processing.
     * @param ways array of cids of ways. Empty array or null - no ways, processing should wait.
     */
    public ActivRange(float range, long[] actions, long[] ways) {
        this.range = range;
        effects = new Effects(actions, ways);
    }

    @Override
    public ActivRange clone() {
        ActivRange clone = null;
        try {
            clone = (ActivRange)super.clone();
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
        List<String> lst = Glob.create_list_of_lines(this, note);
        Glob.add_line(lst, String.format("range = %s", range));
        Glob.add_list_of_lines(lst, effects.to_list_of_lines("effects", debugLevel));

        return lst;
    }
    public List<String> to_list_of_lines() {
        return to_list_of_lines("", 2);
    }
}
