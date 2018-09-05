module cpt_live;
import std.stdio;

import global, tools;
import cpt_live_abstract, cpt_holy;
import interfaces;

/// Live wrapper for the HolyConcept class
final class StaticConcept: Concept {
    this(immutable HolyStaticConcept holyStaticConcept) { super(holyStaticConcept); }
}

/// Ditto
final class Action: DynamicConcept {
    this(immutable HolyAction holyAction) { super(holyAction); }
}

/// Ditto
class UnconditionalNeuron: Neuron {
    this(immutable HolyUnconditionalNeuron holyUnconditionalNeuron) { super(holyUnconditionalNeuron);}
}

/// Ditto
final class Seed: UnconditionalNeuron {
    this(immutable HolySeed holySeed) { super(holySeed); }
}

/// Ditto
final class WeightNeuron: DynamicConcept, EsquashActivationIfc {

    /// Constructor
    this (immutable HolyWeightNeuron holyWeightNeuron) { super(holyWeightNeuron); }

    // implementation of the interface
    mixin EsquashActivationImpl!WeightNeuron;
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
