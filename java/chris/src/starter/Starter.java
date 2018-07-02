package starter;

import auxiliary.Lot;
import chris.Glob;
import concepts.Concept;
import concepts.DynCptName;
import concepts.StatCptName;
import concepts.dyn.Action;
import concepts.dyn.Neuron;
import concepts.dyn.actions.BinaryOperation_act;
import concepts.dyn.premises.Peg_prem;
import concepts.dyn.premises.String_prem;
import concepts.dyn.neurons.BA_nrn;
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

    public static void read_write_console() {
        
        // Premises "chat_prem", "chatter_name_string_prem".
        Glob.attn_disp_loop.add_cpt(new Peg_prem(), DynCptName.chat_prem.name());

        // Primus inter pares premises "chat_media_prem" contains "it_is_console_chat_prem", "it_is_http_chat_prem" premises
        Peg_prem itIsConsoleChat = new Peg_prem();
        Glob.attn_disp_loop.add_cpt(itIsConsoleChat, DynCptName.it_is_console_chat_prem.name());
        Peg_prem itIsHttpChat = new Peg_prem();
        Glob.attn_disp_loop.add_cpt(itIsHttpChat, DynCptName.it_is_http_chat_prem.name());
        PrimusInterPares_prem chatMedia = new PrimusInterPares_prem();
        long chatMediaCid = Glob.attn_disp_loop.add_cpt(chatMedia, DynCptName.chat_media_prem.name());
        chatMedia.set_members(new Concept[]{
            itIsConsoleChat, 
            itIsHttpChat
        });
        itIsConsoleChat.add_property(chatMedia);
        itIsHttpChat.add_property(chatMedia);
        
        // Primitives "line_of_chat_string_prem" and "it_is_the_first_line_of_chat_prem".
        // as the nested cid.
        long lineOfChatCid = Glob.attn_disp_loop.add_cpt(new String_prem(null), DynCptName.line_of_chat_string_prem.name());
        Glob.attn_disp_loop.add_cpt(new Peg_prem(), DynCptName.it_is_the_first_line_of_chat_prem.name());

        // Chat log list and operation of adding the line of chat to the list
        ListStock chatLogList = new ListStock();
        long chatLogListCid = Glob.attn_disp_loop.add_cpt(chatLogList, DynCptName.chat_log_list.name());
        BinaryOperation_act loggingChatLineAct = new BinaryOperation_act(StatCptName.CloneConceptAndAappendToList_stat.ordinal());
        loggingChatLineAct.set_first_operand(chatLogListCid);
        loggingChatLineAct.set_second_operand(lineOfChatCid);
        long loggingChatLineActCid = Glob.attn_disp_loop.add_cpt(loggingChatLineAct);
        
        // Action of requesting the next line.
        Action requestNextLineAct = new Action(StatCptName.RequestNextLineFromChatter_stat.ordinal());
        long requestNextLineActCid = Glob.attn_disp_loop.add_cpt(requestNextLineAct);
        
        //              Neurons, that deal with these premises:
        // The one that waits for the line from chatter.
        Neuron waitLineNrn = new BA_nrn();
        long waitLineNrnCid = Glob.attn_disp_loop.add_cpt(waitLineNrn, DynCptName.wait_for_the_line_from_chatter_nrn.name());
        Neuron requestLineNrn = new BA_nrn();
        long requestLineNrnCid = Glob.attn_disp_loop.add_cpt(requestLineNrn, DynCptName.request_next_line_nrn.name());

        waitLineNrn.set_lots(new Lot[] {
            new Lot(1, lineOfChatCid)
        });
        waitLineNrn.add_effects(0, loggingChatLineActCid, requestLineNrnCid);   // log the line, promote
        waitLineNrn.add_effects(Float.NEGATIVE_INFINITY, null, null);           // stop and wait

        requestLineNrn.set_lots(new Lot[] {
            new Lot(1, chatMediaCid)
        });
        requestLineNrn.add_effects(0, requestNextLineActCid, waitLineNrnCid);      // no actions, just promote
    }
    
    public static void symbols() {
//        Group symbolsGroup = new Group();
//        long symbolsGroupCid = Glob.attn_disp_loop.add_cpt(symbolsGroup, DynCptName.word_separators_group.name());
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
