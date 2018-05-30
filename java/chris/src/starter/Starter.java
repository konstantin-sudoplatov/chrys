package starter;

import chris.Glob;
import concepts.DynCptNameEnum;
import concepts.StatCptEnum;
import concepts.dyn.Neuron;
import concepts.dyn.primitives.Mark;
import concepts.dyn.primitives.MarkedCidSet;

/**
 * When there is no DB or it is empty, we have to start with something...
 * @author su
 */
final public class Starter {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** 
     * Disable constructor.
    */ 
    private Starter() { 
    } 

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    public static void generate_dynamic_concepts() {

        long cid1 = Glob.attn_disp_loop.add_cpt(new Mark(StatCptEnum.Mrk_SinglePremise.ordinal()), DynCptNameEnum.chat.name());
        long cid2 = Glob.attn_disp_loop.add_cpt(new Mark(StatCptEnum.Mrk_SinglePremise.ordinal()), DynCptNameEnum.it_is_console_chat.name());
        long cid3 = Glob.attn_disp_loop.add_cpt(new Mark(StatCptEnum.Mrk_SinglePremise.ordinal()), DynCptNameEnum.it_is_first_line_of_chat.name());
        long cid4 = Glob.attn_disp_loop.add_cpt(new Mark(StatCptEnum.Mrk_SinglePremise.ordinal()), DynCptNameEnum.chatter_unknown.name());
        Glob.attn_disp_loop.add_cpt(new MarkedCidSet(StatCptEnum.Mrk_UnorderedListOfCids.ordinal(), new long[] {cid1, cid2, cid3, cid4}), 
                DynCptNameEnum.set_of_premises_for_start_of_chat.name());
        Glob.attn_disp_loop.add_cpt(new Neuron(), DynCptNameEnum.add_line_to_console_log.name());
        Glob.attn_disp_loop.add_cpt(new Neuron(), DynCptNameEnum.request_next_console_line.name());
        Glob.attn_disp_loop.add_cpt(new Neuron(), DynCptNameEnum.console_log.name());
        Glob.attn_disp_loop.add_cpt(new Neuron(), DynCptNameEnum.line_from_concole.name());
        Glob.attn_disp_loop.add_cpt(new Neuron(), DynCptNameEnum.line_counter.name());
        Glob.attn_disp_loop.add_cpt(new Neuron(), DynCptNameEnum.introduce_myself_and_ask_chatter_name.name());
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

    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--
    
    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
