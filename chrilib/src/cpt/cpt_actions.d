/**
        The action concept is an interface, bridge between the world of cids and dynamic concepts,
    that knows nothing about the code and the static world, which is a big set of functions, that actually are the code.
*/
module cpt.cpt_actions;
import std.format;

import project_params, tools;

import chri_shared;
import cpt.cpt_types, cpt.abs.abs_concept, cpt.cpt_stat;
import atn.atn_circle_thread;
import stat.stat_types;
import crank.crank_types: DcpDescriptor;

/// Spirit Action. Runs a static concept function with signature p0Cal.
@(1) class SpA: SpiritDynamicConcept {

    /**
                Constructor
        Parameters:
            cid = predefined concept identifier
            clid = class identifier
    */
    this(Cid cid, Clid clid = spClid!(typeof(this)) ) { super(cid, clid); }

    /// Create live wrapper for the holy static concept.
    override A live_factory() const {
        return new A(cast(immutable)this);
    }

    /// Equality test
    override bool opEquals(SpiritConcept sc) const {

        if(!super.opEquals(sc)) return false;
        return _statActionCid == (cast(typeof(this))sc)._statActionCid;
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
class A: DynamicConcept {

    /// Private constructor. Use SpiritConcept.live_factory() instead.
    private this(immutable SpA spAction) { super(spAction); }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
                Call static concept function.
        Parameters:
            caldron = name space it which static concept function will be working.
    */
    void run(Caldron caldron) {
        assert((cast(SpA)spirit).statAction != 0, format!"Cid: %s, static action must be assigned."(this.cid));

        (cast(SpA)spirit).run(caldron);
    }
}

/// SpA - spirit action, Cid - p0Calp1Cid
/// Action, that operate on only one concept. Examples: activate/anactivate concept.
@(2) final class SpA_Cid: SpA {

    /// Constructor
    this(Cid cid) { super(cid, spClid!(typeof(this))); }

    /// Create live wrapper for the holy static concept.
    override A_Cid live_factory() const {
        return new A_Cid(cast(immutable)this);
    }

    /// Equality test
    override bool opEquals(SpiritConcept sc) const {

        if(!super.opEquals(sc)) return false;
        return _p1Cid == (cast(typeof(this))sc)._p1Cid;
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
                Call static concept function.
        Parameters:
            caldron = name space it which static concept function will be working.
    */
    override void run(Caldron caldron) {
        auto statAct = (scast!SpStaticConcept(_sm_[_statActionCid]));
        assert(statAct.callType == StatCallType.p0Calp1Cid,
                format!"Static concept: %s( cid:%s) in SpAction must have StatCallType p0Calp1Cid and it has %s."
                        (typeid(statAct), _statActionCid, statAct.callType));
        checkCid!DynamicConcept(caldron, _p1Cid);

        (cast(void function(Caldron, Cid))statAct.fp)(caldron, _p1Cid);
    }

    /// Full setup
    void load(Cid statAction, DcpDescriptor operand) {
        checkCid!SpStaticConcept(statAction);
        _statActionCid = statAction;
        checkCid!SpiritDynamicConcept(operand.cid);
        _p1Cid = operand.cid;
    }

    /// Partial setup, only operand
    void load(DcpDescriptor operand) {
        checkCid!SpiritDynamicConcept(operand.cid);
        _p1Cid = operand.cid;
    }

    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

    /// Cid of a concept to operate on
    protected Cid _p1Cid;
}

/// Live.
final class A_Cid: A {

    /// Private constructor. Use live_factory() instead.
    private this(immutable SpA_Cid spUnaryAction) { super(spUnaryAction); }

}

/// SpA - spirit action, CidCid - p0Calp1Cidp2Cid
/// Actions, that operate on two concepts. Examples: sending a message - the first operand breed of the correspondent,
/// the second operand concept object to send.
@(3) final class SpA_CidCid: SpA {

    /// Constructor
    this(Cid cid) { super(cid, spClid!(typeof(this))); }

    /// Create live wrapper for the holy static concept.
    override A_CidCid live_factory() const {
        return new A_CidCid(cast(immutable)this);
    }

    /// Equality test
    override bool opEquals(SpiritConcept sc) const {

        if(!super.opEquals(sc)) return false;
        auto o = cast(typeof(this))sc;
        return _p1Cid == o._p1Cid && _p2Cid == o._p2Cid;
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
                Call static concept function.
        Parameters:
            caldron = name space it which static concept function will be working.
    */
    override void run(Caldron caldron) {
        auto statAct = (scast!SpStaticConcept(_sm_[_statActionCid]));
        assert(statAct.callType == StatCallType.p0Calp1Cidp2Cid,
                format!"Static concept: %s( cid:%s) in SpAction must have StatCallType p0Calp1Cidp2Cid and it has %s."
                        (typeid(statAct), _statActionCid, statAct.callType));
        checkCid!DynamicConcept(caldron, _p1Cid);
        checkCid!DynamicConcept(caldron, _p2Cid);

        (cast(void function(Caldron, Cid, Cid))statAct.fp)(caldron, _p1Cid, _p2Cid);
    }

    /// Full setup
    void load(Cid statAction, DcpDescriptor firstOperand, DcpDescriptor secondOperand) {
        checkCid!SpStaticConcept(statAction);

        _statActionCid = statAction;
        checkCid!SpiritDynamicConcept(firstOperand.cid);
        _p1Cid = firstOperand.cid;
        checkCid!SpiritDynamicConcept(secondOperand.cid);
        _p2Cid = secondOperand.cid;
    }

    /// Partial setup, without the static action
    void load(DcpDescriptor firstOperand, DcpDescriptor secondOperand) {
        checkCid!SpiritDynamicConcept(firstOperand.cid);
        _p1Cid = firstOperand.cid;
        checkCid!SpiritDynamicConcept(secondOperand.cid);
        _p2Cid = secondOperand.cid;
    }

    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

    /// Cid of the first concept
    protected Cid _p1Cid;

    /// Cid of the second concept
    protected Cid _p2Cid;
}

/// Live.
final class A_CidCid: A {

    /// Private constructor. Use live_factory() instead.
    private this(immutable SpA_CidCid spBinaryAction) { super(spBinaryAction); }
}

/// SpA - spirit action, CidFloat - p0Calp1Cidp2Float
/// Action, that works on a concept and a float value
@(4) final class SpA_CidFloat: SpA {

    /// Constructor
    this(Cid cid) { super(cid, spClid!(typeof(this))); }

    /// Create live wrapper for the holy static concept.
    override A_CidFloat live_factory() const {
        return new A_CidFloat(cast(immutable)this);
    }

    /// Equality test
    override bool opEquals(SpiritConcept sc) const {

        if(!super.opEquals(sc)) return false;
        auto o = cast(typeof(this))sc;
        return _p1Cid == o._p1Cid && _p2Float == o._p2Float;
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /// Run the static action.
    override void run(Caldron caldron) {
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
final class A_CidFloat: A {

    /// Private constructor. Use live_factory() instead.
    private this(immutable SpA_CidFloat spUnaryFloatAction) { super(spUnaryFloatAction); }
}

/// SpA - spirit action, CidCidFloat stands for p0Calp1Cidp2Cidp3Float
/// Action, that involves two concepts and a float value
@(5) final class SpA_CidCidFloat: SpA {

    /// Constructor
    this(Cid cid) { super(cid, spClid!(typeof(this))); }

    /// Create live wrapper for the holy static concept.
    override A_CidCidFloat live_factory() const {
        return new A_CidCidFloat(cast(immutable)this);
    }

    /// Equality test
    override bool opEquals(SpiritConcept sc) const {

        if(!super.opEquals(sc)) return false;
        auto o = cast(typeof(this))sc;
        return _p1Cid == o._p1Cid && _p2Cid == o._p2Cid && _p3Float == o._p3Float;
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /// Run the static action.
    override void run(Caldron caldron) {
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
final class A_CidCidFloat: A {

    /// Private constructor. Use live_factory() instead.
    private this(immutable SpA_CidCidFloat spBinaryFloatAction) { super(spBinaryFloatAction); }
}

/// SpA - spirit action, CidInt - p0Calp1Cidp2Int
/// Action, that involves a concept and a float value
@(6) final class SpA_CidInt: SpA {

    /// Constructor
    this(Cid cid) { super(cid, spClid!(typeof(this))); }

    /// Create live wrapper for the holy static concept.
    override A_CidInt live_factory() const {
        return new A_CidInt(cast(immutable)this);
    }

    /// Equality test
    override bool opEquals(SpiritConcept sc) const {

        if(!super.opEquals(sc)) return false;
        auto o = cast(typeof(this))sc;
        return _p1Cid == o._p1Cid && _p2Int == o._p2Int;
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /// Run the static action.
    override void run(Caldron caldron) {
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
final class A_CidInt: A {

    /// Private constructor. Use live_factory() instead.
    private this(immutable SpA_CidInt spUnaryIntAction) { super(spUnaryIntAction); }
}
