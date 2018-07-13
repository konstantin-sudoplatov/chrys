package attention;

import chris.BaseMessage;

/**
 * Message to a caldron that requests activation of a peg and doing a reasoning cycle.
 * @author su
 */
public class Msg_NotifyBranch extends BaseMessage {

    public long peg_cid;
    
    /** 
     * Constructor.
     * @param pegCid cid of the peg that is to be activated in the destination branch.
     */ 
    public Msg_NotifyBranch(long pegCid) { 
        peg_cid = pegCid;
    } 
}
