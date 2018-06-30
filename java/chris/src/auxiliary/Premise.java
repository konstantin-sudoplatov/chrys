package auxiliary;

import chris.Glob;
import java.util.List;

/** 
 * Structure of premises. A pair of weight of a concept and its cid. 
 */
public class Premise {
    public float weight;    // Weight with which this cid takes part in the weighted sum.
    public long cid;
    public Premise(float weight, long cid) {
        this.weight = weight;
        this.cid = cid;
    }

    /**
     * Create list of lines, which shows the object's content. For debugging. Invoked from Glob.print().
     * @param note printed in the first line just after the object type.
     * @param debugLevel 0 - the shortest, 2 - the fullest
     * @return list of lines, describing this object.
     */
    public List<String> to_list_of_lines(String note, Integer debugLevel) {
        List<String> lst = Glob.create_list_of_lines(this, note);
        Glob.append_last_line(lst, String.format("weight = %s, cid = %s", weight, cid));

        return lst;
    }
    public List<String> to_list_of_lines() {
        return to_list_of_lines("", 2);
    }
}
