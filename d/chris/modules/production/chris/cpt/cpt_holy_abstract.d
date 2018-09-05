/// The HolyConcept and its descendants. All holy classes are shared, and they inherit the shared attribute from the root class
/// HolyConcept.
module cpt_holy_abstract;

import global, tools;
import interfaces;
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
                Default constructor.
            Cid will be generated and assigned in the _hm_.add() method.
    */
    this() {}

    /**
                Constructor
            Used for concepts with predefined cids.
        Parameters:
            cid = concept identifier
    */
    this(Cid cid) {
        this.cid = cid;
    }

    /**
        Create "live" wrapper for this object.
    */
    abstract Concept live_factory() const;

    /**
                Clone an object.
            It makes a shallow copy of this object.
        Note, the runtime type of the object is used, not the compile (declared) type.
            Written by Burton Radons <burton-radons smocky.com>
            https://digitalmars.com/d/archives/digitalmars/D/learn/1625.html
            Tested against memory leaks in the garbage collecter both via copied object omission and omission of reference to other
        object in the its body.
        Parameters:
            srcObject = object to clone
        Returns: cloned object
    */
    shared(HolyConcept) clone() const {

        void* copy = cast(void*)_d_newclass(this.classinfo);
        size_t size = this.classinfo.initializer.length;
        copy [8 .. size] = (cast(void *)this)[8 .. size];
        return cast(shared HolyConcept)copy;
    }
}

/**
            Base for all holy dynamic concepts.
*/
abstract class HolyDynamicConcept: HolyConcept {

    /**
                Default constructor.
            Cid will be generated and assigned in the _hm_.add() method.
    */
    this() {}

    /**
                Constructor
        Parameters:
            Used for concepts with predefined cids.
            cid = concept identifier
    */
    this(Cid cid) { super(cid); }
}

/**
            Base for all holy primitives. They store data. Examples of primitives are a string, a number, a list of strings (log),
    a map of strings/cid (vocabulary) and so on.
    All concrete descendants will have the "_pri" suffix.
*/
abstract class HolyPrimitive: HolyDynamicConcept {

    /**
                Default constructor.
            Cid will be generated and assigned in the _hm_.add() method.
    */
    this() {}

    /**
                Constructor
        Parameters:
            Used for concepts with predefined cids.
            cid = concept identifier
    */
    this(Cid cid) { super(cid); }
}

/**
            Base for all holy actions. The action concept is an interface, bridge between the world of cids and dynamic concepts,
    that knows nothing about the code and the static world, which is a big set of functions, that actually are the code.
    All concrete descendants will have the "_act" suffix.
*/
abstract class HolyAction: HolyDynamicConcept {

    /**
                Default constructor.
            Cid will be generated and assigned in the _hm_.add() method.
    */
    this() {}

    /**
                Constructor
        Parameters:
            Used for concepts with predefined cids.
            cid = concept identifier
    */
    this(Cid cid) { super(cid); }
}

/**
            Base for all premises.
    All concrete descendants will have the "_pre" suffix.
*/
abstract class HolyPremise: HolyDynamicConcept {

    /**
                Default constructor.
            Cid will be generated and assigned in the _hm_.add() method.
    */
    this() {}

    /**
                Constructor
        Parameters:
            Used for concepts with predefined cids.
            cid = concept identifier
    */
    this(Cid cid) { super(cid); }
}

/**
            Base for all neurons.
*/
abstract class HolyNeuron: HolyDynamicConcept {

    /**
                Default constructor.
            Cid will be generated and assigned in the _hm_.add() method.
    */
    this() {}

    /**
                Constructor
        Parameters:
            Used for concepts with predefined cids.
            cid = concept identifier
    */
    this(Cid cid) { super(cid); }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /// Ditto.
    override shared(HolyNeuron) clone() const {

        // Take shallow copy
        shared HolyNeuron clon = cast(shared HolyNeuron)super.clone;

        // Make it deep.
        clon.effects_ = (cast()this).effects_.dup;
        foreach(int i, eff; clon.effects_) {
            clon.effects_[i].branches = effects_[i].branches.dup;
            clon.effects_[i].actions = effects_[i].actions.dup;
        }

        return clon;
    }


    //===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
    //
    //                                  Private
    //
    //===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@

    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

    /// Effects array represents effects (actions to be taken and branches to be set as the next step in the reasoning process),
    /// corresponding to spans of the neuron's activation. Each span is represented by its lower boundary and goes from the lower boundary
    /// of the previous span including (from float.infinity if it is the first span) to the own lower boundary excluding.
    /// Elements are sorted in the descending order of lower boundaries. Lower boundary can't be repeated more than twice. For
    /// example, sequence 10, 1, 1 represents the following spans: [+float.infinity, 10[; [10, 1[; [1]. In this example
    /// the span ]1, -float.infinity] is not defined and by default it means no actions and stop and wait as the branch value.
    /// In all cases when the actions array is null or empty that means no actions, the branches array is null or empty, stop and wait.
    private Effect[] effects_;

    //---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--

    /// Element of the effects_ array.
    private static struct Effect {
        float lowerBoundary;    /// lower boundary of the span (excluding)
        Cid[] branches;         /// list of branches where the first branch is the next head of the current branch and the rest will be spawned
        Cid[] actions;          /// actions, that will be taken before the branching
    }
}

unittest {
    import cpt_holy: HolyWeightNeuron;

    // check cloning
    shared HolyNeuron nrn = new shared HolyWeightNeuron;
    nrn.effects_ = [HolyNeuron.Effect(1, [10, 11], [20, 21])];
    shared const HolyNeuron nrn1 = cast(shared HolyWeightNeuron)nrn.clone;
    assert(nrn1 !is nrn);
    assert(nrn1.effects_ !is nrn.effects_);
    assert(nrn1.effects_ == nrn.effects_);
    assert(nrn1.effects_[0].branches !is nrn.effects_[0].branches);
    assert(nrn1.effects_[0].branches == nrn.effects_[0].branches);
    assert(nrn1.effects_[0].actions !is nrn.effects_[0].actions);
    assert(nrn1.effects_[0].actions == nrn.effects_[0].actions);
}

/**
            Base for neurons, that take its decisions by pure logic on premises, as opposed to weighing them.
*/
abstract class HolyLogicalNeuron: HolyNeuron {

    /**
                Default constructor.
            Cid will be generated and assigned in the _hm_.add() method.
    */
    this() {}

    /**
                Constructor
        Parameters:
            Used for concepts with predefined cids.
            cid = concept identifier
    */
    this(Cid cid) { super(cid); }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--
}
