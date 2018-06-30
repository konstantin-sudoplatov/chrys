package concepts.dyn.ifaces;

import chris.Crash;
import chris.Glob;
import concepts.Concept;
import java.util.List;

/**
 *
 * @author su
 */
public class EffectImpl implements Cloneable, EffectIface {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** 
     * Constructor.
     */ 
    public EffectImpl() { 
    } 

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^
    @Override
    public EffectImpl clone() {
        try {
            return (EffectImpl)super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new Crash("Cloning a concept failed.");
        }
    }
    
    @Override
    public int effect_size() {
        if (effectS == null) 
            return 0;
        else
            return effectS.length;
    }

    @Override
    public long get_effect(int index) {
        return effectS[index];
    }

    @Override
    public long[] get_effects() {
        return effectS;
    }

    @Override
    public long add_effect(Concept cpt) {
        effectS = Glob.append_array(effectS, cpt.get_cid());
        return cpt.get_cid();
    }

    @Override
    public void set_effects(Concept[] concepts) {
        effectS = new long[concepts.length];
        for(int i=0; i<concepts.length; i++)
            effectS[i] = concepts[i].get_cid();
    }

    /**
     * Create list of lines, which shows the object's content. For debugging. Invoked from Glob.print().
     * @param note printed in the first line just after the object type.
     * @param debugLevel 0 - the shortest, 2 - the fullest
     * @return list of lines, describing this object.
     */
    public List<String> to_list_of_lines(String note, Integer debugLevel) {
        List<String> lst = Glob.create_list_of_lines(this, note);
        Glob.add_line(lst, String.format("effect_size() = %s", effect_size()));
        if (debugLevel > 0) {
            Glob.add_line(lst, String.format("cids: "));
            for(Long cid: effectS)
                Glob.append_last_line(lst, String.format("%s; ", cid));
        }

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
    
    /** Array of possible effects. */
    private long[] effectS;

    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
