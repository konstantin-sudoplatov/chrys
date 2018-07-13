package concepts.stat;

import attention.AttnDispatcherLoop;
import attention.Caldron;
import attention.ConceptNameSpace;
import attention.Msg_NotifyBranch;
import concepts.StaticAction;

/**
 * Notify another branch.
 * @author su
 */
public class NotifyBranch_stat extends StaticAction {

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Send to the specified branch a message, containing cid of a peg. That peg will be activated in the addressed branch 
     * and the branch will be awakened from its _defaulProc_() method.
     * @param nameSpace current caldron.
     * @param paramCids 
     *      [0] - seed of a caldron (its cid), that is going to be notified.
     *      [1] - peg (its cid), which is going to be activated if when the caldron gets the message.
     * @param extra null
     * @return null
     */
    @Override
    public long[] go(ConceptNameSpace nameSpace, long[] paramCids, Object extra) {
        AttnDispatcherLoop dispatcher = nameSpace.get_attn_circle().get_attn_dispatcher();
        Caldron destCaldron = dispatcher.get_caldron(nameSpace.load_cpt(paramCids[0]));
        destCaldron.put_in_queue(new Msg_NotifyBranch(paramCids[1]));
        
        return null;
    }
}
