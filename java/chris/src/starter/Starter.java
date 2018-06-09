package starter;

import chris.Glob;
import concepts.DynCptNameEnum;
import concepts.StatCptEnum;
import concepts.dyn.primitives.CiddedArray;
import concepts.dyn.primitives.CiddedNothing;
import concepts.dyn.primitives.JustString;

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

        // Primitive "chat" as CiddedArray of properties "it_is_console_chat" and "chatter_unknown" and marker Mrk_CompositePremise
        // as the nested cid.
        long cid = Glob.attn_disp_loop.add_cpt(new CiddedNothing(StatCptEnum.Mrk_CompositePremise.ordinal()));
        CiddedArray cidArr = new CiddedArray(cid);
        cid = Glob.attn_disp_loop.add_cpt(new CiddedNothing(StatCptEnum.Mrk_ElementaryPremise.ordinal()), DynCptNameEnum.it_is_console_chat.name());
        cidArr.append_array(cid);
        cid = Glob.attn_disp_loop.add_cpt(new CiddedNothing(StatCptEnum.Mrk_ElementaryPremise.ordinal()), DynCptNameEnum.chatter_unknown.name());
        cidArr.append_array(cid);
        long cid1 = Glob.attn_disp_loop.add_cpt(cidArr, DynCptNameEnum.chat.name());
        
        // Primitive "line_of_chat" as CiddedArray of property "it_is_the_first_line_of_chat" and JustString text of line
        // as the nested cid.
        cid = Glob.attn_disp_loop.add_cpt(new JustString(""));
        cidArr = new CiddedArray(cid);
        cid = Glob.attn_disp_loop.add_cpt(new CiddedNothing(StatCptEnum.Mrk_ElementaryPremise.ordinal()), DynCptNameEnum.it_is_the_first_line_of_chat.name());
        cidArr.append_array(cid);
        long cid2 = Glob.attn_disp_loop.add_cpt(cidArr, DynCptNameEnum.line_of_chat.name());
        
        // Console chat skirmisher. It will combine all the above premises and determine possible effects. It will do the first assertion
        // in the reasoning tree.
        

//        Neuron nrn = new Neuron();
//        nrn.set_premise(new Neuron.Premise[] {
//                new Neuron.Premise(0, cid1),
//                new Neuron.Premise(0, cid2),
//                new Neuron.Premise(0, cid3),
//                new Neuron.Premise(0, cid4),
//        });
//        Glob.attn_disp_loop.add_cpt(nrn, DynCptNameEnum.console_chat_seed.name());
//        long cid5 = Glob.attn_disp_loop.add_cpt(new Neuron(), DynCptNameEnum.add_line_to_console_log.name());
//        long cid6 = Glob.attn_disp_loop.add_cpt(new Neuron(), DynCptNameEnum.request_next_console_line.name());
//        nrn.set_effect_cid(new long[] {cid5, cid6});
//        
//        Glob.attn_disp_loop.add_cpt(new Neuron(), DynCptNameEnum.console_log.name());
//        Glob.attn_disp_loop.add_cpt(new Neuron(), DynCptNameEnum.line_of_chat.name());
//        Glob.attn_disp_loop.add_cpt(new Neuron(), DynCptNameEnum.line_counter.name());
//        Glob.attn_disp_loop.add_cpt(new Neuron(), DynCptNameEnum.introduce_myself_and_ask_chatter_name.name());
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
