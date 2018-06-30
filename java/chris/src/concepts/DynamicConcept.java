package concepts;

import chris.Glob;
import java.util.List;

/**
 * The dynamic concept is a common predecessor for the primitives and neurons.
 * @author su
 */
abstract public class DynamicConcept extends Concept {
    
    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Getter.
     * @return concept identifier. 
     */
    @Override
    public long get_cid() {
        return ciD;
    }

    /**
     * Setter.
     * @param ciD 
     */
    public void set_cid(long ciD) {
        this.ciD = ciD;
    }

    /**
     * Getter.
     * @return creation time. 
     */
    public int get_creation_time() {
        return creationTime;
    }

    /**
     * Getter.
     * @return last access time. 
     */
    public int get_last_access_time() {
        return lastAccessTime;
    }

    /**
     * Getter.
     * @return usage counter. 
     */
    public short get_usage_count() {
        return usageCount;
    }

    /**
     * Setter. 
     * @param creationTime
     */
    public void set_creation_time(int creationTime) {
        this.creationTime = creationTime;
    }

    /**
     * Setter. 
     * @param lastAccessTime
     */
    public void set_last_access_time(int lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    /**
     * Setter. 
     * @param usageCount
     */
    public void set_usage_count(short usageCount) {
        this.usageCount = usageCount;
    }

            // Шаблон для вставки в конкретный класс

    /**
     * Create list of lines, which shows the object's content. For debugging. Invoked from Glob.print().
     * @param note printed in the first line just after the object type.
     * @param debugLevel 0 - the shortest, 2 - the fullest
     * @return list of lines, describing this object.
     */
    @Override
    public List<String> to_list_of_lines(String note, Integer debugLevel) {
        List<String> lst = super.to_list_of_lines(note, debugLevel);
        Glob.add_line(lst, String.format("creationTime = %s,(%d)", Glob.date_time_to_string(1000*(long)creationTime), creationTime));
        Glob.append_last_line(lst, String.format("; lastAccessTime = %s,(%d)", Glob.date_time_to_string(1000*(long)lastAccessTime), lastAccessTime));
        Glob.append_last_line(lst, String.format("; usageCount = %s", usageCount));

        return lst;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%

    /** Concept identifier. */
    private long ciD;
    
    /** Time of creation in seconds since 1970. */
    private int creationTime;
    
    /** Time of the last access in seconds since 1970. */
    private int lastAccessTime;
    
    /** Count of accesses(read or write). If -1, then it is > Short.MAX_VALUE, infinity in a sense. */ 
    private short usageCount;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
