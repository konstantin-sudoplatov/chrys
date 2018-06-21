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
     * @param paramCids 
     *  <p>paramCid[0]: "DynCptName.chat_media_prem", media must be set
     *  <p>paramCid[1]: "DynCptName.line_of_chat_string_prem"
     * @param extra not used.
     * @return null
     */
    @Override
    public long[] go(ConceptNameSpace nameSpace, long[] paramCids, Object extra) {
        PrimusInterParesPremise chatMediaCpt = (PrimusInterParesPremise)nameSpace.get_cpt(paramCids[0]);
        String chatMedia = Glob.named.cid_name.get(chatMediaCpt.get_cid());
        if
                (chatMedia.equals(DynCptName.it_is_console_chat_prem.name()))
        {
                StringPremise lineOfChat = (StringPremise)nameSpace.get_cpt(paramCids[1]);
                lineOfChat.set_activation(-1);      // make antiactive
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
        
        return null;
    }
}
