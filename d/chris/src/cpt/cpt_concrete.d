module cpt_concrete;

import cpt_abstract;
import interfaces;

import global, tools;

/**
            Static concept.
    Actually, it is immutable since all fields are immutable. Making the class or constructor immutable, however would introduce
    unneccessary complexity in the code, that uses this class.
*/
final class HolyStaticConcept: HolyConcept {

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
        super(cid);
        cast()flags |= HolyCptFlags.STATIC;
        cast()this.fp = cast(immutable)fp;
        cast()call_type = callType;
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
        Create live wrapper for the holy static concept.
    */
    override StaticConcept live_factory() const {
        return new StaticConcept(cast(immutable)this);
    }
}

/// Live wrapper for the HolyConcept class
final class StaticConcept: Concept {

    /// Private constructor. Use HolyTidPrimitive.live_factory() instead.
    private this(immutable HolyStaticConcept holyStaticConcept) { super(holyStaticConcept); }
}

/**
            Base for all holy actions. The action concept is an interface, bridge between the world of cids and dynamic concepts,
    that knows nothing about the code and the static world, which is a big set of functions, that actually are the code.
    All concrete descendants will have the "_act" suffix.
*/
final class HolyAction: HolyDynamicConcept {

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

    /**
        Create live wrapper for the holy static concept.
    */
    override Action live_factory() const {
        return new Action(cast(immutable)this);
    }
}

/// Ditto
final class Action: DynamicConcept {

    /// Private constructor. Use HolyTidPrimitive.live_factory() instead.
    private this(immutable HolyAction holyAction) { super(holyAction); }
}

/**
            Branch identifier.
        On one hand it is a container for TID. TID itself is stored in the live part, since it is a changeable entity. On the
    other, it is a pointer to the seed of the branch. Its cid is stored in the holy part.

        This concept can be used to start new branch instead of the seed, if we want to have in the parent branch a handler
    to a child to send it messages. This concept will be that handler. After the new branch started, its tid will be put
    in the tid_ field of the live part.
*/
final class HolyBreed: HolyPrimitive {

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
    this(Cid cid) {
        super(cid);
    }

    /// Getter.
    const(Cid) seed_cid() const {
        return seedCid_;
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
        Create live wrapper for the holy tid primitive concept.
    */
    override Breed live_factory() const {
        return new Breed(cast(immutable)this);
    }

    //===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
    //
    //                                  Private
    //
    //===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@

    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

    /// The seed of the branch.
    private Cid seedCid_;
}

/// Ditto.
final class Breed: Primitive, ReadinessCheckIfc {
    import std.concurrency: Tid;

    /// Private constructor. Use HolyTidPrimitive.live_factory() instead.
    private this(immutable HolyBreed holyTidPrimitive) { super(holyTidPrimitive); }

    /// Getter.
    @property Tid tid() {
        return tid_;
    }

    /// Setter.
    @property Tid tid(Tid tid) {
        return tid_ = tid;
    }

    /// Getter.
    const(Cid) seed_cid() const {
        return (cast(immutable HolyBreed)holy).seed_cid;
    }

    /// Mixin up/down states.
    mixin ReadinessCheckImpl!(Breed, false);     // initialize to down

    //===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
    //
    //                                  Private
    //
    //===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@

    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

    /// Thread identifier.
    private Tid tid_;
}

/**
            Uncontitional neuron.
        It is a degenerate neuron, capable only of applying its effects without consulting any premises. Its activation is always 1.
 */
class HolyUnconditionalNeuron: HolyNeuron {

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

    /**
        Create live wrapper for the holy static concept.
    */
    override UnconditionalNeuron live_factory() const {
        return new UnconditionalNeuron(cast(immutable)this);
    }
}

/// Ditto
class UnconditionalNeuron: Neuron {

    /// Private constructor. Use HolyTidPrimitive.live_factory() instead.
    private this(immutable HolyUnconditionalNeuron holyUnconditionalNeuron) { super(holyUnconditionalNeuron);}

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /// No calculation of activation for an unconditional neuron.
    override HolyNeuron.Effect calculate_activation_and_get_effects() {
        return (cast(shared HolyNeuron)holy).select_effects(1);
    }

    /// Getter
    NormalizationType normalization() {
        return NormalizationType.NONE;
    }

    /// Getter
    float activation() const {
        return 1;
    }

}

/**
            Seed.
        Used for anonymous branching as apposed to the Breed. After a branch is started with seed there is no branch identifier
    left in the parent branch, so there is no way to communicate to it except waiting for a result concept or set of concepts,
    that the branch will send to the parent when it finishes.
*/
final class HolySeed: HolyUnconditionalNeuron {

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

    /**
        Create live wrapper for the holy static concept.
    */
    override Seed live_factory() const {
        return new Seed(cast(immutable)this);
    }
}

/// Ditto
final class Seed: UnconditionalNeuron {

    /// Private constructor. Use HolyTidPrimitive.live_factory() instead.
    private this(immutable HolySeed holySeed) { super(holySeed); }
}

/**
            Base for all weighing neurons.
*/
final class HolyWeightNeuron: HolyNeuron {

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

    /**
        Create live wrapper for the holy static concept.
    */
    override WeightNeuron live_factory() const {
        return new WeightNeuron(cast(immutable)this);
    }
}

/// Ditto
final class WeightNeuron: DynamicConcept, EsquashActivationIfc {

    /// Private constructor. Use HolyTidPrimitive.live_factory() instead.
    private this (immutable HolyWeightNeuron holyWeightNeuron) { super(holyWeightNeuron); }

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
