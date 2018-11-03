module cpt.cpt_premises;
import std.string, std.typecons;

import proj_data, proj_types, proj_funcs;

import chri_types, chri_data;
import cpt.cpt_types;
import cpt.abs.abs_concept, cpt.abs.abs_premise;

/**
            Branch identifier. On one hand it is a container for TID. TID itself is stored in the live part, since it is
    a changeable entity. On the other, it is a pointer to the seed of the branch. Its cid is stored in the holy part.

        This concept can be used to start new branch instead of the seed, if we want to have in the parent branch a handler
    to a child to send it messages. This concept will be that handler. After the new branch started, its tid will be put
    in the tid_ field of the live part.
*/
@(12) final class SpBreed: SpiritPremise {
    import cpt.cpt_neurons: SpSeed;

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

    /// Serialize concept
    override Serial serialize() const {
        Serial res = super.serialize;

        res.stable.length = Cid.sizeof;  // allocate
        *cast(Cid*)&res.stable[0] = seedCid_;

        return res;
    }

    /// Equality test
    override bool opEquals(Object sc) const {

        if(!super.opEquals(sc)) return false;
        auto o = scast!(typeof(this))(sc);
        return seedCid_ == o.seedCid_;
    }

    override string toString() const {
        string s = super.toString;
        s ~= "\n    seedCid_ = %s(%,?s)".format(_nm_[seedCid_], '_', seedCid_);
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

    /**
            Initialize concept from its serialized form.
        Parameters:
            stable = stable part of data
            transient = unstable part of data
        Returns: unconsumed slices of the stable and transient byte arrays.
    */
    protected override Tuple!(const byte[], "stable", const byte[], "transient") _deserialize(const byte[] stable,
            const byte[] transient)
    {
        seedCid_ = *cast(Cid*)&stable[0];

        return tuple!(const byte[], "stable", const byte[], "transient")(stable[Cid.sizeof..$], transient);
    }

    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

    /// The seed of the branch.
    private Cid seedCid_;
}

unittest {
    auto a = new SpBreed(42);
    a.ver = 5;
    a.seedCid_ = 43;

    Serial ser = a.serialize;
    auto b = cast(SpBreed)a.deserialize(ser.cid, ser.ver, ser.clid, ser.stable, ser.transient);
    assert(b.cid == 42 && b.ver == 5 && typeid(b) == typeid(SpBreed) && b.seedCid_ == 43);

    assert(a == b);
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
        s ~= "\n    tid = %s".format(cast()tid);
        return s;
    }

    /// Getter.
    Cid seed() const {
        return (cast(immutable SpBreed)spirit).seed;
    }
}

@(13) final class SpGraft: SpiritPremise {
    import cpt.cpt_neurons: SpSeed;

    /**
                Constructor
        Parameters:
            cid = predefined concept identifier
    */
    this(Cid cid) {
        super(cid);
    }

    /// Create live wrapper for the holy static concept.
    override Graft live_factory() const {
        return new Graft(cast(immutable)this);
    }

    /// Serialize concept
    override Serial serialize() const {
        Serial res = super.serialize;

        res.stable.length = Cid.sizeof;  // allocate
        *cast(Cid*)&res.stable[0] = seedCid_;

        return res;
    }

    /// Equality test
    override bool opEquals(Object sc) const {

        if(!super.opEquals(sc)) return false;
        auto o = scast!(typeof(this))(sc);
        return seedCid_ == o.seedCid_;
    }

    override string toString() const {
        string s = super.toString;
        s ~= "\n    seedCid_ = %s(%,?s)".format(_nm_[seedCid_], '_', seedCid_);
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

    /**
            Initialize concept from its serialized form.
        Parameters:
            stable = stable part of data
            transient = unstable part of data
        Returns: unconsumed slices of the stable and transient byte arrays.
    */
    protected override Tuple!(const byte[], "stable", const byte[], "transient") _deserialize(const byte[] stable,
            const byte[] transient)
    {
        seedCid_ = *cast(Cid*)&stable[0];

        return tuple!(const byte[], "stable", const byte[], "transient")(stable[Cid.sizeof..$], transient);
    }

    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

    /// The seed of the branch.
    private Cid seedCid_;
}

unittest {
    auto a = new SpGraft(42);
    a.ver = 5;
    a.seedCid_ = 43;

    Serial ser = a.serialize;
    auto b = cast(SpGraft)a.deserialize(ser.cid, ser.ver, ser.clid, ser.stable, ser.transient);
    assert(b.cid == 42 && b.ver == 5 && typeid(b) == typeid(SpGraft) && b.seedCid_ == 43);

    assert(a == b);
}

/// Live.
final class Graft: Premise {

    /// Private constructor. Use spiritual live_factory() instead.
    private this(immutable SpGraft spGraft) { super(spGraft); }

    /// Getter.
    const(Cid) seed() const {
        return (cast(immutable SpGraft)spirit).seed;
    }
}

/**
        Tid premise.
*/
@(14) final class SpTidPrem: SpiritPremise {

    /// Constructor
    this(Cid cid) { super(cid); }

    /// Create live wrapper for the holy static concept.
    override TidPrem live_factory() const {
        return new TidPrem(cast(immutable)this);
    }
}

/// Live.
final class TidPrem: Premise {
    import std.concurrency: Tid;

    /// The tid field
    Tid tid;

    /// Private constructor. Use spiritual live_factory() instead.
    private this(immutable SpTidPrem SpTidPremise) { super(SpTidPremise); }

    override string toString() const {
        string s = super.toString;
        s ~= "\n    tid = %s".format(cast()tid);
        return s;
    }
}

/**
            Peg premise.
*/
@(15) final class SpPegPrem: SpiritPremise {

    /// Constructor
    this(Cid cid) { super(cid); }

    /// Create live wrapper for the holy static concept.
    override PegPrem live_factory() const {
        return new PegPrem(cast(immutable)this);
    }
}

/// Live.
final class PegPrem: Premise {

    /// Private constructor. Use spiritual live_factory() instead.
    private this(immutable SpPegPrem spPegPrem) { super(spPegPrem); }
}

/**
            String premise.
    The string field is in the live part.
*/
@(16)final class SpStringPrem: SpiritPremise {

    /**
                Constructor
        Parameters:
            cid = predefined concept identifier
    */
    this(Cid cid) { super(cid); }

    /// Create live wrapper for the spirit static concept.
    override StringPrem live_factory() const {
        return new StringPrem(cast(immutable)this);
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--
}

/// Live.
final class StringPrem: Premise {

    /// The string
    string text;

    /// Private constructor. Use spiritual live_factory() instead.
    private this(immutable SpStringPrem spStringPremise) { super(spStringPremise); }

    override string toString() const {
        string s = super.toString;
        s ~= "\n    text = %s".format(text);
        return s;
    }
}

/**
            Queue premise. This concept is capable of accumulating a queue of strings. For example, when messages from
    user come, they may be coming faster than they get processed. In that case such queue will help.
*/
@(17)final class SpStringQueuePrem: SpiritPremise {

    /// Constructor.
    this(Cid cid) { super(cid); }

    /// Create live wrapper for the spirit static concept.
    override StringQueuePrem live_factory() const {return new StringQueuePrem(cast(immutable)this); }
}

/// Live.
final class StringQueuePrem: Premise {

    /// The queue
    Deque!string deque;
    alias deque this;

    /// Private constructor. Use spiritual live_factory() instead.
    private this(immutable SpStringQueuePrem spStrQuePrem) { super(spStrQuePrem); }

    override string toString() const {
        return "\n    deq = %s".format(deque.toString);
    }
}
