package attention;

import chris.BaseMessage;
import concepts.dyn.ifaces.TransientIface;

/**
 * Message to a caldron that requests activation of a peg and doing a reasoning cycle.
 * @author su
 */
public class Msg_NotifyBranch extends BaseMessage {

    public long peg_cid;
    TransientIface telegram;
    
    /** 
     * Constructor.
     * @param pegCid cid of the peg that is to be activated in the destination branch.
     * @param telegram a concept to be copied by the follow() method on the receiving side.
     */ 
    public Msg_NotifyBranch(long pegCid, TransientIface telegram) { 
        peg_cid = pegCid;
        this.telegram = telegram;
    } 
}
