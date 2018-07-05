package concepts;

import chris.Crash;
import chris.Glob;
import java.util.List;

/**
 * Base class for all concepts.
 * @author su
 */
abstract public class Concept implements Cloneable {
    @Override
    public Concept clone() {
        try {
            return (Concept)super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new Crash("Cloning a concept failed.");
        }
    }

    /**
     * Getter.
     * @return 
     */
    abstract public long get_cid();

    /**
     * Create list of lines, which shows the object's content. For debugging. Invoked from Glob.print().
     * @param note printed in the first line just after the object type.
     * @param debugLevel 0 - the shortest, 2 - the fullest
     * @return list of lines, describing this object.
     */
    public List<String> to_list_of_lines(String note, Integer debugLevel) {
        List<String> lst = Glob.create_list_of_lines(this, note, debugLevel);
        Glob.append_last_line(lst, String.format("get_cid() = %d", get_cid()));
        String cptName = Glob.named.cid_name.get(get_cid());
        if (cptName != null)
            Glob.append_last_line(lst, String.format("; concept name: %s.", cptName));
        else
            Glob.append_last_line(lst, String.format("; unnamed concept."));

        return lst;
    }
    public List<String> to_list_of_lines() {
        return to_list_of_lines("", 2);
    }
}
