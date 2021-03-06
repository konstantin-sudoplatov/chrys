package starter;

import chris.Glob;
import concepts.Concept;
import concepts.DCN;
import concepts.SCN;
import concepts.dyn.Action;
import concepts.dyn.Neuron;
import concepts.dyn.actions.BinaryOperation_actn;
import concepts.dyn.actions.UnaryOperation_actn;
import concepts.dyn.neurons.And_nrn;
import concepts.dyn.neurons.Unconditional_nrn;
import concepts.dyn.premises.ActivPeg_prem;
import concepts.dyn.premises.Peg_prem;
import concepts.dyn.primitives.TransientString_prim;

/**
 * When there is no DB or it is empty, we have to start with something...
 * @author su
 */
final public class Starter {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    public void common_concepts() {
                // Common concepts
        // We execute this action from effects of a neuron if we want the caldron to wait on that neuron.
        newCpt(new Action(SCN.CaldronStopAndWait_stat), DCN.caldron_stop_and_wait_actn);
        // Chat media types - console or http. The pertinent type will be activated in the constructor of the attention circle.
        newCpt(new Peg_prem(), DCN.it_is_console_chat_prem);
        newCpt(new Peg_prem(), DCN.it_is_http_chat_prem);
        // line-from-chatter, used in more than one branch
        newCpt(new TransientString_prim(), DCN.line_from_chatter_strprim);

                // Chat log branch
        newCpt(new Unconditional_nrn(), DCN.chat_log_main_seed_uncnrn);
    }
    
    public void chat_branch() {
                // Forward creation
        Unconditional_nrn consoleSeedNrn = newCpt(new Unconditional_nrn(), DCN.console_main_seed_uncnrn);
        Peg_prem chatRequestsNextLinePeg = newCpt(new Peg_prem(), DCN.chat_requests_next_line_peg);
                
                // Create
        // Main branch seed
        Unconditional_nrn seedNrn = newCpt(new Unconditional_nrn(), DCN.chat_main_seed_uncnrn);
            UnaryOperation_actn anactivateNextLineComePegAct = newCpt(new UnaryOperation_actn() 
                    , DCN.anactivate_console_notifies_chat_next_line_come_peg_unop);
        // next line valve neuron
        And_nrn waitNextLineValveNrn = newCpt(new And_nrn(), DCN.wait_next_chat_line_valve_andnrn);
            // premises
            ActivPeg_prem consoleCaldronIsUpAPeg = newCpt(new ActivPeg_prem(), DCN.console_caldron_is_up_activprem);
            Peg_prem nextLineComePeg = newCpt(new Peg_prem(), DCN.console_notifies_chat_next_line_come_peg);
            // actions
            BinaryOperation_actn requestNextLineAct = newCpt(new BinaryOperation_actn(), DCN.chat_requests_next_line_binop);

                // Set up
            // seed
        // actions
        anactivateNextLineComePegAct.set_static_action(SCN.Anactivate_stat);
            anactivateNextLineComePegAct.set_operand(nextLineComePeg);
            // next line valve neuron
        // actions
        consoleCaldronIsUpAPeg.set_static_action(SCN.CaldronIsUp_stat);
            consoleCaldronIsUpAPeg.set_operands(getCpt(DCN.console_main_seed_uncnrn) 
                    , getCpt(DCN.console_caldron_is_up_activprem));
        requestNextLineAct.set_static_action(SCN.NotifyBranch_stat);
            requestNextLineAct.set_first_operand(consoleSeedNrn);
            requestNextLineAct.set_second_operand(chatRequestsNextLinePeg);

                // Mate
            // seed
        // actions
        seedNrn.append_action(anactivateNextLineComePegAct);
        // branches
        seedNrn.append_branch(waitNextLineValveNrn);    // pass control for the wait for next line neuron
        seedNrn.append_branch((Neuron)getCpt(DCN.console_main_seed_uncnrn));    // set up the console as a subsidiary branch
            // wait for the next line valve
        // premises
        waitNextLineValveNrn.add_premise(consoleCaldronIsUpAPeg);
        waitNextLineValveNrn.add_premise(nextLineComePeg);
        // effects
        waitNextLineValveNrn.add_effects(0, 
            new long[] {
                anactivateNextLineComePegAct.get_cid(),
                requestNextLineAct.get_cid()
            }, 
            waitNextLineValveNrn
        );
        waitNextLineValveNrn.add_effects(Float.NEGATIVE_INFINITY, (Action)getCpt(DCN.caldron_stop_and_wait_actn));
    }
    
    public void console_branch() {
        
                // Create
        // Main branch seeds
        Unconditional_nrn seedNrn = newCpt(new Unconditional_nrn(), DCN.console_main_seed_uncnrn);
            UnaryOperation_actn anactivateNextLineComePegAct = newCpt(new UnaryOperation_actn()
                    , DCN.anactivate_loop_notifies_console_branch_next_line_come_peg_unop);
        // wait-the-next-console-line valve
        And_nrn waitNextLineValveNrn = newCpt(new And_nrn(), DCN.wait_next_console_line_valve_andnrn);
            // premises
            Peg_prem nextLineComePeg = newCpt(new Peg_prem(), DCN.loop_notifies_console_branch_next_line_come_peg);
            // actions
            UnaryOperation_actn anactivateChatRequestNextLinePeg = newCpt(new UnaryOperation_actn()
                    , DCN.anactivate_chat_requests_next_line_come_peg);
            BinaryOperation_actn consoleNotifiesChatNextLineComeBinop = newCpt(new BinaryOperation_actn()
                    , DCN.console_notifies_chat_next_line_come_binop);
        // request-next-console-line valve
        And_nrn requestNextLineValveNrn = newCpt(new And_nrn(), DCN.request_next_line_from_chatter_valve_andnrn);
            // premises
            Peg_prem chatRequestsNextLinePeg = newCpt(new Peg_prem(), DCN.chat_requests_next_line_peg);
            // actions
            Action requestNextLineFromChatterAct = newCpt(new Action(), DCN.console_requests_next_line_from_chatter_actn);

                // Set up
            // seed
        // actions
        anactivateNextLineComePegAct.set_static_action(SCN.Anactivate_stat);
            anactivateNextLineComePegAct.set_operand(nextLineComePeg);
            // wait-next-console-line valve
        // actions
        anactivateChatRequestNextLinePeg.set_static_action(SCN.Anactivate_stat);
            anactivateChatRequestNextLinePeg.set_operand(chatRequestsNextLinePeg);
        consoleNotifiesChatNextLineComeBinop.set_static_action(SCN.NotifyBranch_stat);
            consoleNotifiesChatNextLineComeBinop.set_first_operand(getCpt(DCN.chat_main_seed_uncnrn));
            consoleNotifiesChatNextLineComeBinop.set_second_operand(getCpt(DCN.console_notifies_chat_next_line_come_peg));
            consoleNotifiesChatNextLineComeBinop.set_extra(getCpt(DCN.line_from_chatter_strprim));
            // console-requests-next-line-from-chatter action
        // actions
        requestNextLineFromChatterAct.set_static_action(SCN.SendReadFromConsoleMessage);
            
                // Mate
            // seed
        // actions
        seedNrn.append_action(anactivateNextLineComePegAct);
        // branches
        seedNrn.append_branch(waitNextLineValveNrn);
            // wait-next-console-line valve
        // premises
        waitNextLineValveNrn.add_premise(nextLineComePeg);
        // effects 
        waitNextLineValveNrn.add_effects(0, 
            new long[] {
                anactivateChatRequestNextLinePeg.get_cid(), 
                consoleNotifiesChatNextLineComeBinop.get_cid()
            },
            requestNextLineValveNrn
        );
        waitNextLineValveNrn.add_effects(Float.NEGATIVE_INFINITY, (Action)getCpt(DCN.caldron_stop_and_wait_actn));
            // request-next-console-line valve
        // premises
        requestNextLineValveNrn.add_premise(chatRequestsNextLinePeg);
        // effects
        requestNextLineValveNrn.add_effects(0, 
            new long[] {
                anactivateNextLineComePegAct.get_cid(), 
                requestNextLineFromChatterAct.get_cid()
            }, 
            waitNextLineValveNrn
        );
        requestNextLineValveNrn.add_effects(Float.NEGATIVE_INFINITY, (Action)getCpt(DCN.caldron_stop_and_wait_actn));
    }
    
//    public void chat_rootway() {
//
//        // Primus inter pares premises "chat_media_prem" contains "it_is_console_chat_prem", "it_is_http_chat_prem" premises
//        Peg_prim itIsConsoleChat = new Peg_prim();
//        long itIsConsoleChatCid = Glob.attn_disp_loop.add_cpt(itIsConsoleChat, DCN.it_is_console_chat_prem.name());
//        Peg_prim itIsHttpChat = new Peg_prim();
//        long itIsHttpChatCid = Glob.attn_disp_loop.add_cpt(itIsHttpChat, DCN.it_is_http_chat_prem.name());
//        PrimusInterPares_prem chatMedia = new PrimusInterPares_prem();
//        long chatMediaCid = Glob.attn_disp_loop.add_cpt(chatMedia, DCN.chat_media_prem.name());
//        chatMedia.set_members(new Concept[]{
//            itIsConsoleChat, 
//            itIsHttpChat
//        });
//        
//        // "line_from_chatter_strprem" as waiting condition for the getting the line neuron.
//        long lineOfChatCid = Glob.attn_disp_loop.add_cpt(new String_prem(null), DCN.line_from_chatter_strprem.name());
//
//        // And_prem "valve_for_requesting_next_line_prem" and its members "chatter_line_logged_subvalve_prem" and "chatter_line_parsed_subvalve_prem"
//        And_prem valveForRequestingNextLine = new And_prem();
//        Glob.attn_disp_loop.add_cpt(valveForRequestingNextLine, DCN.valve_for_requesting_next_line_prem.name());
//        Peg_prem subvalveLineLogged = new Peg_prem();
//        Glob.attn_disp_loop.add_cpt(subvalveLineLogged, DCN.chatter_line_logged_subvalve_prem.name());
//        valveForRequestingNextLine.add_member(subvalveLineLogged);
//        
//        // Action of requesting the next line.
//        Action requestNextLineAct = new Action(SCN.RequestNextLineFromChatter_stat.ordinal());
//        long requestNextLineActCid = Glob.attn_disp_loop.add_cpt(requestNextLineAct);
//        
//        //              Neurons, that deal with these premises:
//        // The one that waits for the line from chatter.
//        Neuron waitLineNrn = new Neuron(ActivationIface.ActivationType.AND, ActivationIface.NormalizationType.BIN);
//        long waitLineNrnCid = Glob.attn_disp_loop.add_cpt(waitLineNrn, DCN.valve_for_getting_next_line_nrn.name());
//        Neuron requestLineNrn = new Neuron(ActivationIface.ActivationType.AND, ActivationIface.NormalizationType.BIN);
//        long requestLineNrnCid = Glob.attn_disp_loop.add_cpt(requestLineNrn, DCN.valve_for_requesting_next_line_nrn.name());
//
//        waitLineNrn.set_lots(new Lot[] {
//            new Lot(1, lineOfChatCid)
//        });
//        waitLineNrn.add_effects(0, null, requestLineNrnCid);   // promote
//        waitLineNrn.add_effects(Float.NEGATIVE_INFINITY, 
//                Glob.attn_disp_loop.get_cpt(DCN.caldron_stop_and_wait_actn.name()).get_cid(), null);           // stop and wait
//
//        requestLineNrn.set_lots(new Lot[] {
//            new Lot(1, chatMediaCid)
//        });
//        requestLineNrn.add_effects(0, requestNextLineActCid, waitLineNrnCid);      // no actions, just promote
//        
//        // Seeds.
//            // Console chat seeds.
//        ActionPack_actn consoleChatSeedPack = new ActionPack_actn();
//        long consoleChatSeedPackCid = Glob.attn_disp_loop.add_cpt(consoleChatSeedPack, DCN.console_chat_rootway_seed_apk.name());
//        consoleChatSeedPack.add_act(new Act(SCN.Anactivate_stat.ordinal(), lineOfChatCid));
//        consoleChatSeedPack.add_act(new Act(SCN.SetPrimusInterPares_stat.ordinal(), chatMediaCid, itIsConsoleChatCid));
//        Neuron consoleChatSeedNrn = new Neuron(ActivationIface.ActivationType.WEIGHED_SUM, ActivationIface.NormalizationType.BIN);
//        Glob.attn_disp_loop.add_cpt(consoleChatSeedNrn, DCN.console_chat_rootway_seed_nrn.name());
//        consoleChatSeedNrn.add_effects(Float.NEGATIVE_INFINITY, consoleChatSeedPackCid, waitLineNrnCid);
//            // Http chat seeds.
//        ActionPack_actn httpChatSeedPack = new ActionPack_actn();
//        long httpChatSeedPackCid = Glob.attn_disp_loop.add_cpt(httpChatSeedPack, DCN.http_chat_rootway_seed_apk.name());
//        httpChatSeedPack.add_act(new Act(SCN.Anactivate_stat.ordinal(), lineOfChatCid));
//        httpChatSeedPack.add_act(new Act(SCN.SetPrimusInterPares_stat.ordinal(), chatMediaCid, itIsHttpChatCid));
//        Neuron httpChatSeedNrn = new Neuron(ActivationIface.ActivationType.WEIGHED_SUM, ActivationIface.NormalizationType.BIN);
//        Glob.attn_disp_loop.add_cpt(httpChatSeedNrn, DCN.http_chat_rootway_seed_nrn.name());
//        httpChatSeedNrn.add_effects(Float.NEGATIVE_INFINITY, httpChatSeedPackCid, waitLineNrnCid);
//    }
//    
//    public void chat_log_way() {
//
//        // Chat log list and operation of adding the line of chat to the list
//        ListStock chatLogList = new ListStock();
//        long chatLogListCid = Glob.attn_disp_loop.add_cpt(chatLogList, DCN.chat_log_lst.name());
//        BinaryOperation_actn loggingChatLineAct = new BinaryOperation_actn(SCN.CloneConceptAndAappendToList_stat.ordinal());
//        loggingChatLineAct.set_first_operand(chatLogListCid);
//        loggingChatLineAct.set_second_operand(Glob.named.name_cid.get(DCN.line_from_chatter_strprem.name()));
//        long loggingChatLineActCid = Glob.attn_disp_loop.add_cpt(loggingChatLineAct);
////        Group symbolsGroup = new Group();
////        long symbolsGroupCid = Glob.attn_disp_loop.add_cpt(symbolsGroup, DCN.word_separators_group.name());
//    }
//    
//    public void chat_seeds() {
//        
//        // Seeds for root way and branch ways of the chat attention circle, console variant
//        ActionPack_actn chatConsoleMainSeedApk = new ActionPack_actn();
//        long chatConsoleMainSeedApkCid = Glob.attn_disp_loop.add_cpt(chatConsoleMainSeedApk, DCN.console_chat_main_seed_apk.name());
//        long chatConsoleRootwayNrnCid = Glob.named.name_cid.get(DCN.console_chat_rootway_seed_nrn.name());
//        Neuron chatConsoleMainSeedNrn = new Neuron(ActivationIface.ActivationType.WEIGHED_SUM, ActivationIface.NormalizationType.BIN);
//        Glob.attn_disp_loop.add_cpt(chatConsoleMainSeedNrn, DCN.console_chat_main_seed_nrn.name());
//        chatConsoleMainSeedNrn.add_effects(Float.NEGATIVE_INFINITY, chatConsoleMainSeedApkCid, new long[] {
//                chatConsoleRootwayNrnCid,
//        });
//        
//        // Seeds for root way and branch ways of the chat attention circle, http variant
//        ActionPack_actn chatHttpMainSeedApk = new ActionPack_actn();
//        long chatHttpMainSeedApkCid = Glob.attn_disp_loop.add_cpt(chatHttpMainSeedApk, DCN.http_chat_main_seed_apk.name());
//        long chatHttpRootwayNrnCid = Glob.named.name_cid.get(DCN.http_chat_rootway_seed_nrn.name());
//        Neuron chatHttpMainSeedNrn = new Neuron(ActivationIface.ActivationType.WEIGHED_SUM, ActivationIface.NormalizationType.BIN);
//        Glob.attn_disp_loop.add_cpt(chatHttpMainSeedNrn, DCN.http_chat_main_seed_nrn.name());
//        chatHttpMainSeedNrn.add_effects(Float.NEGATIVE_INFINITY, chatHttpMainSeedApkCid, new long[] {
//                chatHttpRootwayNrnCid,
//        });
//        
//    }
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%

    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    /**
     * Add new concept to the global name space. If cid is created already, the existing 
     * concept will be returned.
     * @param cpt
     * @param name
     * @return created concept
     */
    private <T extends Concept> T newCpt(T cpt, DCN name) {
        assert cpt.get_cid() == 0;  // newly created
        
        Long cid = Glob.named.name_cid.get(name.name());
        if      //does the concept exist already?
                (cid != null)
        {//yes: return it
            assert Glob.attn_disp_loop.cpt_exists(cid);
            return (T)Glob.attn_disp_loop.load_cpt(cid);
        }
        else {//no: add new concept 
            Glob.attn_disp_loop.add_cpt(cpt, name.name());
            return cpt;
        }
    }

    /**
     * Add new concept to the global name space.
     * @param cpt
     * @return created concept
     */
    private <T extends Concept> T newCpt(T cpt) {
        assert cpt.get_cid() == 0;  // newly created

        Glob.attn_disp_loop.add_cpt(cpt);
        return cpt;
    }

    /**
     * Get a concept from the global name space.
     * @param cid
     * @return 
     */
    private Concept getCpt(long cid) {
        return Glob.attn_disp_loop.load_cpt(cid);
    }

    /**
     * Get a concept from the global name space.
     * @param name as it presented in the DCN enum.
     * @return 
     */
    private Concept getCpt(DCN name) {
        return Glob.attn_disp_loop.load_cpt(name.name());
    }

    /**
     * Get a concept from the global name space.
     * @param name as it presented in the SCN enum.
     * @return 
     */
    private Concept getCpt(SCN name) {
        return Glob.attn_disp_loop.load_cpt(name.name());
    }
}
