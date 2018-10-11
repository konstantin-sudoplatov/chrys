module cpt.cpt_actions;
import std.format;

import project_params, tools;

import chri_shared;
import cpt.cpt_abstract, cpt.cpt_stat;
import attn.attn_circle_thread;
import stat.stat_types;
import crank.crank_types: DcpDescriptor;

/**
            Base for all holy actions. The action concept is an interface, bridge between the world of cids and dynamic concepts,
    that knows nothing about the code and the static world, which is a big set of functions, that actually are the code.
    All concrete descendants will have the "_act" suffix.
*/
alias SA = SpAction;       /// SA - spirit action
class SpAction: SpiritDynamicConcept {

    /**
                Constructor
        Parameters:
            cid = predefined concept identifier
    */
    this(Cid cid) { super(cid); }

    /// Create live wrapper for the holy static concept.
    override Action live_factory() const {
        return new Action(cast(immutable)this);
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
                Call static concept function.
        Parameters:
            caldron = name space it which static concept function will be working.
    */
    void run(Caldron caldron) {
        assert((cast(SpStaticConcept)_sm_[_statActionCid]).callType == StatCallType.p0Cal,
                format!"Static concept: %s( cid:%s) in SpAction must have StatCallType none and it has %s."
                      (_nm_[_statActionCid], _statActionCid, (cast(SpStaticConcept)_sm_[_statActionCid]).callType));

        auto statCpt = (cast(SpStaticConcept)_sm_[_statActionCid]);
        (cast(void function(Caldron))statCpt.fp)(caldron);
    }

    /// Full setup
    final void load(Cid statAction) {
        checkCid!SpStaticConcept(statAction);
        _statActionCid = statAction;
    }

    /// Getter
    final @property Cid statAction() {
        return _statActionCid;
    }

    /// Setter
    final @property Cid statAction(Cid statActionCid) {
        debug checkCid!SpStaticConcept(statActionCid);
        return _statActionCid = statActionCid;
    }

    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

    // Static action.
    protected Cid _statActionCid;
}

/// Live.
class Action: DynamicConcept {

    /// Private constructor. Use SpiritConcept.live_factory() instead.
    private this(immutable SpAction spAction) { super(spAction); }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
                Call static concept function.
        Parameters:
            caldron = name space it which static concept function will be working.
    */
    void run(Caldron caldron) {
        assert((cast(SpAction)spirit).statAction != 0, format!"Cid: %s, static action must be assigned."(this.cid));

        (cast(SpAction)spirit).run(caldron);
    }
}

/// Actions, that operate on only one concept. Examples: activate/anactivate concept.
alias SA_Cid = SpUnaryAction;       /// SA - spirit action, Cid - p0Calp1Cid
final class SpUnaryAction: SpAction {

    /// Constructor
    this(Cid cid) { super(cid); }

    /// Create live wrapper for the holy static concept.
    override UnaryAction live_factory() const {
        return new UnaryAction(cast(immutable)this);
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
                Call static concept function.
        Parameters:
            caldron = name space it which static concept function will be working.
    */
    final override void run(Caldron caldron) {
        auto statAct = (scast!SpStaticConcept(_sm_[_statActionCid]));
        assert(statAct.callType == StatCallType.p0Calp1Cid,
                format!"Static concept: %s( cid:%s) in SpAction must have StatCallType p0Calp1Cid and it has %s."
                        (typeid(statAct), _statActionCid, statAct.callType));
        checkCid!DynamicConcept(caldron, _operandCid);

        (cast(void function(Caldron, Cid))statAct.fp)(caldron, _operandCid);
    }

    /// Full setup
    void load(Cid statAction, DcpDescriptor operand) {
        checkCid!SpStaticConcept(statAction);
        _statActionCid = statAction;
        checkCid!SpiritDynamicConcept(operand.cid);
        _operandCid = operand.cid;
    }

    /// Partial setup, only operand
    void load(DcpDescriptor operand) {
        checkCid!SpiritDynamicConcept(operand.cid);
        _operandCid = operand.cid;
    }

    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

    /// Cid of a concept to operate on
    protected Cid _operandCid;
}

/// Live.
final class UnaryAction: Action {

    /// Private constructor. Use live_factory() instead.
    private this(immutable SpUnaryAction spUnaryAction) { super(spUnaryAction); }

}

/// Actions, that operate on two concepts. Examples: sending a message - the first operand breed of the correspondent,
/// the second operand concept object to send.
alias SA_CidCid = SpBinaryAction;       /// SA - spirit action, CidCid - p0Calp1Cidp2Cid
final class SpBinaryAction: SpAction {

    /// Constructor
    this(Cid cid) { super(cid); }

    /// Create live wrapper for the holy static concept.
    override BinaryAction live_factory() const {
        return new BinaryAction(cast(immutable)this);
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
                Call static concept function.
        Parameters:
            caldron = name space it which static concept function will be working.
    */
    final override void run(Caldron caldron) {
        auto statAct = (scast!SpStaticConcept(_sm_[_statActionCid]));
        assert(statAct.callType == StatCallType.p0Calp1Cidp2Cid,
                format!"Static concept: %s( cid:%s) in SpAction must have StatCallType p0Calp1Cidp2Cid and it has %s."
                        (typeid(statAct), _statActionCid, statAct.callType));
        checkCid!DynamicConcept(caldron, _firstOperandCid);
        checkCid!DynamicConcept(caldron, _secondOperandCid);

        (cast(void function(Caldron, Cid, Cid))statAct.fp)(caldron, _firstOperandCid, _secondOperandCid);
    }

    /// Full setup
    void load(Cid statAction, DcpDescriptor firstOperand, DcpDescriptor secondOperand) {
        checkCid!SpStaticConcept(statAction);

        _statActionCid = statAction;
        checkCid!SpiritDynamicConcept(firstOperand.cid);
        _firstOperandCid = firstOperand.cid;
        checkCid!SpiritDynamicConcept(secondOperand.cid);
        _secondOperandCid = secondOperand.cid;
    }

    /// Partial setup, without the static action
    void load(DcpDescriptor firstOperand, DcpDescriptor secondOperand) {
        checkCid!SpiritDynamicConcept(firstOperand.cid);
        _firstOperandCid = firstOperand.cid;
        checkCid!SpiritDynamicConcept(secondOperand.cid);
        _secondOperandCid = secondOperand.cid;
    }

    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

    /// Cid of the first concept
    protected Cid _firstOperandCid;

    /// Cid of the second concept
    protected Cid _secondOperandCid;
}

/// Live.
final class BinaryAction: Action {

    /// Private constructor. Use live_factory() instead.
    private this(immutable SpBinaryAction spBinaryAction) { super(spBinaryAction); }
}

/// Action, that involves a concept and a float value
alias SA_CidFloat = SpUnaryFloatAction;     /// SA - spirit action, CidFloat - p0Calp1Cidp2Float
class SpUnaryFloatAction: SpAction {

    /// Constructor
    this(Cid cid) { super(cid); }

    /// Create live wrapper for the holy static concept.
    override UnaryFloatAction live_factory() const {
        return new UnaryFloatAction(cast(immutable)this);
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /// Run the static action.
    final override void run(Caldron caldron) {
        auto statAct = (scast!SpStaticConcept(_sm_[_statActionCid]));
        assert(statAct.callType == StatCallType.p0Calp1Cidp2Float,
                format!"Static concept: %s( cid:%s) in SpAction must have StatCallType p0Calp1Cidp2Float and it has %s."
                        (typeid(statAct), _statActionCid, statAct.callType));
        checkCid!DynamicConcept(caldron, _p1Cid);

        (cast(void function(Caldron, Cid, float))statAct.fp)(caldron, _p1Cid, _p2Float);
    }

    /**
            Set float value for a concept in the current namespace.
        Parameters:
            statActionCid = static action to perform
            p1 = concept, that takes the float value
            p2 = float value
    */
    void load(Cid statActionCid, DcpDescriptor p1, float p2) {
        checkCid!SpStaticConcept(statActionCid);
        checkCid!SpiritDynamicConcept(p1.cid);

        _statActionCid = statActionCid;
        _p1Cid = p1.cid;
        _p2Float = p2;
    }

    /// Partial setup, without the static action
    void load(DcpDescriptor p1, float p2) {
        checkCid!SpiritDynamicConcept(p1.cid);

        _p1Cid = p1.cid;
        _p2Float = p2;
    }

    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

    // Parameter 1 - a concept cid.
    protected Cid _p1Cid;

    // Parameter 2 - a float value
    protected float _p2Float;
}

/// Live.
class UnaryFloatAction: Action {

    /// Private constructor. Use live_factory() instead.
    private this(immutable SpUnaryFloatAction spUnaryFloatAction) { super(spUnaryFloatAction); }
}

/// Action, that involves two concepts and a float value
alias SA_CidCidFloat = SpBinaryFloatAction;     /// SA - spirit action, CidCidFloat stands for p0Calp1Cidp2Cidp3Float
class SpBinaryFloatAction: SpAction {

    /// Constructor
    this(Cid cid) { super(cid); }

    /// Create live wrapper for the holy static concept.
    override BinaryFloatAction live_factory() const {
        return new BinaryFloatAction(cast(immutable)this);
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /// Run the static action.
    final override void run(Caldron caldron) {
        auto statAct = (scast!SpStaticConcept(_sm_[_statActionCid]));
        assert(statAct.callType == StatCallType.p0Calp1Cidp2Cidp3Float,
                format!"Static concept: %s( cid:%s) in SpAction must have StatCallType p0Calp1Cidp2Cidp3Float and it has %s."
                        (typeid(statAct), _statActionCid, statAct.callType));
        checkCid!DynamicConcept(caldron, _p1Cid);
        checkCid!DynamicConcept(caldron, _p2Cid);

        (cast(void function(Caldron, Cid, Cid, float))statAct.fp)(caldron, _p1Cid, _p2Cid, _p3Float);
    }

    /**
            Set float value for a concept in the current namespace.
        Parameters:
            statActionCid = static action to perform
            p1 = first concept
            p2 = second concept
            p3 = float value
    */
    void load(Cid statActionCid, DcpDescriptor p1, DcpDescriptor p2, float p3) {
        checkCid!SpStaticConcept(statActionCid);
        checkCid!SpiritDynamicConcept(p1.cid);
        checkCid!SpiritDynamicConcept(p2.cid);

        _statActionCid = statActionCid;
        _p1Cid = p1.cid;
        _p2Cid = p2.cid;
        _p3Float = p3;
    }

    /// Partial setup, without the static action
    void load(DcpDescriptor p1, DcpDescriptor p2, float p3) {
        checkCid!SpiritDynamicConcept(p1.cid);
        checkCid!SpiritDynamicConcept(p2.cid);

        _p1Cid = p1.cid;
        _p2Cid = p2.cid;
        _p3Float = p3;
    }

    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

    // Parameter 1 - a concept cid.
    protected Cid _p1Cid;

    // Parameter 2 - a concept cid.
    protected Cid _p2Cid;

    // Parameter 2 - a float value
    protected float _p3Float;
}

/// Live.
class BinaryFloatAction: Action {

    /// Private constructor. Use live_factory() instead.
    private this(immutable SpBinaryFloatAction spBinaryFloatAction) { super(spBinaryFloatAction); }
}

/// Action, that involves a concept and a float value
alias SA_CidInt = SpUnaryIntAction;         /// SA - spirit action, CidInt - p0Calp1Cidp2Int
class SpUnaryIntAction: SpAction {

    /// Constructor
    this(Cid cid) { super(cid); }

    /// Create live wrapper for the holy static concept.
    override UnaryIntAction live_factory() const {
        return new UnaryIntAction(cast(immutable)this);
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /// Run the static action.
    final override void run(Caldron caldron) {
        auto statAct = (scast!SpStaticConcept(_sm_[_statActionCid]));
        assert(statAct.callType == StatCallType.p0Calp1Cidp2Int,
                format!"Static concept: %s( cid:%s) in SpAction must have StatCallType p0Calp1Cidp2Int and it has %s."
                        (typeid(statAct), _statActionCid, statAct.callType));
        checkCid!DynamicConcept(caldron, _p1Cid);

        (cast(void function(Caldron, Cid, int))statAct.fp)(caldron, _p1Cid, _p2Int);
    }

    /**
            Set int value for a concept in the current namespace.
        Parameters:
            statActionCid = static action to perform
            p1 = concept, that takes the int value
            p2 = int value
    */
    void load(Cid statActionCid, DcpDescriptor p1, int p2) {
        checkCid!SpStaticConcept(statActionCid);
        checkCid!SpiritDynamicConcept(p1.cid);

        _statActionCid = statActionCid;
        _p1Cid = p1.cid;
        _p2Int = p2;
    }

    /// Partial setup, without the static action
    void load(DcpDescriptor p1, int p2) {
        checkCid!SpiritDynamicConcept(p1.cid);

        _p1Cid = p1.cid;
        _p2Int = p2;
    }

    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

    // Parameter 1 - a concept cid.
    protected Cid _p1Cid;

    // Parameter 2 - a float value
    protected int _p2Int;
}

/// Live.
class UnaryIntAction: Action {

    /// Private constructor. Use live_factory() instead.
    private this(immutable SpUnaryIntAction spUnaryIntAction) { super(spUnaryIntAction); }
}
