package concepts.dyn.actions;

import attention.ConceptNameSpace;
import concepts.Concept;
import concepts.SCN;
import concepts.StaticAction;
import concepts.dyn.Action;

/**
 * An operation on a concept.
 * @author su
 */
public final class UnaryOperation_actn extends Action {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--


    /** 
     * Constructor.
     */ 
    public UnaryOperation_actn() {} 

    /** 
     * Constructor.
     * @param statAction
     */ 
    public UnaryOperation_actn(SCN statAction) { super(statAction); } 

    /** 
     * Constructor.
     * @param statAction
     * @param operand
     */ 
    public UnaryOperation_actn(SCN statAction, Concept operand) {
        this(statAction); 
        operandCid = operand.get_cid();
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
        assert nameSpace.cpt_exists(operandCid);
        ((StaticAction)nameSpace.load_cpt(_statActionCid_)).go(nameSpace, new long[] {operandCid}, null);
    }

    /**
     * Setter.
     * @param cpt 
     */
    public void set_operand(Concept cpt) {
        assert operandCid == 0;
        operandCid = cpt.get_cid();
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
        return new long[] {operandCid};
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

    private long operandCid;
    private Concept extrA = null;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
