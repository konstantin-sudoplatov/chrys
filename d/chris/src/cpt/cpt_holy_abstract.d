/// The HolyConcept and its descendants. All holy classes are shared, and they inherit the shared attribute from the root class
/// HolyConcept.
module cpt_holy_abstract;

import global;
import cpt_live_abstract, cpt_live;

/// External runtime function, that creates new objects by their ClassInfo. No constructors are called. Very fast, much faster
/// than manually allocating the object in the heap as new buf[], as it is done in the emplace function (and more economical, no
/// waisting 16 bytes on the array descriptor). Used in the HolyConcept.clone() method.
private extern (C) Object _d_newclass (ClassInfo info);     //

/// Call types of the static concept (static concept is function).
enum StatCallType {
    rCid_p0Cal_p1Cidar_p2Obj,           // Cid function(Caldron nameSpace, Cid[] paramCids, Object extra)
    rCidar_p0Cal_p1Cidar_p2Obj,         // Cid[] function(Caldron nameSpace, Cid[] paramCids, Object extra)
}

abstract:
/**
            Base for all concepts.
    "Holy" means stable and storable as opposed to "Live" concepts, that are in a constant using and change and living only
    in memory. All live concepts contain reference to its holy counterpart. There can be many sin instances that corresponds
    to only one holy partner, which is considered immutable by them.

    "shared" attribute is inherrited by successors and cannot be changed.
*/
shared abstract class HolyConcept {

    immutable Cid cid = 0;       /// Cid of the concept, to check if cid used to find a concept is its actual cid. (paranoia)

    /**
        Create "live" wrapper for this object.
    */
    abstract Concept live_factory() const;

    /**
                Clone an object.
            It makes a shallow copy of an object.
        Note, the runtime type of the object is used, not the compile (declared) type.
            Written by Burton Radons <burton-radons smocky.com>
            https://digitalmars.com/d/archives/digitalmars/D/learn/1625.html
            Tested against memory leaks in the garbage collecter both via copied object omission and omission of reference to other
        object in the its body.
        Parameters:
            srcObject = object to clone
        Returns: cloned object
    */
    Object clone (Object srcObject) {
        if (srcObject is null)
            return null;

        void *copy = cast(void*)_d_newclass(srcObject.classinfo);
        size_t size = srcObject.classinfo.initializer.length;
        copy [8 .. size] = (cast(void *)srcObject)[8 .. size];
        return cast(Object)copy;
    }
}

/**
            Base for all holy dynamic concepts.
*/
abstract class HolyDynamicConcept: HolyConcept {
}

/**
            Base for all holy primitives. They store data. Examples of primitives are a string, a number, a list of strings (log),
    a map of strings/cid (vocabulary) and so on.
    All concrete descendants will have the "_pri" suffix.
*/
abstract class HolyPrimitive: HolyDynamicConcept {}

/**
            Base for all holy actions. The action concept is an interface, bridge between the world of cids and dynamic concepts,
    that knows nothing about the code and the static world, which is a big set of functions, that actually are the code.
    All concrete descendants will have the "_act" suffix.
*/
abstract class HolyAction: HolyDynamicConcept {}

/**
            Base for all premises.
    All concrete descendants will have the "_pre" suffix.
*/
abstract class HolyPremise: HolyDynamicConcept {}

/**
            Base for all neurons.
*/
abstract class HolyNeuron: HolyDynamicConcept {}

/**
            Base for neurons, that take its decisions by pure logic on premises, as opposed to weighing them.
*/
abstract class HolyLogicalNeuron: HolyNeuron {}

/**
            Base for all weighing neurons.
*/
abstract class HolyWeightNeuron: HolyNeuron {}
