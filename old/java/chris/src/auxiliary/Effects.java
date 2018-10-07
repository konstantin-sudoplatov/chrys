package auxiliary;

import chris.Crash;
import chris.Glob;
import java.util.List;

/**
 *
 * @author su
 */
public class Effects implements Cloneable {

    public long[] actions;
    public long[] branches;

    /** 
     * Constructor.
     * @param actions
     * @param ways
     */ 
    public Effects(long[] actions, long[] ways) {
        this.actions = actions;
        this.branches = ways;
    } 

    @Override
    public Effects clone() {
        Effects clone = null;
        try {
            clone = (Effects)super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new Crash("Cloning a concept failed.");
        }
        clone.actions = actions.clone();
        clone.branches = branches.clone();
        
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
        Glob.add_list_of_lines(lst, "actions", actions, debugLevel-1);
        Glob.add_list_of_lines(lst, "branches", branches, debugLevel-1);

        return lst;
    }
    public List<String> to_list_of_lines() {
        return to_list_of_lines("", 2);
    }
}
