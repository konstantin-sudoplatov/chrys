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
    public long[] ways;

    /** 
     * Constructor.
     * @param actions
     * @param ways
     */ 
    public Effects(long[] actions, long[] ways) {
        this.actions = actions;
        this.ways = ways;
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
        clone.ways = ways.clone();
        
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
        if (actions == null)
            Glob.add_line(lst, String.format("actions = null"));
        else
            for(int i = 0; i < actions.length; i++) 
                Glob.add_line(lst, String.format("action[%s] = %s", i, actions[i]));
        if (ways == null)
            Glob.add_line(lst, String.format("ways = null"));
        else
            for(int i = 0; i < ways.length; i++) 
                Glob.add_line(lst, String.format("effect[%s] = %s", i, ways[i]));

        return lst;
    }
    public List<String> to_list_of_lines() {
        return to_list_of_lines("", 2);
    }
}
