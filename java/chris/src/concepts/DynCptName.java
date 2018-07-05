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
    chat_console_main_seed_apk,             // seed for console chat attention circle, action pack
    chat_console_main_seed_nrn,             // seed for console chat attention circle, neuron
    chat_http_main_seed_apk,                // seed for http chat attention circle, action pack
    chat_http_main_seed_nrn,                // seed for http chat attention circle, neuron
    
    // Starter.read_write_console()
    chat_media_prem,                        // primus inter pares premise, contains "Mrk_ItIsConsoleChat" and "Mrk_ItIsHttpChat" static concepts
    it_is_console_chat_prem,                // chat_prem by console property to the "chat_prem" premise
    it_is_http_chat_prem,                   // chat_prem by http property to the "chat_prem" premise
    line_from_chatter_strprem,               // activation +1: the line has come and waits to be processed, -1: old line invalidated, the new one has not come yet
    valve_for_getting_next_line_nrn,        // waits on the valve And_prem for the line of chat to get processed
    request_next_line_actn,                 // sends to console the request for new line
    valve_for_requesting_next_line_prem,    // And_prem that combines all conditions for the valve neuron to pass
    chatter_line_logged_subvalve_prem,      // pass condition from logging, member of the above valve
    chatter_line_parsed_subvalve_prem,      // pass condition from parser, member of the above valve
    valve_for_requesting_next_line_nrn,     // waits on its valve until the current chat line is processed, then requests new line
    chat_console_rootway_seed_apk,          // action package, that prepares the root caldron for processing
    chat_console_rootway_seed_nrn,          // neuron, that executes the chat seed action packet
    chat_http_rootway_seed_apk,             // action package, that prepares the root caldron for processing
    chat_http_rootway_seed_nrn,             // neuron, that executes the chat seed action packet
    
    // Chat log
    chat_log_lst,                           // collection of the lines of chat
    chat_log_way_seed_apk,                  // seed for chat logging caldron, action package
    chat_log_way_seed_nrn,                  // seed for chat logging caldron, neuron
    
    //
    word_separators_group,                          // each special symbol is in this group
    chatter_name_string_prem,      
    add_line_to_console_log,
    console_log,
    line_counter,
    introduce_myself_and_ask_chatter_name
}
