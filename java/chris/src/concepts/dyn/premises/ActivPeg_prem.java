package concepts.dyn.premises;

import attention.Caldron;
import concepts.Concept;
import concepts.StatCptName;
import concepts.StaticAction;

/**
 * Peg, for which activation is calculated dynamically using a static action. The calculation is done relatively to the current caldron.
 * @author su
 */
public class ActivPeg_prem extends Peg_prem {

    /** 
     * Constructor.
     */ 
    public ActivPeg_prem() { 
    } 

    /** 
     * Constructor.
     * @param statAction
     */ 
    public ActivPeg_prem(StatCptName statAction) { 
        statActionCid = statAction.ordinal();
    } 

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Activation is calculated dynamically using a static action. The calculation is done relatively to the current caldron,
     * gotten through Thread.currentThread().
     * @return 
     */
    @Override
    public float get_activation() {
        Caldron caldron = (Caldron)Thread.currentThread();
        
        // Call static action, passing it ourself as parameter "extra".
        // As a side effect this static action sets up activation for our peg (using set_activation() method).
        ((StaticAction)caldron.load_cpt(statActionCid)).go(caldron, parameterS, this);
        
        return _activation_;
    }
    
    /**
     * Setter.
     * @param staticAction 
     */
    public void set_static_action(Concept staticAction) {
        statActionCid = staticAction.get_cid();
    }
    
    /**
     * Setter.
     * @param firstOperand 
     */
    public void set_operands(Concept firstOperand) {
        parameterS = new long[] {firstOperand.get_cid()};
    }

    /**
     * Setter.
     * @param firstOperand 
     * @param secondOperand 
     */
    public void set_operands(Concept firstOperand, Concept secondOperand) {
        parameterS = new long[] {firstOperand.get_cid(), secondOperand.get_cid()};
    }

    /**
     * Setter. 
     * @param operands array of parameter cids
     */
    public void set_operands(long[] operands) {
        parameterS = operands;
    }
    
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%

    /** Static action to be called. */
    private long statActionCid;
    
    /** Array of parameters to be passed to the static action. */
    private long[] parameterS;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--
}
