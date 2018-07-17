package concepts.dyn.actions;

import attention.ConceptNameSpace;
import chris.Crash;
import chris.Glob;
import concepts.Concept;
import concepts.SCN;
import concepts.StaticAction;
import concepts.dyn.Action;
import java.util.ArrayList;
import java.util.List;

/**
 * Call static actions few at once and without preparing their parameters as concepts.
 * @author su
 */
public class ActionPack_actn extends Action {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    /** Structure that represents one action call. */
    public static class Act {
        public long stat_action_cid;
        public long[] parm_cids;
        public Object extra;
        
        public Act(SCN statAction) {
            stat_action_cid = statAction.ordinal();
            parm_cids = null;
            extra = null;
        }
        
        public Act(SCN statAction, Concept operand) {
            stat_action_cid = statAction.ordinal();
            parm_cids = new long[] {operand.get_cid()};
            extra = null;
        }
        
        public Act(SCN statAction, Concept firstOperand, Concept secondOperand) {
            stat_action_cid = statAction.ordinal();
            parm_cids = new long[] {firstOperand.get_cid(), secondOperand.get_cid()};
            extra = null;
        }
        
        public Act(SCN statAction, long[] parameters) {
            stat_action_cid = statAction.ordinal();
            parm_cids = parameters;
            extra = null;
        }
        
        public Act(SCN statAction, long[] parameters, Object extra) {
            stat_action_cid = statAction.ordinal();
            parm_cids = parameters;
            this.extra = extra;
        }
        
        public Act(long statActionCid, long[] parmCids, Object extra) {
            stat_action_cid = statActionCid;
            parm_cids = parmCids;
            this.extra = extra;
        }

        /**
         * Create list of lines, which shows the object's content. For debugging. Invoked from Glob.print().
         * @param note printed in the first line just after the object type.
         * @param debugLevel 0 - the shortest, 2 - the fullest
         * @return list of lines, describing this object.
         */
        public List<String> to_list_of_lines(String note, Integer debugLevel) {
            List<String> lst = Glob.create_list_of_lines(this, note, debugLevel);
            if (debugLevel < 0)
                return lst;
            else if (debugLevel == 0 ) {
                Glob.add_line(lst, String.format("stat_action_cid = %s, parm_sid.size() = %s, extra = %s",
                        stat_action_cid, parm_cids.length, extra));
            }
            else if (debugLevel > 0 ) {
                Glob.add_line(lst, String.format("stat_action_cid = %s", stat_action_cid));
                Glob.add_list_of_lines(lst, "parm_sids[]", parm_cids, debugLevel-1);
                Glob.add_line(lst, String.format("extra = %s", extra));
            }

            return lst;
        }
        public List<String> to_list_of_lines() {
            return to_list_of_lines("", 20);
        }
    }
    
    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** 
     * Constructor.
     */ 
    public ActionPack_actn() { 
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
            ((StaticAction)nameSpace.load_cpt(act.stat_action_cid)).go(nameSpace, act.parm_cids, act.extra);
        }
    }

    /**
     * Add an act without parameters.
     * @param statAction static action
     */
    public void add_act(SCN statAction) {
        add_act(new Act(statAction));
    }

    /**
     * Add an unary operation.
     * @param statAction static action
     * @param operand
     */
    public void add_act(SCN statAction, Concept operand) {
        add_act(new Act(statAction, operand));
    }

    /**
     * Add a binary operation.
     * @param statAction static action
     * @param firstOperand
     * @param secondOperand
     */
    public void add_act(SCN statAction, Concept firstOperand, Concept secondOperand) {
        add_act(new Act(statAction, firstOperand, secondOperand));
    }

    /**
     * Add an action with arbitrary parameters as a cid array.
     * @param statAction static action
     * @param parameters array of cids
     */
    public void add_act(SCN statAction, long[] parameters) {
        add_act(new Act(statAction, parameters));
    }

    /**
     * Add an action with arbitrary parameters as a cid array and an extra parameter as an object. 
     * @param statAction static action
     * @param parameters
     * @param extra
     */
    public void add_act(SCN statAction, long[] parameters, Object extra) {
        add_act(new Act(statAction, parameters, extra));
    }
    
    /**
     * Add new act to the list. 
     * @param act
     */
    public void add_act(Act act) {
        actS.add(act);
    }

    /**
     * Add an action as the Action concept.
     * @param action 
     */
    public void add_act(Action action) {
        add_act(new Act(action.get_stat_action(), action.get_parameters(), action.get_extra()));
    }
    
    /**
     * Get array of parameter cids.
     * @return parameters
     */
    @Override
    public long[] get_parameters() {
        throw new Crash("Not supported for action packs.");
    }
    
    /**
     * Get the extra parameter.
     * @return extra
     */
    @Override
    public Object get_extra() {
        throw new Crash("Not supported for action packs.");
    }
    
    /**
     * Getter.
     * @return result cids array. 
     */
    @Override
    public long[] get_result_cids() {
        throw new Crash("Not supported for action packs.");
    }

    /**
     * Create list of lines, which shows the object's content. For debugging. Invoked from Glob.print().
     * @param note printed in the first line just after the object type.
     * @param debugLevel 0 - the shortest, 2 - the fullest
     * @return list of lines, describing this object.
     */
    @Override
    public List<String> to_list_of_lines(String note, Integer debugLevel) {
        List<String> lst = super.to_list_of_lines(note, debugLevel);
        if (debugLevel >= 0) {
            Glob.add_list_of_lines(lst, note, actS.toArray(), debugLevel-1);
        }
        
        return lst;
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
