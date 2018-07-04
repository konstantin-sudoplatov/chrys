package concepts.stat;

import attention.AttnCircle;
import attention.AttnDispatcherLoop;
import attention.ConceptNameSpace;
import chris.Crash;
import chris.Glob;
import concepts.DynCptName;
import concepts.StaticAction;
import concepts.dyn.premises.PrimusInterPares_prem;
import concepts.dyn.premises.String_prem;
import console.Msg_ReadFromConsole;

/**
 * Requiring the caldron to send request for the next line of chat.
 * @author su
 */
public class RequestNextLineFromChatter_stat extends StaticAction {

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Make caldron to send request for the next line from chatter. On making request
     * the line_from_chatter_strprem string premise is antiactivated to make it invalid until getting the next line.
     * @param nameSpace caldron, in which thread this function would be invoked.
     * <p>implicit in parameter "DynCptName.chat_media_prem", media must be set or it'll be crash.
     * <p>implicit out parameter "DynCptName.line_from_chatter_strprem", its activation set to -1 to
     *    indicate that string is not available yet.
     * @param paramCids null
     * @param extra null
     * @return null
     */
    @Override
    public long[] go(ConceptNameSpace nameSpace, long[] paramCids, Object extra) {
        PrimusInterPares_prem chatMediaCpt = (PrimusInterPares_prem)nameSpace.get_cpt(DynCptName.chat_media_prem.name());
        if      // is not set to a valid cid?
                (chatMediaCpt.get_activation() != 1)
            throw new Crash("Concept chat_media_prem must be set");
        String chatMedia = Glob.named.cid_name.get(chatMediaCpt.get_primus());
        if
                (chatMedia.equals(DynCptName.it_is_console_chat_prem.name()))
        {
                String_prem lineOfChat = (String_prem)nameSpace.get_cpt(DynCptName.line_from_chatter_strprem.name());
                lineOfChat.set_activation(-1);      // make it antiactive
                AttnCircle attnCircle = nameSpace.get_attn_circle();
                AttnDispatcherLoop attnDisp = attnCircle.get_attn_dispatcher();
                attnDisp.put_in_queue(new Msg_ReadFromConsole());
        }
        else if
               (chatMedia.equals(DynCptName.it_is_http_chat_prem.name()))
        {
            throw new Crash("Not realized yet.");
        }
        else
            throw new Crash("Unknown chat media " + chatMedia);
    
        return null;
    }
    
}
