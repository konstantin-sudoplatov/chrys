package concepts.dyn.actions;

import attention.ConceptNameSpace;
import concepts.Concept;
import concepts.SCN;
import concepts.StaticAction;
import concepts.dyn.Action;

/**
 * An operation on two concepts. The first operand is applied to the second one.
 * @author su
 */
public final class BinaryOperation_actn extends Action {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** 
     * Constructor.
     */ 
    public BinaryOperation_actn() {} 

    /** 
     * Constructor.
     * @param statAction
     */ 
    public BinaryOperation_actn(SCN statAction) { super(statAction); } 

    /** 
     * Constructor.
     * @param statAction
     * @param firstOperand
     * @param secondOperand
     */ 
    public BinaryOperation_actn(SCN statAction, Concept firstOperand, Concept secondOperand) {
        this(statAction); 
        firstOperandCid = firstOperand.get_cid();
        secondOperandCid = secondOperand.get_cid();
    } 

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Invoke the function of the static concept functor.
     * @param nameSpace
     */
    @Override
    public void go(ConceptNameSpace nameSpace) {
        assert nameSpace.cpt_exists(_statActionCid_);
        assert nameSpace.cpt_exists(firstOperandCid);
        assert nameSpace.cpt_exists(secondOperandCid);
        
        ((StaticAction)nameSpace.load_cpt(_statActionCid_)).go(nameSpace, new long[] {firstOperandCid, secondOperandCid}, null);
    }

    /**
     * Setter.
     * @param cpt 
     */
    public void set_first_operand(Concept cpt) {
        assert firstOperandCid == 0;
        firstOperandCid = cpt.get_cid();
    }

    /**
     * Setter.
     * @param cpt 
     */
    public void set_second_operand(Concept cpt) {
        assert secondOperandCid == 0;
        secondOperandCid = cpt.get_cid();
    }

    /**
     * Setter.
     * @param cpt 
     */
    public void set_extra(Concept cpt) {
        assert extrA == null;
        extrA = cpt;
    }
    
    /**
     * Get array of parameter cids.
     * @return parameters
     */
    @Override
    public long[] get_parameters() {
        return new long[] {firstOperandCid, secondOperandCid};
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

    private long firstOperandCid;
    private long secondOperandCid;
    private Concept extrA = null;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
