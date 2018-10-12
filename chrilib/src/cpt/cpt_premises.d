module cpt.cpt_premises;
import std.format;

import project_params, tools;

import chri_shared;
import cpt.cpt_abstract;
import crank.crank_types: DcpDescriptor;

/**
            Branch identifier.
        On one hand it is a container for TID. TID itself is stored in the live part, since it is a changeable entity. On the
    other, it is a pointer to the seed of the branch. Its cid is stored in the holy part.

        This concept can be used to start new branch instead of the seed, if we want to have in the parent branch a handler
    to a child to send it messages. This concept will be that handler. After the new branch started, its tid will be put
    in the tid_ field of the live part.
*/
@(11) final class SpBreed: SpiritPremise {
    import cpt.cpt_neurons: SpSeed;

    /**
                Constructor
        Parameters:
            cid = predefined concept identifier
    */
    this(Cid cid) {
        super(cid, spClid!SpBreed);
    }

    /// Create live wrapper for the holy static concept.
    override Breed live_factory() const {
        return new Breed(cast(immutable)this);
    }

    override string toString() const {
        string s = super.toString;
        s ~= format!"\n    seedCid_ = %s(%,?s)"(_nm_[seedCid_], '_', seedCid_);
        return s;
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    void load(DcpDescriptor seedDsc) {
        checkCid!SpSeed(seedDsc.cid);
        seedCid_ = seedDsc.cid;
    }

    /// Getter.
    @property Cid seed() const {
        return seedCid_;
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
        return (cast(immutable SpBreed)spirit).seed;
    }
}

/**
        Tid premise.
*/
@(12) final class SpTidPremise: SpiritPremise {

    /// Constructor
    this(Cid cid) { super(cid, spClid!SpTidPremise); }

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

/**
            Peg premise.
*/
@(13) final class SpPegPremise: SpiritPremise {

    /// Constructor
    this(Cid cid) { super(cid, spClid!SpPegPremise); }

    /// Create live wrapper for the holy static concept.
    override PegPremise live_factory() const {
        return new PegPremise(cast(immutable)this);
    }
}

/// Live.
final class PegPremise: Premise {

    /// Private constructor. Use spiritual live_factory() instead.
    private this(immutable SpPegPremise holyPegPremise) { super(holyPegPremise); }
}

/**
            String premise.
    The string field is in the live part.
*/
@(14)final class SpStringPremise: SpiritPremise {

    /**
                Constructor
        Parameters:
            cid = predefined concept identifier
    */
    this(Cid cid) { super(cid, spClid!SpStringPremise); }

    /// Create live wrapper for the spirit static concept.
    override StringPremise live_factory() const {
        return new StringPremise(cast(immutable)this);
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--
}

/// Live.
final class StringPremise: Premise {

    /// The string
    string line;

    /// Private constructor. Use spiritual live_factory() instead.
    private this(immutable SpStringPremise spStringPremise) { super(spStringPremise); }

    override string toString() const {
        string s = super.toString;
        s ~= format!"\n    line = %s"(line);
        return s;
    }
}

/**
            Queue premise. This concept is capable of accumulating a queue of strings. For example, when messages from
    user come, they may be coming faster than they get processed. In that case such queue will help.
*/
@(15)final class SpStringQueuePremise: SpiritPremise {

    /// Constructor.
    this(Cid cid) { super(cid, spClid!SpStringQueuePremise); }

    /// Create live wrapper for the spirit static concept.
    override StringQueuePremise live_factory() const {return new StringQueuePremise(cast(immutable)this); }
}

/// Live.
final class StringQueuePremise: Premise {

    /// The queue
    Deque!string deque;
    alias deque this;

    /// Private constructor. Use spiritual live_factory() instead.
    private this(immutable SpStringQueuePremise spStrQuePrem) { super(spStrQuePrem); }

    override string toString() const {
        return format!"\n    deq = %s"(deque.toString);
    }
}
