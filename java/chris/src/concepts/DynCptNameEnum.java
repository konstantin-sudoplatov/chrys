package concepts;

/** Name of a concept. 
 * Cids are assigned to concepts randomly, so code can only rely on them being immutable trough the life of a concept. 
 * It has to use other methods to match the cid and the meaning of a concept. Most dynamic concepts are not supposed 
 * to have names. There identity should be derived from their interconnections. But, from the start code must know at least
 * some of them by sight, hence this enum.
 * @author su
 */
public enum DynCptNameEnum {
    chat,
    it_is_console_chat,                   // chat by console
    chatter_unknown,      
    console_log,
    line_from_concole_came,
    it_is_first_line_of_chat,             // it is the first line of chat
    line_counter,
    wait_for_next_line,
    add_line_to_console_log,
    introduce_myself_and_ask_chatter_name,
    ;
}
