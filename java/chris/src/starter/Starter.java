package starter;

import chris.Glob;
import concepts.DynCptNameEnum;
import concepts.StatCptEnum;
import concepts.dyn.Neuron;
import concepts.dyn.primitives.Mark;
import concepts.dyn.primitives.MarkedCidArray;

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

        // concept "initial_concepts_for_start_of_console_chat" - everything we need for initialization of the first assertion.
        long cid1 = Glob.attn_disp_loop.add_cpt(new Mark(StatCptEnum.Mrk_SinglePremise.ordinal()), DynCptNameEnum.chat.name());
        long cid2 = Glob.attn_disp_loop.add_cpt(new Mark(StatCptEnum.Mrk_SinglePremise.ordinal()), DynCptNameEnum.it_is_console_chat.name());
        long cid3 = Glob.attn_disp_loop.add_cpt(new Mark(StatCptEnum.Mrk_SinglePremise.ordinal()), DynCptNameEnum.it_is_first_line_of_chat.name());
        long cid4 = Glob.attn_disp_loop.add_cpt(new Mark(StatCptEnum.Mrk_SinglePremise.ordinal()), DynCptNameEnum.chatter_unknown.name());
        Neuron nrn = new Neuron();
        nrn.set_premise( new Neuron.Premise[] {
                new Neuron.Premise(0, cid1),
                new Neuron.Premise(0, cid2),
                new Neuron.Premise(0, cid3),
                new Neuron.Premise(0, cid4),
        });
        Glob.attn_disp_loop.add_cpt(nrn, DynCptNameEnum.initial_concepts_for_start_of_console_chat.name());
        long cid5 = Glob.attn_disp_loop.add_cpt(new Neuron(), DynCptNameEnum.add_line_to_console_log.name());
        long cid6 = Glob.attn_disp_loop.add_cpt(new Neuron(), DynCptNameEnum.request_next_console_line.name());
        nrn.set_effect_cid(new long[] {cid5, cid6});
        
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
