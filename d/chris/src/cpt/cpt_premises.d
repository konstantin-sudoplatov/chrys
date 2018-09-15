module cpt_premises;
import std.stdio;
import std.format;

import global, tools;
import interfaces;
import cpt_abstract;

/**
            Branch identifier.
        On one hand it is a container for TID. TID itself is stored in the live part, since it is a changeable entity. On the
    other, it is a pointer to the seed of the branch. Its cid is stored in the holy part.

        This concept can be used to start new branch instead of the seed, if we want to have in the parent branch a handler
    to a child to send it messages. This concept will be that handler. After the new branch started, its tid will be put
    in the tid_ field of the live part.
*/
final class SpBreed: SpiritPremise {
import cpt_neurons: SpSeed;
    /**
                Constructor
        Parameters:
            cid = predefined concept identifier
    */
    this(Cid cid) {
        super(cid);
    }

    /// Create live wrapper for the holy static concept.
    override Breed live_factory() const {
        return new Breed(cast(immutable)this);
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /// Getter.
    @property Cid _seed_() const {
        return seedCid_;
    }

    /// Setter.
    @property Cid _seed_(Cid seedCid) {
        debug _checkCid_!SpSeed(seedCid);

        return seedCid_ = seedCid;
    }

    /// Adapter.
    @property Cid _seed_(CptDescriptor seedDesc) {
        debug _checkCid_!SpSeed(seedDesc.cid);

        return seedCid_ = seedDesc.cid;
    }

    /// Getter.
    @property Cid _parentBreed_() const {
        return parentBreedCid_;
    }

    /// Setter.
    @property Cid _parentBreed_(Cid parentBreedCid) {
        debug _checkCid_!SpBreed(parentBreedCid);

        return parentBreedCid_ = parentBreedCid;
    }

    /// Adapter.
    @property Cid _parentBreed_(CptDescriptor parentBreedDesc) {
        debug _checkCid_!SpBreed(parentBreedDesc.cid);

        return parentBreedCid_ = parentBreedDesc.cid;
    }

    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

    /// The seed of the branch.
    private Cid seedCid_;

    /// Breed of the parent. It is incarnated in the new caldron name space when spawning the caldron.
    /// If this field left 0, it's ok. That would be an anonymous caldron with no need for any ID, since there should be
    // no messages from other caldrons.
    private Cid parentBreedCid_;
}

/// Live.
final class Breed: Premise {
    import std.concurrency: Tid;

    /// The thread identifier.
    Tid _tid_;

    /// Private constructor. Use spiritual live_factory() instead.
    private this(immutable SpBreed spBreed) { super(spBreed); }

    /// Getter.
    const(Cid) _seed_() const {
        return (cast(immutable SpBreed)holy)._seed_;
    }

    /// Getter.
    const(Cid) _parentBreed_() const {
        return (cast(immutable SpBreed)holy)._parentBreed_;
    }
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
