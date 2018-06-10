package starter;

import chris.Glob;
import concepts.DynCptNameEnum;
import concepts.StatCptEnum;
import concepts.dyn.Action;
import concepts.dyn.Neuron;
import concepts.dyn.Seed;
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
        long chatCid = Glob.attn_disp_loop.add_cpt(cidArr, DynCptNameEnum.chat.name());
        
        // Primitive "line_of_chat" as CiddedArray of property "it_is_the_first_line_of_chat" and JustString text of line
        // as the nested cid.
        cid = Glob.attn_disp_loop.add_cpt(new JustString(""));
        cidArr = new CiddedArray(cid);
        cid = Glob.attn_disp_loop.add_cpt(new CiddedNothing(StatCptEnum.Mrk_ElementaryPremise.ordinal()), DynCptNameEnum.it_is_the_first_line_of_chat.name());
        cidArr.append_array(cid);
        long lineCid = Glob.attn_disp_loop.add_cpt(cidArr, DynCptNameEnum.line_of_chat.name());
        
        // Action of requesting the next line.
        Action requestNextLineAction = new Action(DynCptNameEnum.request_next_console_line.ordinal());
        long requestNextLineCid = Glob.attn_disp_loop.add_cpt(requestNextLineAction);
        
        //              Neurons, that deal with these premises:
        // The one that finishes processing of the line.
        Neuron nrn = new Neuron();
        Glob.attn_disp_loop.add_cpt(nrn);       // without name
        nrn.set_premises(new Neuron.Premise[] {
            new Neuron.Premise(1, chatCid), 
            new Neuron.Premise(1, lineCid)
        });
        nrn.set_effects(new long[] {nrn.get_cid()});    // set up itself as a successor (the cid is assigned already)
        nrn.set_actions(new long[] {requestNextLineCid});
        
        // Console chat skirmisher. It combines all the above premises and determines possible effects. It will make the first assertion
        // in the reasoning tree.
        Seed seed = new Seed();
        seed.set_properties(new long[] {chatCid, lineCid});
        seed.set_effects(new long[] {nrn.get_cid()});
        Glob.attn_disp_loop.add_cpt(seed, DynCptNameEnum.console_chat_seed.name());
        

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
