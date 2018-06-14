package starter;

import auxiliary.Premise;
import chris.Glob;
import concepts.DynCptNameEnum;
import concepts.StatCptEnum;
import concepts.dyn.Action;
import concepts.dyn.Neuron;
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

        // Premises "chat_prem", "it_is_console_chat_prem" and "chatter_unknown_prem".
        long chatCid = Glob.attn_disp_loop.add_cpt(new CiddedNothing(StatCptEnum.Mrk_ElementaryPremise.ordinal()), DynCptNameEnum.chat_prem.name());
        long isConsChatCid = Glob.attn_disp_loop.add_cpt(new CiddedNothing(StatCptEnum.Mrk_ElementaryPremise.ordinal()), DynCptNameEnum.it_is_console_chat_prem.name());
        long chatterUnknownCid = Glob.attn_disp_loop.add_cpt(new CiddedNothing(StatCptEnum.Mrk_ElementaryPremise.ordinal()), DynCptNameEnum.chatter_unknown_prem.name());
        
        // Primitives "line_of_chat_juststring" and "it_is_the_first_line_of_chat_prem".
        // as the nested cid.
        long lineHasComeCid = Glob.attn_disp_loop.add_cpt(new JustString(null), DynCptNameEnum.next_line_of_chat_has_come_prem.name());
        long lineOfChatCid = Glob.attn_disp_loop.add_cpt(new JustString(null), DynCptNameEnum.line_of_chat_juststring.name());
        Glob.attn_disp_loop.add_cpt(new CiddedNothing(StatCptEnum.Mrk_ElementaryPremise.ordinal()), DynCptNameEnum.it_is_the_first_line_of_chat_prem.name());
        
        // Action of requesting the next line.
        Action requestNextLineAction = new Action(DynCptNameEnum.request_next_line_actn.ordinal());
        long requestNextLineCid = Glob.attn_disp_loop.add_cpt(requestNextLineAction);
        
        //              Neurons, that deal with these premises:
        // The one that waits for the console line.
        Neuron nrn = new Neuron();
        nrn.set_premises(new Premise[] {
            new Premise(1, chatCid), 
            new Premise(1, lineOfChatCid)
        });
        nrn.set_effects(new long[] {nrn.get_cid()});    // set up itself as a successor (the cid is assigned already)
        nrn.append_action_ranges(0, new long[] {requestNextLineCid});
        Glob.attn_disp_loop.add_cpt(nrn, DynCptNameEnum.wait_for_the_line_from_chatter_nrn.name());
        
//        // Console chat_prem skirmisher. It combines all the above premises and determines possible effects. It will make the first assess
//        // in the reasoning tree.
//        Seed seed = new Seed();
//        seed.set_properties(new long[] {chatCid, lineCid});
//        seed.set_effects(new long[] {nrn.get_cid()});
//        Glob.attn_disp_loop.add_cpt(seed, DynCptNameEnum.console_chat_seed.name());
        

//        Neuron nrn = new Neuron();
//        nrn.set_premise(new Neuron.Premise[] {
//                new Neuron.Premise(0, cid1),
//                new Neuron.Premise(0, cid2),
//                new Neuron.Premise(0, cid3),
//                new Neuron.Premise(0, cid4),
//        });
//        Glob.attn_disp_loop.add_cpt(nrn, DynCptNameEnum.console_chat_seed.name());
//        long cid5 = Glob.attn_disp_loop.add_cpt(new Neuron(), DynCptNameEnum.add_line_to_console_log.name());
//        long cid6 = Glob.attn_disp_loop.add_cpt(new Neuron(), DynCptNameEnum.request_next_line_actn.name());
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
