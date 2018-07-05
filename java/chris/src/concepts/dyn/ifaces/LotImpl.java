package concepts.dyn.ifaces;

import auxiliary.Lot;
import chris.Crash;
import chris.Glob;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author su
 */
public class LotImpl implements LotIface, Cloneable {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** 
     * Constructor.
     */ 
    public LotImpl() { 
    } 

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    @Override
    public LotImpl clone() {
        LotImpl clone = null;
        try {
            clone = (LotImpl)super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new Crash("Cloning a concept failed.");
        }
        if (lotS != null) clone.lotS = lotS.clone();
        
        return clone;
    }

    @Override
    public int size() {
        if (lotS == null)
            return 0;
        else
            return lotS.length;
    }
    
    @Override
    public Lot get_lot(int index) {
        return lotS[index];
    }

    @Override
    public Lot[] get_lots() {
        if (lotS == null) lotS = new Lot[0];
        return Arrays.copyOf(lotS, lotS.length);
    }

    @Override
    public Lot add_lot(Lot lot) {
        lotS = (Lot[])Glob.append_array(lotS, lot);
        return lot;
    }

    @Override
    public void set_lots(Lot[] lotArray) {
        lotS = lotArray;
    }

    @Override
    public float get_bias() {
        return biaS;
    }

    @Override
    public void set_bias(float bias) {
        biaS = bias;
    }

    /**
     * Create list of lines, which shows the object's content. For debugging. Invoked from Glob.print().
     * @param note printed in the first line just after the object type.
     * @param debugLevel 0 - the shortest, 2 - the fullest
     * @return list of lines, describing this object.
     */
    public List<String> to_list_of_lines(String note, Integer debugLevel) {
        List<String> lst = Glob.create_list_of_lines(this, note, debugLevel);
        Glob.append_last_line(lst, String.format("biaS = %s", biaS));
        
        if (lotS == null)
            Glob.add_line(lst, String.format("lotS = null"));
        else
            for(int i = 0; i < lotS.length; i++)
                Glob.add_list_of_lines(lst, lotS[i].to_list_of_lines(String.format("lot[%s]", i), debugLevel));
        
        return lst;
    }
    public List<String> to_list_of_lines() {
        return to_list_of_lines("", 2);
    }

    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //      Protected    Protected    Protected    Protected    Protected    Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$--- protected data $$$---$$$---$$$---$$$---$$$---$$$--

    //---$$$---$$$---$$$---$$$---$$$--- protected methods ---$$$---$$$---$$$---$$$---$$$---

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%
    
    /** Array of cids and weights of premises. The cids are not forbidden to be duplicated in the properties. */
    private Lot[] lotS;
    
    /** The free term of the linear expression. */
    private float biaS;

    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
