package concepts.dyn.actions;

import attention.ConceptNameSpace;
import concepts.StaticAction;
import concepts.dyn.Action;
import java.util.ArrayList;
import java.util.List;

/**
 * Call static actions few at once and without preparing their parameters as concepts.
 * @author su
 */
public class ActionPacket extends Action {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    /** Structure that represents one action call. */
    public static class Act {
        public long stat_action_cid;
        public long[] parm_cids;
        public Object extra;
        
        public Act(long statActionCid) {
            stat_action_cid = statActionCid;
            parm_cids = null;
            extra = null;
        }
        
        public Act(long statActionCid, long operand) {
            stat_action_cid = statActionCid;
            parm_cids = new long[] {operand};
            extra = null;
        }
        
        public Act(long statActionCid, long firstOperand, long secondOperand) {
            stat_action_cid = statActionCid;
            parm_cids = new long[] {firstOperand, secondOperand};
            extra = null;
        }
        
        public Act(long statActionCid, long[] parameters) {
            stat_action_cid = statActionCid;
            parm_cids = parameters;
            extra = null;
        }
        
        public Act(long statActionCid, long[] parameters, Object extra) {
            stat_action_cid = statActionCid;
            parm_cids = parameters;
            this.extra = extra;
        }
    }
    
    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** 
     * Constructor.
     */ 
    public ActionPacket() { 
    } 

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Do actions on the Act list (ordered).
     * @param nameSpace 
     */
    @Override
    public void go(ConceptNameSpace nameSpace) {
        for(Act act: actS) {
            ((StaticAction)nameSpace.get_cpt(act.stat_action_cid)).go(nameSpace, act.parm_cids, act.extra);
        }
    }
    
    /**
     * Add new act to the list. 
     * @param act
     * @return true/false.
     */
    public boolean add_act(Act act) {
        return actS.add(act);
    }
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //      Protected    Protected    Protected    Protected    Protected    Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$--- protected data $$$---$$$---$$$---$$$---$$$---$$$--

    //---$$$---$$$---$$$---$$$---$$$--- protected methods ---$$$---$$$---$$$---$$$---$$$---

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%

    private List<Act> actS = new ArrayList();
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
