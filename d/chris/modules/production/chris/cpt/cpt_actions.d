module cpt_actions;
import std.stdio;
import std.format;

import global, tools;
import cpt_abstract, cpt_pile;
import attn_circle_thread;

/**
            Base for all holy actions. The action concept is an interface, bridge between the world of cids and dynamic concepts,
    that knows nothing about the code and the static world, which is a big set of functions, that actually are the code.
    All concrete descendants will have the "_act" suffix.
*/
class HolyAction: HolyDynamicConcept {

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
    void _do_(Caldron caldron) {
        assert((cast(HolyStaticConcept)_hm_[statActionCid_])._callType_ == StatCallType.p0Cal,
                format!"Static concept: %s( cid:%s) in HolyAction must have StatCallType none and it has %s."
                      (_nm_[statActionCid_], statActionCid_, (cast(HolyStaticConcept)_hm_[statActionCid_])._callType_));

        auto statCpt = (cast(HolyStaticConcept)_hm_[statActionCid_]);
        (cast(void function(Caldron))statCpt.fp)(caldron);
    }

    /// Getter
    @property Cid _statActionCid_() {
        return statActionCid_;
    }

    /// Setter
    @property Cid _statActionCid_(Cid cid) {
        debug _checkCid_!HolyStaticConcept(cid);
        return statActionCid_ = cid;
    }

    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

    // Static action.
    private Cid statActionCid_;
}

/// Live.
class Action: DynamicConcept {

    /// Private constructor. Use HolyTidPrimitive.live_factory() instead.
    private this(immutable HolyAction holyAction) { super(holyAction); }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
                Call static concept function.
        Parameters:
            caldron = name space it which static concept function will be working.
    */
    void _do_(Caldron caldron) {
        assert((cast(shared HolyAction)holy)._statActionCid_ != 0, format!"Cid: %s, static action must be assigned."(this.cid));
        (cast(shared HolyAction)holy)._do_(caldron);
    }
}

/// Actions, that operate on only one concept
class HolyUnaryOperation: HolyAction {

    /// Constructor
    this(Cid cid) { super(cid); }

    /// Create live wrapper for the holy static concept.
    override UnaryOperation live_factory() const {
        return new UnaryOperation(cast(immutable)this);
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
                Call static concept function.
        Parameters:
            caldron = name space it which static concept function will be working.
    */
    override void _do_(Caldron caldron) {
        assert((cast(HolyStaticConcept)_hm_[statActionCid_])._callType_ == StatCallType.p0Calp1Cid,
                format!"Static concept: %s( cid:%s) in HolyAction must have StatCallType none and it has %s."
                      (_nm_[statActionCid_], statActionCid_, (cast(HolyStaticConcept)_hm_[statActionCid_])._callType_));

        auto statCpt = (cast(HolyStaticConcept)_hm_[statActionCid_]);
        assert(operandCid_ != 0, "Operand must be assigned.");
        (cast(void function(Caldron, Cid))statCpt.fp)(caldron, operandCid_);
    }

    /// Getter
    @property Cid _operandCid_() {
        return operandCid_;
    }

    /// Setter
    @property Cid _operandCid_(Cid cid) {
        _checkCid_!HolyDynamicConcept(cid);
        return operandCid_ = cid;
    }

    /// Adapter
    @property Cid _operandCid_(CptDescriptor cd) {
        _checkCid_!HolyDynamicConcept(cd.cid);
        return operandCid_ = cd.cid;
    }

    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

    /// Cid of a concept to operate on
    private Cid operandCid_;
}

/// Live.
class UnaryOperation: Action {

    /// Private constructor. Use live_factory() instead.
    private this(immutable HolyUnaryOperation holyUnaryOperation) { super(holyUnaryOperation); }

}
//---***---***---***---***---***--- types ---***---***---***---***---***---***

//---***---***---***---***---***--- data ---***---***---***---***---***--

/**
        Constructor
*/
//this(){}

//---***---***---***---***---***--- functions ---***---***---***---***---***--

//~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
//
//                                 Protected
//
//~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
protected:
//---$$$---$$$---$$$---$$$---$$$--- data ---$$$---$$$---$$$---$$$---$$$--

//---$$$---$$$---$$$---$$$---$$$--- functions ---$$$---$$$---$$$---$$$---$$$---

//---$$$---$$$---$$$---$$$---$$$--- types ---$$$---$$$---$$$---$$$---$$$---

//===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
//
//                                  Private
//
//===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
private:
//---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
