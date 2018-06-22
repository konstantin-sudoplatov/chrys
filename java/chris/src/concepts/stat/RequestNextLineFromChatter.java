package concepts.stat;

import attention.AttnCircle;
import attention.AttnDispatcherLoop;
import attention.ConceptNameSpace;
import chris.Crash;
import chris.Glob;
import concepts.DynCptName;
import concepts.StaticConcept;
import concepts.dyn.PrimusInterParesPremise;
import concepts.dyn.StringPremise;
import console.Msg_ReadFromConsole;

/**
 * Requiring the caldron to send request for the next line of chat.
 * @author su
 */
public class RequestNextLineFromChatter extends StaticConcept {

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Make caldron to send request for getting the next line from chatter. On making request
     * the line_of_chat_string_prem antiactivated to make it invalid until getting the next line.
     * @param nameSpace caldron, in which thread this function would be invoked.
     * <p>implicit in parameter "DynCptName.chat_media_prem", media must be set or it'll be crash.
     * <p>implicit out parameter "DynCptName.line_of_chat_string_prem", its activation set to -1 to
     * indicate that string is not available yet.
     */
    @Override
    public void go(ConceptNameSpace nameSpace) {
        PrimusInterParesPremise chatMediaCpt = (PrimusInterParesPremise)nameSpace.get_cpt(DynCptName.chat_media_prem.name());
        if      // is not set to a valid cid?
                (chatMediaCpt.get_activation() != 1)
            throw new Crash("Concept chat_media_prem must be set");
        String chatMedia = Glob.named.cid_name.get(chatMediaCpt.get_primus());
        if
                (chatMedia.equals(DynCptName.it_is_console_chat_prem.name()))
        {
                StringPremise lineOfChat = (StringPremise)nameSpace.get_cpt(DynCptName.line_of_chat_string_prem.name());
                lineOfChat.set_activation(-1);      // make it antiactive
                AttnCircle attnCircle = nameSpace.get_attn_circle();
                AttnDispatcherLoop attnDisp = attnCircle.get_attn_dispatcher();
                attnCircle.put_in_queue(new Msg_ReadFromConsole());
        }
        else if
               (chatMedia.equals(DynCptName.it_is_http_chat_prem.name()))
        {
            throw new Crash("Not realized yet.");
        }
        else
            throw new Crash("Unknown chat media " + chatMedia);
    }
}
