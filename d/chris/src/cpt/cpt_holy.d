module cpt_holy;

import global;
import interfaces;

/// Call types of the static concept (static concept is function).
enum StatCallType {
    rCid_p0Cal_p1Cidar_p2Obj,           // Cid function(Caldron nameSpace, Cid[] paramCids, Object extra)
    rCidar_p0Cal_p1Cidar_p2Obj,         // Cid[] function(Caldron nameSpace, Cid[] paramCids, Object extra)
}

/**
            Base for all concepts.
    "Holy" means stable and storable as opposed to "Live" concepts, that are in a constant using and change and living only
    in memory. All live concepts contain reference to its holy counterpart. There can be many sin instances that corresponds
    to only one holy partner, which is considered immutable by them.

    "shared" attribute is inherrited by successors and cannot be changed.
*/
private extern (C) Object _d_newclass (ClassInfo info);
shared abstract class HolyConcept {


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
    Object clone (Object srcObject)
    {
        if (srcObject is null)
            return null;

        void *copy = cast(void*)_d_newclass(srcObject.classinfo);
        size_t size = srcObject.classinfo.initializer.length;
        copy [8 .. size] = (cast(void *)srcObject)[8 .. size];
        return cast(Object)copy;
    }

protected:
    immutable Cid _cid = 0;       /// Cid of the concept, to check if cid used to find a concept is its actual cid. (paranoia)
}

/**
            Static concept.
*/
shared final class StaticConcept: HolyConcept {
    import common_types;

    immutable void* fp;                     /// function pointer to the static concept function
    immutable StatCallType call_type;       /// call type of the static concept function

    /**
                Constructor
        Parameters:
            cid = cid
            fp = function pointer to the static concept function
            callType = call type of the static concept function
    */
    this(Cid cid, void* fp, StatCallType callType){
        cast()_cid = cid;
        cast()this.fp = cast(immutable)fp;
        cast()call_type = callType;
    }
}

/**
            Base for all holy dynamic concepts.
*/
abstract class HolyDynamicConcept: HolyConcept {
    this(){}
}

/**
            Base for all neurons.
        All of its descendant class names will have the suffix of "_hnr".
*/
abstract class HolyNeuron: HolyDynamicConcept {
    this(){
        super();
    }
}

/**
            Uncontitional neuron.
        It is a degenerate, capable only of applying its effects without consulting any premises. Its activation is always 1.
 */
class UnconditionalNeuron_hnr: HolyDynamicConcept {
    this() {
        super();
    }
}

/**
            Seed.
*/
class Seed: UnconditionalNeuron_hnr {
    this() { super(); }
}