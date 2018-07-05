package starter;

import auxiliary.Lot;
import chris.Glob;
import concepts.Concept;
import concepts.DynCptName;
import concepts.StatCptName;
import concepts.dyn.Action;
import concepts.dyn.Neuron;
import concepts.dyn.actions.ActionPack;
import concepts.dyn.actions.ActionPack.Act;
import concepts.dyn.actions.BinaryOperation_act;
import concepts.dyn.ifaces.ActivationIface;
import concepts.dyn.premises.And_prem;
import concepts.dyn.premises.Peg_prem;
import concepts.dyn.premises.String_prem;
import concepts.dyn.premises.PrimusInterPares_prem;
import concepts.dyn.stocks.ListStock;

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

    public static void common_concepts() {
        // We execute this action from effects of a neuron if we want the caldron to wait on that neuron.
        Glob.attn_disp_loop.add_cpt(new Action(StatCptName.CaldronStopAndWait_stat.ordinal()), DynCptName.caldron_stop_and_wait_actn.name());
    }
    
    public static void chat_rootway() {

        // Primus inter pares premises "chat_media_prem" contains "it_is_console_chat_prem", "it_is_http_chat_prem" premises
        Peg_prem itIsConsoleChat = new Peg_prem();
        long itIsConsoleChatCid = Glob.attn_disp_loop.add_cpt(itIsConsoleChat, DynCptName.it_is_console_chat_prem.name());
        Peg_prem itIsHttpChat = new Peg_prem();
        long itIsHttpChatCid = Glob.attn_disp_loop.add_cpt(itIsHttpChat, DynCptName.it_is_http_chat_prem.name());
        PrimusInterPares_prem chatMedia = new PrimusInterPares_prem();
        long chatMediaCid = Glob.attn_disp_loop.add_cpt(chatMedia, DynCptName.chat_media_prem.name());
        chatMedia.set_members(new Concept[]{
            itIsConsoleChat, 
            itIsHttpChat
        });
        itIsConsoleChat.add_property(chatMedia);
        itIsHttpChat.add_property(chatMedia);
        
        // "line_from_chatter_strprem" as waiting condition for the getting the line neuron.
        long lineOfChatCid = Glob.attn_disp_loop.add_cpt(new String_prem(null), DynCptName.line_from_chatter_strprem.name());

        // And_prem "valve_for_requesting_next_line_prem" and its members "chatter_line_logged_subvalve_prem" and "chatter_line_parsed_subvalve_prem"
        And_prem valveForRequestingNextLine = new And_prem();
        Peg_prem subvalveLineLogged = new Peg_prem();
        valveForRequestingNextLine.add_member(subvalveLineLogged);
//        Peg_prem subvalveLineParsed = new Peg_prem();
//        valveForRequestingNextLine.add_member(subvalveLineParsed);
        Glob.attn_disp_loop.add_cpt(valveForRequestingNextLine, DynCptName.valve_for_requesting_next_line_prem.name());
        
        // Action of requesting the next line.
        Action requestNextLineAct = new Action(StatCptName.RequestNextLineFromChatter_stat.ordinal());
        long requestNextLineActCid = Glob.attn_disp_loop.add_cpt(requestNextLineAct);
        
        //              Neurons, that deal with these premises:
        // The one that waits for the line from chatter.
        Neuron waitLineNrn = new Neuron(ActivationIface.ActivationType.AND, ActivationIface.NormalizationType.BIN);
        long waitLineNrnCid = Glob.attn_disp_loop.add_cpt(waitLineNrn, DynCptName.valve_for_getting_next_line_nrn.name());
        Neuron requestLineNrn = new Neuron(ActivationIface.ActivationType.AND, ActivationIface.NormalizationType.BIN);
        long requestLineNrnCid = Glob.attn_disp_loop.add_cpt(requestLineNrn, DynCptName.valve_for_requesting_next_line_nrn.name());

        waitLineNrn.set_lots(new Lot[] {
            new Lot(1, lineOfChatCid)
        });
        waitLineNrn.add_effects(0, null, requestLineNrnCid);   // promote
        waitLineNrn.add_effects(Float.NEGATIVE_INFINITY, 
                Glob.attn_disp_loop.get_cpt(DynCptName.caldron_stop_and_wait_actn.name()).get_cid(), null);           // stop and wait

        requestLineNrn.set_lots(new Lot[] {
            new Lot(1, chatMediaCid)
        });
        requestLineNrn.add_effects(0, requestNextLineActCid, waitLineNrnCid);      // no actions, just promote
        
        // Seeds.
            // Console chat seeds.
        ActionPack consoleChatSeedPack = new ActionPack();
        consoleChatSeedPack.add_act(new Act(StatCptName.Antiactivate_stat.ordinal(), lineOfChatCid));
        consoleChatSeedPack.add_act(new Act(StatCptName.SetPrimusInterPares_stat.ordinal(), chatMediaCid, itIsConsoleChatCid));
        long consoleChatSeedPackCid = Glob.attn_disp_loop.add_cpt(consoleChatSeedPack, DynCptName.chat_console_rootway_seed_apk.name());
        Neuron consoleChatSeedNrn = new Neuron(ActivationIface.ActivationType.WEIGHED_SUM, ActivationIface.NormalizationType.BIN);
        consoleChatSeedNrn.add_effects(Float.NEGATIVE_INFINITY, consoleChatSeedPackCid, waitLineNrnCid);
        Glob.attn_disp_loop.add_cpt(consoleChatSeedNrn, DynCptName.chat_console_rootway_seed_nrn.name());
            // Http chat seeds.
        ActionPack httpChatSeedPack = new ActionPack();
        httpChatSeedPack.add_act(new Act(StatCptName.Antiactivate_stat.ordinal(), lineOfChatCid));
        httpChatSeedPack.add_act(new Act(StatCptName.SetPrimusInterPares_stat.ordinal(), chatMediaCid, itIsHttpChatCid));
        long httpChatSeedPackCid = Glob.attn_disp_loop.add_cpt(httpChatSeedPack, DynCptName.chat_http_rootway_seed_apk.name());
        Neuron httpChatSeedNrn = new Neuron(ActivationIface.ActivationType.WEIGHED_SUM, ActivationIface.NormalizationType.BIN);
        httpChatSeedNrn.add_effects(Float.NEGATIVE_INFINITY, httpChatSeedPackCid, waitLineNrnCid);
        Glob.attn_disp_loop.add_cpt(httpChatSeedNrn, DynCptName.chat_http_rootway_seed_nrn.name());
    }
    
    public static void chat_log_way() {

        // Chat log list and operation of adding the line of chat to the list
        ListStock chatLogList = new ListStock();
        long chatLogListCid = Glob.attn_disp_loop.add_cpt(chatLogList, DynCptName.chat_log_lst.name());
        BinaryOperation_act loggingChatLineAct = new BinaryOperation_act(StatCptName.CloneConceptAndAappendToList_stat.ordinal());
        loggingChatLineAct.set_first_operand(chatLogListCid);
        loggingChatLineAct.set_second_operand(Glob.named.name_cid.get(DynCptName.line_from_chatter_strprem.name()));
        long loggingChatLineActCid = Glob.attn_disp_loop.add_cpt(loggingChatLineAct);
//        Group symbolsGroup = new Group();
//        long symbolsGroupCid = Glob.attn_disp_loop.add_cpt(symbolsGroup, DynCptName.word_separators_group.name());
    }
    
    public static void chat_seeds() {
        
        // Seeds for root way and branch ways of the chat attention circle, console variant
        ActionPack chatConsoleMainSeedApk = new ActionPack();
        long chatConsoleMainSeedApkCid = Glob.attn_disp_loop.add_cpt(chatConsoleMainSeedApk, DynCptName.chat_console_main_seed_apk.name());
        long chatConsoleRootwayNrnCid = Glob.named.name_cid.get(DynCptName.chat_console_rootway_seed_nrn.name());
        Neuron chatConsoleMainSeedNrn = new Neuron(ActivationIface.ActivationType.WEIGHED_SUM, ActivationIface.NormalizationType.BIN);
        Glob.attn_disp_loop.add_cpt(chatConsoleMainSeedNrn, DynCptName.chat_console_main_seed_nrn.name());
        chatConsoleMainSeedNrn.add_effects(Float.NEGATIVE_INFINITY, chatConsoleMainSeedApkCid, new long[] {
                chatConsoleRootwayNrnCid,
        });
        
        // Seeds for root way and branch ways of the chat attention circle, http variant
        ActionPack chatHttpMainSeedApk = new ActionPack();
        long chatHttpMainSeedApkCid = Glob.attn_disp_loop.add_cpt(chatHttpMainSeedApk, DynCptName.chat_http_main_seed_apk.name());
        long chatHttpRootwayNrnCid = Glob.named.name_cid.get(DynCptName.chat_http_rootway_seed_nrn.name());
        Neuron chatHttpMainSeedNrn = new Neuron(ActivationIface.ActivationType.WEIGHED_SUM, ActivationIface.NormalizationType.BIN);
        Glob.attn_disp_loop.add_cpt(chatHttpMainSeedNrn, DynCptName.chat_http_main_seed_nrn.name());
        chatHttpMainSeedNrn.add_effects(Float.NEGATIVE_INFINITY, chatHttpMainSeedApkCid, new long[] {
                chatHttpRootwayNrnCid,
        });
        
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
