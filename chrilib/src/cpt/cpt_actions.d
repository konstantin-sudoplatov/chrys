/**
        The action concept is an interface, bridge between the world of cids and dynamic concepts,
    that knows nothing about the code and the static world, which is a big set of functions, that actually are the code.
*/
module cpt.cpt_actions;
import std.stdio;
import std.format, std.typecons;

import proj_data, proj_funcs;

import chri_types, chri_data;
import cpt.cpt_types, cpt.abs.abs_concept, cpt.cpt_stat;
import atn.atn_circle_thread;

/// Spirit Action. Runs a static concept function with signature p0Cal.
@(1) class SpA: SpiritDynamicConcept {

    /**
                Constructor
        Parameters:
            cid = predefined concept identifier
    */
    this(Cid cid) { super(cid); }

    /// Create live wrapper for the holy static concept.
    override A live_factory() const {
        return new A(cast(immutable)this);
    }

    /// Serialize concept
    override Serial serialize() const {
        auto res = Serial(cid, ver, _spReg_[typeid(this)]);

        res.stable.length = St.length;  // allocate
        *cast(Cid*)&res.stable[St._statActionCid_ofs] = _statActionCid;

        return res;
    }

    /// Equality test
    override bool opEquals(Object sc) const {

        if(!super.opEquals(sc)) return false;
        return _statActionCid == scast!(typeof(this))(sc)._statActionCid;
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

    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //                                 Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$--- data ---$$$---$$$---$$$---$$$---$$$--

    // Static action.
    protected Cid _statActionCid;

    //---$$$---$$$---$$$---$$$---$$$--- functions ---$$$---$$$---$$$---$$$---$$$---

    /**
            Initialize concept from its serialized form.
        Parameters:
            stable = stable part of data
            transient = unstable part of data
        Returns: unconsumed slices of the stable and transient byte arrays.
    */
    protected override Tuple!(const byte[], "stable", const byte[], "transient") _deserialize(const byte[] stable,
            const byte[] transient)
    {
        _statActionCid = *cast(Cid*)&stable[St._statActionCid_ofs];

        return tuple!(const byte[], "stable", const byte[], "transient")(stable[St.length..$], transient);
    }

    //===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
    //
    //                                  Private
    //
    //===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@

    /// Stable offsets. Used by serialize()/_deserialize()
    private enum St {
        _statActionCid_ofs = 0,
        length = _statActionCid_ofs + _statActionCid.sizeof
    }

    /// Tranzient offsets. Used by serialize()/_deserialize()
    private enum Tr {
        length = 0
    }
}

unittest {
    auto a = new SpA(42);
    a.ver = 5;
    a.statAction(43);

    SpiritConcept.Serial ser = a.serialize;
    auto b = cast(SpA)a.deserialize(ser.cid, ser.ver, ser.clid, ser.stable, ser.transient);
    assert(ser.cid == 42 && ser.ver == 5 && typeid(b) == typeid(SpA));

    assert(a == b);
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
    this(Cid cid) { super(cid); }

    /// Create live wrapper for the holy static concept.
    override A_Cid live_factory() const {
        return new A_Cid(cast(immutable)this);
    }

    /// Serialize concept
    override Serial serialize() const {
        Serial res = Serial(cid, ver, _spReg_[typeid(this)]);

        res.stable.length = St.length;  // allocate
        *cast(Cid*)&res.stable[St._statActionCid_ofs] = _statActionCid;
        *cast(Cid*)&res.stable[St._p1Cid_ofs] = _p1Cid;

        return res;
    }

    /// Equality test
    override bool opEquals(Object sc) const {

        if(!super.opEquals(sc)) return false;
        auto o = scast!(typeof(this))(sc);
        return _p1Cid == o._p1Cid;
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

    //---%%%---%%%---%%%---%%%---%%% funcs ---%%%---%%%---%%%---%%%---%%%---%%%

    /**
            Initialize concept from its serialized form.
        Parameters:
            stable = stable part of data
            transient = unstable part of data
        Returns: unconsumed slices of the stable and transient byte arrays.
    */
    protected override Tuple!(const byte[], "stable", const byte[], "transient") _deserialize(const byte[] stable,
            const byte[] transient)
    {
        _statActionCid = *cast(Cid*)&stable[St._statActionCid_ofs];
        _p1Cid = *cast(Cid*)&stable[St._p1Cid_ofs];

        return tuple!(const byte[], "stable", const byte[], "transient")(stable[St.length..$], transient);
    }

    //===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
    //
    //                                  Private
    //
    //===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@

    /// Stable offsets. Used by serialize()/_deserialize()
    private enum St {
        _statActionCid_ofs = 0,
        _p1Cid_ofs = _statActionCid_ofs + _statActionCid.sizeof,
        length = _p1Cid_ofs + _p1Cid.sizeof
    }

    /// Tranzient offsets. Used by serialize()/_deserialize()
    private enum Tr {
        length = 0
    }
}

unittest {
    auto a = new SpA_Cid(42);
    a.ver = 5;
    a._statActionCid = 43;
    a._p1Cid = 44;

    SpiritConcept.Serial ser = a.serialize;
    auto b = cast(SpA_Cid)a.deserialize(ser.cid, ser.ver, ser.clid, ser.stable, ser.transient);
    assert(b.cid == 42 && b.ver == 5 && typeid(b) == typeid(SpA_Cid) &&
            b._statActionCid == 43 && b._p1Cid == 44);

    assert(a == b);
}

/// Live.
final class A_Cid: A {

    /// Private constructor. Use live_factory() instead.
    private this(immutable SpA_Cid spUnaryAction) { super(spUnaryAction); }

}

/**
        SpA - spirit action, CidCid - p0Calp1Cidp2Cid
    Actions, that operate on two concepts. Examples: sending a message - the first operand breed of the correspondent,
    the second operand concept object to send.
*/
@(3) final class SpA_CidCid: SpA {

    /// Constructor
    this(Cid cid) { super(cid); }

    /// Create live wrapper for the holy static concept.
    override A_CidCid live_factory() const {
        return new A_CidCid(cast(immutable)this);
    }

    /// Serialize concept
    override Serial serialize() const {
        Serial res = Serial(cid, ver, _spReg_[typeid(this)]);

        res.stable.length = St.length;  // allocate
        *cast(Cid*)&res.stable[St._statActionCid_ofs] = _statActionCid;
        *cast(Cid*)&res.stable[St._p1Cid_ofs] = _p1Cid;
        *cast(Cid*)&res.stable[St._p2Cid_ofs] = _p2Cid;

        return res;
    }

    /// Equality test
    override bool opEquals(Object sc) const {

        if(!super.opEquals(sc)) return false;
        auto o = scast!(typeof(this))(sc);
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

    //---%%%---%%%---%%%---%%%---%%% funcs ---%%%---%%%---%%%---%%%---%%%---%%%

    /**
            Initialize concept from its serialized form.
        Parameters:
            stable = stable part of data
            transient = unstable part of data
        Returns: unconsumed slices of the stable and transient byte arrays.
    */
    protected override Tuple!(const byte[], "stable", const byte[], "transient") _deserialize(const byte[] stable,
            const byte[] transient)
    {
        _statActionCid = *cast(Cid*)&stable[St._statActionCid_ofs];
        _p1Cid = *cast(Cid*)&stable[St._p1Cid_ofs];
        _p2Cid = *cast(Cid*)&stable[St._p2Cid_ofs];

        return tuple!(const byte[], "stable", const byte[], "transient")(stable[St.length..$], transient);
    }

    //===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
    //
    //                                  Private
    //
    //===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@

    /// Stable offsets. Used by serialize()/_deserialize()
    private enum St {
        _statActionCid_ofs = 0,
        _p1Cid_ofs = _statActionCid_ofs + _statActionCid.sizeof,
        _p2Cid_ofs = _p1Cid_ofs + _p1Cid.sizeof,
        length = _p2Cid_ofs + _p2Cid.sizeof
    }

    /// Tranzient offsets. Used by serialize()/_deserialize()
    private enum Tr {
        length = 0
    }
}

unittest {
    auto a = new SpA_CidCid(42);
    a.ver = 5;
    a._statActionCid = 43;
    a._p1Cid = 44;
    a._p2Cid = 45;

    SpiritConcept.Serial ser = a.serialize;
    auto b = cast(SpA_CidCid)a.deserialize(ser.cid, ser.ver, ser.clid, ser.stable, ser.transient);
    assert(b.cid == 42 && b.ver == 5 && typeid(b) == typeid(SpA_CidCid) &&
            b._statActionCid == 43 && b._p1Cid == 44 && b._p2Cid == 45);

    assert(a == b);
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
    this(Cid cid) { super(cid); }

    /// Create live wrapper for the holy static concept.
    override A_CidFloat live_factory() const {
        return new A_CidFloat(cast(immutable)this);
    }

    /// Serialize concept
    override Serial serialize() const {
        Serial res = Serial(cid, ver, _spReg_[typeid(this)]);

        res.stable.length = St.length;  // allocate
        *cast(Cid*)&res.stable[St._statActionCid_ofs] = _statActionCid;
        *cast(Cid*)&res.stable[St._p1Cid_ofs] = _p1Cid;
        *cast(float*)&res.stable[St._p2Float_ofs] = _p2Float;

        return res;
    }

    /// Equality test
    override bool opEquals(Object sc) const {

        if(!super.opEquals(sc)) return false;
        auto o = scast!(typeof(this))(sc);
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

    //---%%%---%%%---%%%---%%%---%%% funcs ---%%%---%%%---%%%---%%%---%%%---%%%

    /**
            Initialize concept from its serialized form.
        Parameters:
            stable = stable part of data
            transient = unstable part of data
        Returns: unconsumed slices of the stable and transient byte arrays.
    */
    protected override Tuple!(const byte[], "stable", const byte[], "transient") _deserialize(const byte[] stable,
            const byte[] transient)
    {
        _statActionCid = *cast(Cid*)&stable[St._statActionCid_ofs];
        _p1Cid = *cast(Cid*)&stable[St._p1Cid_ofs];
        _p2Float = *cast(float*)&stable[St._p2Float_ofs];

        return tuple!(const byte[], "stable", const byte[], "transient")(stable[St.length..$], transient);
    }

    //===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
    //
    //                                  Private
    //
    //===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@

    /// Stable offsets. Used by serialize()/_deserialize()
    private enum St {
        _statActionCid_ofs = 0,
        _p1Cid_ofs = _statActionCid_ofs + _statActionCid.sizeof,
        _p2Float_ofs = _p1Cid_ofs + _p1Cid.sizeof,
        length = _p2Float_ofs + _p2Float.sizeof
    }

    /// Tranzient offsets. Used by serialize()/_deserialize()
    private enum Tr {
        length = 0
    }
}

unittest {
    auto a = new SpA_CidFloat(42);
    a.ver = 5;
    a._statActionCid = 43;
    a._p1Cid = 44;
    a._p2Float = 4.5;

    SpiritConcept.Serial ser = a.serialize;
    auto b = cast(SpA_CidFloat)a.deserialize(ser.cid, ser.ver, ser.clid, ser.stable, ser.transient);
    assert(b.cid == 42 && b.ver == 5 && typeid(b) == typeid(SpA_CidFloat) &&
            b._statActionCid == 43 && b._p1Cid == 44 && b._p2Float == 4.5);

    assert(a == b);
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
    this(Cid cid) { super(cid); }

    /// Create live wrapper for the holy static concept.
    override A_CidCidFloat live_factory() const {
        return new A_CidCidFloat(cast(immutable)this);
    }

    /// Serialize concept
    override Serial serialize() const {
        Serial res = Serial(cid, ver, _spReg_[typeid(this)]);

        res.stable.length = St.length;  // allocate
        *cast(Cid*)&res.stable[St._statActionCid_ofs] = _statActionCid;
        *cast(Cid*)&res.stable[St._p1Cid_ofs] = _p1Cid;
        *cast(Cid*)&res.stable[St._p2Cid_ofs] = _p2Cid;
        *cast(float*)&res.stable[St._p3Float_ofs] = _p3Float;

        return res;
    }

    /// Equality test
    override bool opEquals(Object sc) const {

        if(!super.opEquals(sc)) return false;
        auto o = scast!(typeof(this))(sc);
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

    //---%%%---%%%---%%%---%%%---%%% funcs ---%%%---%%%---%%%---%%%---%%%---%%%

    /**
            Initialize concept from its serialized form.
        Parameters:
            stable = stable part of data
            transient = unstable part of data
        Returns: unconsumed slices of the stable and transient byte arrays.
    */
    protected override Tuple!(const byte[], "stable", const byte[], "transient") _deserialize(const byte[] stable,
            const byte[] transient)
    {
        _statActionCid = *cast(Cid*)&stable[St._statActionCid_ofs];
        _p1Cid = *cast(Cid*)&stable[St._p1Cid_ofs];
        _p2Cid = *cast(Cid*)&stable[St._p2Cid_ofs];
        _p3Float = *cast(float*)&stable[St._p3Float_ofs];

        return tuple!(const byte[], "stable", const byte[], "transient")(stable[St.length..$], transient);
    }

    //===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
    //
    //                                  Private
    //
    //===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@

    /// Stable offsets. Used by serialize()/_deserialize()
    private enum St {
        _statActionCid_ofs = 0,
        _p1Cid_ofs = _statActionCid_ofs + _statActionCid.sizeof,
        _p2Cid_ofs = _p1Cid_ofs + _p1Cid.sizeof,
        _p3Float_ofs = _p2Cid_ofs + _p2Cid.sizeof,
        length = _p3Float_ofs + _p3Float.sizeof
    }

    /// Tranzient offsets. Used by serialize()/_deserialize()
    private enum Tr {
        length = 0
    }
}

unittest {
    auto a = new SpA_CidCidFloat(42);
    a.ver = 5;
    a._statActionCid = 43;
    a._p1Cid = 44;
    a._p2Cid = 45;
    a._p3Float = 4.5;

    SpiritConcept.Serial ser = a.serialize;
    auto b = cast(SpA_CidCidFloat)a.deserialize(ser.cid, ser.ver, ser.clid, ser.stable, ser.transient);
    assert(b.cid == 42 && b.ver == 5 && typeid(b) == typeid(SpA_CidCidFloat) &&
            b._statActionCid == 43 && b._p1Cid == 44 && b._p2Cid == 45 && b._p3Float == 4.5);

    assert(a == b);
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
    this(Cid cid) { super(cid); }

    /// Create live wrapper for the holy static concept.
    override A_CidInt live_factory() const {
        return new A_CidInt(cast(immutable)this);
    }

    /// Serialize concept
    override Serial serialize() const {
        Serial res = Serial(cid, ver, _spReg_[typeid(this)]);

        res.stable.length = St.length;  // allocate
        *cast(Cid*)&res.stable[St._statActionCid_ofs] = _statActionCid;
        *cast(Cid*)&res.stable[St._p1Cid_ofs] = _p1Cid;
        *cast(int*)&res.stable[St._p2Int_ofs] = _p2Int;

        return res;
    }

    /// Equality test
    override bool opEquals(Object sc) const {

        if(!super.opEquals(sc)) return false;
        auto o = scast!(typeof(this))(sc);
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

    //---%%%---%%%---%%%---%%%---%%% funcs ---%%%---%%%---%%%---%%%---%%%---%%%

    /**
            Initialize concept from its serialized form.
        Parameters:
            stable = stable part of data
            transient = unstable part of data
        Returns: unconsumed slices of the stable and transient byte arrays.
    */
    protected override Tuple!(const byte[], "stable", const byte[], "transient") _deserialize(const byte[] stable,
            const byte[] transient)
    {
        _statActionCid = *cast(Cid*)&stable[St._statActionCid_ofs];
        _p1Cid = *cast(Cid*)&stable[St._p1Cid_ofs];
        _p2Int = *cast(int*)&stable[St._p2Int_ofs];

        return tuple!(const byte[], "stable", const byte[], "transient")(stable[St.length..$], transient);
    }

    //===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
    //
    //                                  Private
    //
    //===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@

    /// Stable offsets. Used by serialize()/_deserialize()
    private enum St {
        _statActionCid_ofs = 0,
        _p1Cid_ofs = _statActionCid_ofs + _statActionCid.sizeof,
        _p2Int_ofs = _p1Cid_ofs + _p1Cid.sizeof,
        length = _p2Int_ofs + _p2Int.sizeof
    }

    /// Tranzient offsets. Used by serialize()/_deserialize()
    private enum Tr {
        length = 0
    }
}

unittest {
    auto a = new SpA_CidInt(42);
    a.ver = 5;
    a._statActionCid = 43;
    a._p1Cid = 44;
    a._p2Int = 45;

    SpiritConcept.Serial ser = a.serialize;
    auto b = cast(SpA_CidInt)a.deserialize(ser.cid, ser.ver, ser.clid, ser.stable, ser.transient);
    assert(b.cid == 42 && b.ver == 5 && typeid(b) == typeid(SpA_CidInt) &&
            b._statActionCid == 43 && b._p1Cid == 44 && b._p2Int == 45);

    assert(a == b);
}

/// Live.
final class A_CidInt: A {

    /// Private constructor. Use live_factory() instead.
    private this(immutable SpA_CidInt spUnaryIntAction) { super(spUnaryIntAction); }
}
