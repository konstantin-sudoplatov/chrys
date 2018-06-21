package concepts;

/** Name of a concept. 
 * Cids are assigned to concepts randomly, so code can only rely on them being immutable trough the life of a concept. 
 * It has to use other methods to match the cid and the meaning of a concept. Most dynamic concepts are not supposed 
 * to have names. There identity should be derived from their interconnections. But, from the start code must know at least
 * some of them by sight, hence this enum.
 * @author su
 */
public enum DynCptName {
    request_stop_reasoning_actn,            // tell a caldron to stop on the current head and wait for a change in the premises
    chat_prem,                              // chatting premise
    chat_media_prem,                        // primus inter pares premise, contains "Mrk_ItIsConsoleChat" and "Mrk_ItIsHttpChat" static concepts
    it_is_console_chat_prem,                // chat_prem by console property to the "chat_prem" premise
    it_is_http_chat_prem,                    // chat_prem by http property to the "chat_prem" premise
    line_of_chat_string_prem,                    // ctivation +1: the line has come and waits to be processed, -1: old line invalidated, the new one has not come yet
    it_is_the_first_line_of_chat_prem,      // it is the first line of chat_prem
    chatter_unknown_prem,      
    wait_for_the_line_from_chatter_nrn,
    request_next_line_actn,
    add_line_to_console_log,
    console_log,
    line_counter,
    introduce_myself_and_ask_chatter_name,
    ;
}
