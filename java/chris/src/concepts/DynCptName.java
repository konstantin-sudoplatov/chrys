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
    
    // Main branch seeds
    chat_main_seed_unknrn,                  // this is the root way of the chat (perminent)
    console_main_seed_unknrn,               // branch for reading/writing console lines (perminent)
    chat_log_main_seed_unknrn,              // branch for logging console lines (perminent)
    
    // Chat branch
    it_is_console_chat_prem,                // chat by console premise
    it_is_http_chat_prem,                   // chat by http premise
    wait_next_chat_line_valve_andnrn,            // waits for next line of chat
        // prem
        console_caldron_is_up_activprem,        // checks status of the console caldron and sets up activation of the neuron accordingly
        console_notifies_chat_next_line_come_pegprem,       // is activated by the console or http branch when the next line comes
        // actn
        chat_branch_requests_next_console_line_signal_actn, // sends signal to console branch, which tells it that chat is ready to take next line
    
    // Console branch
    register_console_caldron_in_dispatcher_loop_actn,   // give itself to the dispatcher, so it would know to whome route the console messages
    wait_next_console_line_valve_andnrn,             // waits for the next line from console
        console_loop_notifies_next_line_come_pegprem,    // is activated by the loop _defaultProc_ method when it sets new value to line_from_chatter_strprim
        console_notifies_chat_next_line_come_binopactn,  // console branch notifies chat that the next line has come
    request_next_console_line_valve_andnrn,             //wait until the console line is taken by all consumers, then request next line
        chat_requests_next_line_pegprem,    // is signaled(activated) by the chat branch when it is ready to take in the next line

    // Chat log branch
        
    // Chat-console-http branches interpaly
    line_from_chatter_strprim,              // the line from chatter

//        chat_log_requests_next_console_line_pegprem,    // is signaled(activated) by the chat branch when it is ready to take next line
        console_mass_notifies_next_line_come_apk,   // compacts signal actions for all interested branches
                anactivate_chat_requests_next_console_line_pegprem_actn,
//            console_notifies_chat_log_next_line_come_apk,
//                console_notifies_chat_log_next_line_come_actn,
//                anactivate_chat_log_requests_next_console_line_pegprem_actn,
    
//    request_next_line_actn,                 // sends to console the request for new line
    

//    chat_line_reader_andnrn,                // console or http communication controller
//
//    line_from_chatter_strprem,              // activation +1: the line has come and waits to be processed, -1: no line is available

    
//    // Common concepts
//    caldron_stop_and_wait_actn,             // request caldron to stop reasoning on the current neuron
//    console_chat_main_seed_apk,             // seed for console chat attention circle, action pack
//    console_chat_main_seed_nrn,             // seed for console chat attention circle, neuron
//    http_chat_main_seed_apk,                // seed for http chat attention circle, action pack
//    http_chat_main_seed_nrn,                // seed for http chat attention circle, neuron
//    
//    // Starter.read_write_console()
//    chat_media_prem,                        // primus inter pares premise, contains "Mrk_ItIsConsoleChat" and "Mrk_ItIsHttpChat" static concepts
//    line_from_chatter_strprem,               // activation +1: the line has come and waits to be processed, -1: old line invalidated, the new one has not come yet
//    valve_for_getting_next_line_nrn,        // waits on the valve And_prem for the line of chat to get processed
//    valve_for_requesting_next_line_prem,    // And_prem that combines all conditions for the valve neuron to pass
//    chatter_line_logged_subvalve_prem,      // pass condition from logging, member of the above valve
//    chatter_line_parsed_subvalve_prem,      // pass condition from parser, member of the above valve
//    valve_for_requesting_next_line_nrn,     // waits on its valve until the current chat line is processed, then requests new line
//    console_chat_rootway_seed_apk,          // action package, that prepares the root caldron for processing
//    console_chat_rootway_seed_nrn,          // neuron, that executes the chat seed action packet
//    http_chat_rootway_seed_apk,             // action package, that prepares the root caldron for processing
//    http_chat_rootway_seed_nrn,             // neuron, that executes the chat seed action packet
//    
//    // Chat log
//    chat_log_lst,                           // collection of the lines of chat
//    chat_log_way_seed_apk,                  // seed for chat logging caldron, action package
//    chat_log_way_seed_nrn,                  // seed for chat logging caldron, neuron
//    
//    //
//    word_separators_group,                          // each special symbol is in this group
//    chatter_name_string_prem,      
//    add_line_to_console_log,
//    console_log,
//    line_counter,
//    introduce_myself_and_ask_chatter_name
}
