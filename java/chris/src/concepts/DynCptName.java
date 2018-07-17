package concepts;

/** Name of a concept. 
 * Cids are assigned to concepts randomly, so code can only rely on them being immutable trough the life of a concept. 
 * It has to use other methods to match the cid and the meaning of a concept. Most dynamic concepts are not supposed 
 * to have names. There identity should be derived from their interconnections. But, from the start code must know at least
 * some of them by sight, hence this enum.
 * @author su
 */
public enum DynCptName {
    // Common concepts
    caldron_stop_and_wait_actn,             // request caldron to stop reasoning on the current neuron
    it_is_console_chat_prem,                // chat by console premise
    it_is_http_chat_prem,                   // chat by http premise
    line_from_chatter_strprim,              // the line from chatter
    
    // Chat branch
    chat_main_seed_uncnrn,                  // this is the root way of the chat
        anactivate_console_notifies_chat_next_line_come_pegprem_unop,
    wait_next_chat_line_valve_andnrn,            // waits for next line of chat
        // prem
        console_caldron_is_up_activprem,        // checks status of the console caldron and sets up activation of the neuron accordingly
        console_notifies_chat_next_line_come_pegprem,       // is activated by the console or http branch when the next line comes
        // actn
        chat_requests_next_line_binopactn,
    
    // Console branch
    console_main_seed_uncnrn,               // branch for reading/writing console lines (permanent branch)
        anactivate_loop_notifies_console_branch_next_line_come_pegprem_unop,
    wait_next_console_line_valve_andnrn,             // waits for the next line from console
        // prem
        loop_notifies_console_branch_next_line_come_pegprem,    // is activated by the loop _defaultProc_ method when it sets new value to line_from_chatter_strprim
        // actn
        console_notifies_chat_next_line_come_binopactn,  // console branch notifies chat that the next line has come
    request_next_console_line_valve_andnrn,             //wait until the console line is taken by all consumers, then request next line
        // prem
        chat_requests_next_line_pegprem,    // is signaled(activated) by the chat branch when it is ready to take in the next line

    // Chat log branch
    chat_log_main_seed_uncnrn,              // branch for logging console lines (permanent branch) 
}
