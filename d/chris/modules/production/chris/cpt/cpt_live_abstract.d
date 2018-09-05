module cpt_live_abstract;
import std.stdio;

import global, tools;
import interfaces;
import cpt_holy_abstract;

/// Live wrapper for the HolyConcept
abstract class Concept {
    immutable HolyConcept holy;

    /// Constructor
    this(immutable HolyConcept holy) {
        this.holy = holy;
    }

    /// Overrided default Object.toString()
    override string toString() {
        import std.format: format;
        if
        (auto name = holy.cid in _nm_)
            return format!"%s: cid = %s, name = %s"(super.toString, holy.cid, *name);
        else
            return format!"%s: cid = %s"(super.toString, holy.cid);
    }
}

/// Ditto
abstract class DynamicConcept: Concept {
    this(immutable HolyDynamicConcept holyDynamicConcept) { super(holyDynamicConcept); }
}

/// Ditto
abstract class Primitive: DynamicConcept {
    this(immutable HolyPrimitive holyPrimitive) { super(holyPrimitive); }
}

/// Ditto
abstract class Action: DynamicConcept {
    this(immutable HolyAction holyAction) { super(holyAction); }
}

/// Ditto
abstract class Premise: DynamicConcept {
    this(immutable HolyPremise holyPremise) { super(holyPremise); }
}

/// Ditto
abstract class Neuron: DynamicConcept {
    this(immutable HolyNeuron holyNeuron) { super(holyNeuron); }
}

/// Ditto
abstract class LogicalNeuron: DynamicConcept, BinActivationIfc {

    /// Constructor
    this (immutable HolyLogicalNeuron holyLogicalNeuron) { super(holyLogicalNeuron); }

    // implementation of the interface
    mixin BinActivationImpl!LogicalNeuron;
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
