module cpt.base_concepts;
import interfaces;

/// Base for all concepts.
/// "Holy" means stable and storable as opposed to "Live" concepts, that are in a constant using and change and living only
/// in memory. All live concepts contain reference to its holy counterpart. There can be many sin instances that corresponds
/// to only one holy partner, which is considered immutable by them.
abstract class HolyConcept {

}

/// Base for all holy dynamic concepts.
abstract class HolyDynamicConcept: HolyConcept {

}

/// Base for all live dynamic concepts.
abstract class LiveDynamicConcept {

    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //                                 Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$--- protected data ---$$$---$$$---$$$---$$$---$$$--
    protected HolyDynamicConcept holY;
}

abstract class HolyNeuron: HolyDynamicConcept {

}

abstract class LiveNeuron: LiveDynamicConcept, EsquashActivationIfc {

    mixin EsquashActivationImpl!LiveNeuron;
}