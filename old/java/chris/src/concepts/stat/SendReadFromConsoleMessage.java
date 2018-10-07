package concepts.stat;

import attention.AttnDispatcherLoop;
import attention.ConceptNameSpace;
import concepts.StaticAction;
import console.Msg_ReadFromConsole;

/**
 * Send a message to the console loop requesting new line from chatter.
 * @author su
 */
public class SendReadFromConsoleMessage extends StaticAction {

    /** 
     * Constructor.
     */ 
    public SendReadFromConsoleMessage() { 
    } 

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Send a message to the console loop requesting new line from chatter.
     * @param nameSpace current caldron.
     * @param paramCids null
     * @param extra null
     * @return null
     */
    @Override
    public long[] go(ConceptNameSpace nameSpace, long[] paramCids, Object extra) {
        AttnDispatcherLoop disp = nameSpace.get_attn_circle().get_attn_dispatcher();
        disp.put_in_queue(new Msg_ReadFromConsole());
        
        return null;
    }
}
