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

    override string toString() const {
        string s = super.toString;
        s ~= format!"\nseedCid_ = %s(%s)"
                (seedCid_, _nm_.name(seedCid_));
        return s;
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /// Getter.
    @property Cid seed() const {
        return seedCid_;
    }

    /// Setter.
    @property Cid seed(Cid seedCid) {
        debug checkCid!SpSeed(seedCid);

        return seedCid_ = seedCid;
    }

    /// Adapter.
    @property Cid seed(CptDescriptor seedDesc) {
        debug checkCid!SpSeed(seedDesc.cid);

        return seedCid_ = seedDesc.cid;
    }

    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

    /// The seed of the branch.
    private Cid seedCid_;
}

/// Live.
final class Breed: Premise {
    import std.concurrency: Tid;

    /// The thread identifier.
    Tid tid;

    /// Private constructor. Use spiritual live_factory() instead.
    private this(immutable SpBreed spBreed) { super(spBreed); }

    override string toString() const {
        string s = super.toString;
        s ~= format!"\n    tid = %s"(cast()tid);
        return s;
    }

    /// Getter.
    const(Cid) seed() const {
        return (cast(immutable SpBreed)sp).seed;
    }
}

/**
        Tid premise.
*/
final class SpTidPremise: SpiritPremise {

    /// Constructor
    this(Cid cid) { super(cid); }

    /// Create live wrapper for the holy static concept.
    override TidPremise live_factory() const {
        return new TidPremise(cast(immutable)this);
    }
}

/// Live.
final class TidPremise: Premise {
    import std.concurrency: Tid;

    /// The tid field
    Tid tid;

    /// Private constructor. Use spiritual live_factory() instead.
    private this(immutable SpTidPremise SpTidPremise) { super(SpTidPremise); }

    override string toString() const {
        string s = super.toString;
        s ~= format!"\n    tid = %s"(cast()tid);
        return s;
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
