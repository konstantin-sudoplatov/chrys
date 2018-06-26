package starter;

import auxiliary.Premise;
import chris.Glob;
import concepts.DynCptName;
import concepts.StatCptName;
import concepts.dyn.Action;
import concepts.dyn.Neuron;
import concepts.dyn.PrimusInterParesPremise;
import concepts.dyn.SimplePremise;
import concepts.dyn.StringPremise;

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
        
        // Action "request_stop_reasoning_actn" - making a caldron wait on a change in premises is widely used
        long requestStopReasoningCid = Glob.attn_disp_loop.add_cpt(new Action(StatCptName.RequestStopReasoning.ordinal()), 
                DynCptName.request_stop_reasoning_actn.name());
        
        // Premises "chat_prem", "chatter_unknown_prem".
        long chatCid = Glob.attn_disp_loop.add_cpt(new SimplePremise(), DynCptName.chat_prem.name());
        long chatterUnknownCid = Glob.attn_disp_loop.add_cpt(new SimplePremise(), DynCptName.chatter_unknown_prem.name());

        // Primus inter pares premises "chat_media_prem" contains "it_is_console_chat_prem", "it_is_http_chat_prem" premises
        long itIsConsoleChatCid = Glob.attn_disp_loop.add_cpt(new SimplePremise(), DynCptName.it_is_console_chat_prem.name());
        long itIsHttpChatCid = Glob.attn_disp_loop.add_cpt(new SimplePremise(), DynCptName.it_is_http_chat_prem.name());
        PrimusInterParesPremise chatMedia = new PrimusInterParesPremise();
        long chatMediaCid = Glob.attn_disp_loop.add_cpt(chatMedia, DynCptName.chat_media_prem.name());
        chatMedia.set_group(new long[] {
            itIsConsoleChatCid, 
            itIsHttpChatCid
        });
        
        // Primitives "line_of_chat_string_prem" and "it_is_the_first_line_of_chat_prem".
        // as the nested cid.
        long lineOfChatCid = Glob.attn_disp_loop.add_cpt(new StringPremise(null), DynCptName.line_of_chat_string_prem.name());
        Glob.attn_disp_loop.add_cpt(new SimplePremise(), DynCptName.it_is_the_first_line_of_chat_prem.name());
        
        // Action of requesting the next line.
        Action requestNextLineAction = new Action(StatCptName.RequestNextLineFromChatter.ordinal());
        long requestNextLineCid = Glob.attn_disp_loop.add_cpt(requestNextLineAction);
        
        //              Neurons, that deal with these premises:
        // The one that waits for the line from chatter.
        Neuron waitLineNrn = new Neuron();
        waitLineNrn.set_premises(new Premise[] {
            new Premise(1, lineOfChatCid)
        });
        waitLineNrn.add_action_range(0, null);      // no actions, just promote
        waitLineNrn.add_action_range(Float.NEGATIVE_INFINITY, new long[] {requestStopReasoningCid});
        long waitLineNrnCid = Glob.attn_disp_loop.add_cpt(waitLineNrn, DynCptName.wait_for_the_line_from_chatter_nrn.name());
        // The one, that requests the next line
        Neuron requestLineNrn = new Neuron();
        requestLineNrn.set_premises(new Premise[] {
            new Premise(1, chatMediaCid)
        });
        requestLineNrn.add_action_range(0, new long[] {requestNextLineCid});      // no actions, just promote
        long requestLineNrnCid = Glob.attn_disp_loop.add_cpt(requestLineNrn, DynCptName.request_next_line_nrn.name());
        waitLineNrn.add_effect(requestLineNrnCid);
        requestLineNrn.add_effect(waitLineNrnCid);
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
