/// The HolyConcept and its descendants. All holy classes are shared, and they inherit the shared attribute from the root class
/// HolyConcept.
module cpt_abstract;
import std.conv, std.format;

import global, tools;
import interfaces;
import cpt_pile, cpt_neurons, cpt_premises, cpt_actions;
import attn_circle_thread;

/// External runtime function, that creates new objects by their ClassInfo. No constructors are called. Very fast, much faster
/// than manually allocating the object in the heap as new buf[], as it is done in the emplace function (and more economical, no
/// waisting 16 bytes on the array descriptor). Used in the HolyConcept.clone() method.
private extern (C) Object _d_newclass (ClassInfo info);     //

/**
        Test for an array of a given type.
    Parameters:
        S = type to test
        T = type of array element
*/
enum bool isArrayOf(S, T) = is(S : T[]);
///
unittest {
    assert(isArrayOf!(int[], int));
    assert(!isArrayOf!(int[], long));
}

/**
        Test for a given type.
    Parameters:
        S = type to test
        T = type to test against
*/
enum bool isOf(S, T) = is(S == T);
///
unittest {
    assert(isOf!(shared int, shared int));
    assert(isOf!(int[], int[]));
}

/// Concept's attributes.
enum SpCptFlags: short {

    /// Static concept
    STATIC = 0x0001,

    /// Temporary dynamic concept. Heavily uses its live part, since it is thread local. Even its holy part is not designed
    /// to be stored in the DB, if only to collect the usage info.
    TEMP = 0x0002,

    /// Permanent dynamic concept. The holy part is stored in the DB and constitutes the knoledge base.
    PERM = 0x0004,
}

/**
            Base for all concepts.
    "Spiritual" means stable and storable as opposed to "Live" concepts, that are in a constant using and change and living only
    in memory. All live concepts contain reference to its holy counterpart. There can be many sin instances that corresponds
    to only one holy partner, which is considered immutable by them.

    "shared" attribute is inherrited by successors and cannot be changed.
*/
shared abstract class SpiritConcept {

    /// Concept identifier.
    immutable Cid cid = 0;

    /// Attributes of the concept.
    immutable SpCptFlags flags =  cast(SpCptFlags)0;

    /**
                Constructor
            Used for concepts with predefined cids.
        Parameters:
            cid = Concept identifier.
    */
    this(Cid cid) {
        this.cid = cid;
    }

    /**
                Clone an object and than make it a deep copy.
        Note, the runtime type of the object is used, not the compile (declared) type.
            Written by Burton Radons <burton-radons smocky.com>
            https://digitalmars.com/d/archives/digitalmars/D/learn/1625.html
            Tested against memory leaks in the garbage collecter both via copied object omission and omission of reference to other
        object in the its body.
        Returns: deep clone of itself
    */
    shared(SpiritConcept) _deep_copy_() const {

        void* copy = cast(void*)_d_newclass(this.classinfo);
        size_t size = this.classinfo.initializer.length;
        copy [8 .. size] = (cast(void *)this)[8 .. size];
        return cast(shared SpiritConcept)copy;
    }

    /**
        Create "live" wrapper for this object.
    */
    abstract Concept live_factory() const;

    /// Cannot override Object.toString with shared function, so live with it.
    string toString() const {
        import std.format: format;

        return format!"%s(%s): %,3?s"(_nm_.name(cid), typeid(this), '_', cid);
    }
}

/**
            Live wrapper for the HolyConcept.
        Every live concept has a reference to its holy counterpart.
    While the holy concepts contain stable data, and in fact all namespaces (caldrons) can count on them to be immutable,
    dispite the fact that the holy classes declared as just shared, their live mates operate with changeable data, like
    activation or prerequisites. While the holy concepts are shared by all caldrons, the live ones are thread local.

        We don't create live concepts directly through constructors, instead we use the live_factory() method of their
    holy partners.
*/
abstract class Concept {
    immutable SpiritConcept sp;

    /// Constructor
    this(immutable SpiritConcept spirit) {
        this.sp = spirit;
    }

    /**
            It is a partly deep copy. All fields of the object cloned deeply except the holy part. The holy is immutable
        for a caldron, no need to duplicate it.
    */
    Concept clone() const {

        // binary copy
        void* copy = cast(void*)_d_newclass(this.classinfo);
        size_t size = this.classinfo.initializer.length;
        copy [8 .. size] = (cast(void *)this)[8 .. size];
        Concept cpt = cast(Concept)copy;

        return cpt;
    }

    /// Overrided default Object.toString()
    override string toString() const {
        import std.format: format;
        import std.array: replace;

        string s = format!"%s(%s):"(_nm_.name(sp.cid), typeid(this));
        s ~= format!"\nsp = %s"((cast(shared)sp).toString).replace("\n", "\n    ");
        return s;
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /// Getter
    @property cid() const {
        return sp.cid;
    }
}

/**
            Base for all holy dynamic concepts.
*/
abstract class SpiritDynamicConcept: SpiritConcept {

    /**
                Constructor
        Used for concepts with predefined cids.
        Parameters:
            cid = Concept identifier. Can be a preassigned value or 0. If it is 0, then actual value is generated when you
                  add the concept to the holy map.
    */
    this(Cid cid) {
        super(cid);
        cast()flags |= SpCptFlags.PERM;       // all dynamic concepts are permanent until further notice
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--
}

/// Ditto
abstract class DynamicConcept: Concept {
    this(immutable SpiritDynamicConcept holyDynamicConcept) { super(holyDynamicConcept); }
}

/**
            Base for all spirit primitives. They store data. Examples of primitives are a string, a number, a list of strings (log),
    a map of strings/cid (vocabulary) and so on.
    All concrete descendants will have the "_pri" suffix.
*/
abstract class SpiritPrimitive: SpiritDynamicConcept {

    /**
                Constructor
        Parameters:
            Used for concepts with predefined cids.
            cid = concept identifier
    */
    this(Cid cid) { super(cid); }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--
}

/// Ditto
abstract class Primitive: DynamicConcept {
    this(immutable SpiritPrimitive holyPrimitive) { super(holyPrimitive); }
}

/**
            Base for all premises.
    All concrete descendants will have the "_pre" suffix.
*/
abstract class SpiritPremise: SpiritDynamicConcept {

    /// constructor
    this(Cid cid) { super(cid); }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--
}

/// Ditto
abstract class Premise: DynamicConcept, BinActivationIfc {
    this(immutable SpiritPremise holyPremise) { super(holyPremise); }

    override string toString() const {
        string s = super.toString;
        s ~= format!"\n    _activation = %s"(_activation);
        return s;
    }

    mixin BinActivationImpl!Premise;
}

/**
            Base for all neurons.
*/
abstract class SpiritNeuron: SpiritDynamicConcept {

    //---***---***---***---***---***--- types ---***---***---***---***---***---***

    /// Element of the effects_ array.
    static struct Effect {
        float upperBound;    /// lower boundary of the span (excluding)
        Cid[] actions;          /// actions, that will be taken before the branching
        Cid[] branches;         /// list of branches where the first branch is the next head of the current branch and the rest will be spawned

        this(float upBnd, Cid[] acts, Cid[] brs) {
            upperBound = upBnd;
            actions = acts;
            branches = brs;
        }
    }

    //---***---***---***---***---***--- data ---***---***---***---***---***--

    /**
                Constructor
        Parameters:
            Used for concepts with predefined cids.
            cid = concept identifier
    */
    this(Cid cid) { super(cid); }

    /// Ditto.
    override shared(SpiritNeuron) _deep_copy_() const {

        // Take shallow copy
        shared SpiritNeuron clon = cast(shared SpiritNeuron)super._deep_copy_;

        // Make it deep.
        clon._effects = (cast()this)._effects.dup;
        foreach(int i, eff; clon._effects) {
            clon._effects[i].branches = _effects[i].branches.dup;
            clon._effects[i].actions = _effects[i].actions.dup;
        }

        return clon;
    }

    override string toString() const {
        string s = super.toString;
        s ~= format!"\n    effects_: %s"(_effects);

        return s;
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
                Get effects corresponding to given activation.
        Parameters:
            activation = activation value
        Returns:
            the Effect struct as the Voldemort value.
    */
    final Effect selectEffects(float activation) {
        assert(activation !is float.nan);

        // find and return the span
        foreach(eff; _effects) {
            if      // activation fits the span?
                    (activation <= eff.upperBound)
            return cast()eff;
        }

        // not found, return null effects
        return Effect(float.infinity, null, null);
    }

    /**
                Add actions and branches for a new span of the activation values.
        Parameters:
            upperBound = higher boundary of the span, including. The lower boundary of the span is the upper bound of the
                         previous span, excluding, or -float.infinity, if it is the first span.
    */
    final void addEffects(float upperBound, Cid[] actions, Cid[] branches)
    in {
        if(_effects.length > 0)
            assert(upperBound > _effects[$-1].upperBound,
                    format!"The upper bound %s must be bigger than the upper bound of the previous span, which is %s"
                            (upperBound, _effects[$-1].upperBound));
        foreach(act; actions)
            assert(act > MAX_STATIC_CID,
                    format!"The action cid %s is laying within the static concept range, which is not allowed."(act));
        foreach(br; branches)
            assert(br > MAX_STATIC_CID,
                    format!"The action cid %s is laying within the static concept range, which is not allowed."(br));
    }
    do {
        _effects ~= cast(shared)Effect(upperBound, actions, branches);
    }

    /**
            Adaptor to the add_effects() function.
        Parameters:
            upperBound = the upper boundary of the span
            acts = Actions. It can be null, a single cid, an array of cids, single CptDescriptor, an array of CptDescriptors.
            brans = Branches. It can be null, a single cid, an array of cids, single CptDescriptor, an array of CptDescriptors.
    */
    void addEffects(Tu: float, Ta, Tb)(Tu upperBound, Ta acts, Tb brans)
    if
            ((isOf!(Ta, Cid) || isArrayOf!(Ta, Cid) || isOf!(Ta, CptDescriptor) || isArrayOf!(Ta, CptDescriptor))
                                                    &&
            (isOf!(Tb, Cid) || isArrayOf!(Tb, Cid) || isOf!(Tb, CptDescriptor) || isArrayOf!(Tb, CptDescriptor)))
    {
        // convert act to Cid[]
        static if      // is array of actions null?
                (is(Ta == typeof(null)))
        {   // leave the first parameter null
            Cid[] a;
        }
        else static if   // is Ta an array?
                (is(Ta T : T[]))
            static if // is it array of the concept descriptors?
                    (is(T == CptDescriptor))
            {   //yes: convert it into array of cids
                Cid[] a;
                foreach (cd; acts) {
                    debug if(_maps_filled_)
                        assert(cast(shared SpAction)_hm_[cd.cid],
                                format!"Cid: %s must be an action, and it is a %s."(cd.cid, cd.className));
                    a ~= cd.cid;
                }
            }
            else //no: it is array of Cids
            {
                debug if(_maps_filled_)
                    foreach (cid; acts) {
                        assert(cast(shared SpAction)_hm_[cid],
                                format!"Cid: %s must be an action, and it is a %s."(cid, typeid(_hm_[cid])));
                    }
                Cid[] a = acts;
            }
        else static if // is it a concept descriptor?
                    (is(Ta == CptDescriptor))
            {  //yes: convert it to array of cids
                debug if(_maps_filled_)
                    assert(cast(shared SpAction)_hm_[acts.cid],
                            format!"Cid: %s must be an action, and it is a %s."(acts.cid, acts.className));
                Cid[] a = [acts.cid];
            }
            else//no: it is a cid; convert it to an array of cids
            {
                debug if(_maps_filled_)
                    assert(cast(shared SpAction)_hm_[acts],
                            format!"Cid: %s must be an action, and it is a %s."(acts, typeid(_hm_[acts])));
                Cid[] a = [acts];
            }

        // convert bran to Cid[]
        static if      // is array of branches null?
                (is(Tb == typeof(null)))
        {   // leave the branch parameter null
            Cid[] b;
        }
        else static if   // is Tb an array?
                (is(Tb TT : TT[]))
            static if // is it array of the concept descriptors?
                    (is(TT == CptDescriptor))
            {   //yes: convert it into array of cids
                Cid[] b;
                foreach (cd; brans) {
                    debug if(_maps_filled_)
                        assert(cast(shared SpBreed)_hm_[cd.cid] || cast(shared SpiritNeuron)_hm_[cd.cid],
                                format!"Cid: %s must be HolyNeuron, including HolySeed or HolyBreed, and it is a %s."
                                        (cd.cid, cd.className));
                    b ~= cd.cid;
                }
            }
            else //no: it is array of Cids
            {
                debug if(_maps_filled_)
                    foreach (cid; brans) {
                        assert(cast(shared SpBreed)_hm_[cid] || cast(shared SpiritNeuron)_hm_[cid],
                                format!"Cid: %s must be HolyNeuron, including HolySeed or HolyBreed, and it is a %s"
                                        (cid, typeid(_hm_[cid])));
                    }
                Cid[] b = brans;
            }
        else    //no: it is a single value
            static if // is it a concept descriptor?
                    (is(Tb == CptDescriptor))
            {  //yes: convert it to array of cids
                debug if(_maps_filled_)
                    assert(cast(shared SpBreed)_hm_[brans.cid] || cast(shared SpiritNeuron)_hm_[brans.cid],
                            format!"Cid: %s must be HolyNeuron, including HolySeed or HolyBreed, and it is a %s."
                                    (brans.cid, brans.className));
                Cid[] b = [brans.cid];
            }
            else//no: it is a cid; convert it an to array of cids
            {
                debug if(_maps_filled_)
                    assert(cast(shared SpBreed)_hm_[brans] || cast(shared SpiritNeuron)_hm_[brans],
                            format!"Cid: %s must be HolyNeuron, including HolySeed or HolyBreed, and it is a %s."
                                    (brans, typeid(_hm_[brans])));
                Cid[] b = [brans];
            }

        addEffects(upperBound, a, b);
    }

    /**
            Append action cids to an existing span.
        Parameters:
            activation = activation value to select span.
            actCids = array of cids of appended actions.
    */
    final void appendActions(float activation, Cid[] actCids)
    in {
        assert(_effects.length > 0, "First add then append.");
        foreach(act; actCids) {
            assert(act > MAX_STATIC_CID,
                    format!"The action cid %s is laying within the static concept range, which is not allowed."(act));
            assert(act in _hm_, format!"Cid %s must be present in the holy map"(act));
            assert(cast(Seed)_hm_[act] || cast(Breed)_hm_[act],
                    format!"Cid %s - must be the Seed or Breed concept"(act));
        }
    }
    do {
        // find and append
        foreach(ref eff; _effects) {
            if      // activation fits the span?
                    (activation <= eff.upperBound)
            {
                eff.actions ~= actCids;
                break;
            }
        }
    }

    /// Adapter.
    final void appendActions(float activation, Cid actCid) {
        appendActions(activation, [actCid]);
    }

    /// Adapter.
    final void appendActions(float activation, CptDescriptor actDesc) {
        appendActions(activation, [actDesc.cid]);
    }

    /// Adapter.
    final void appendActions(float activation, CptDescriptor[] actDescs) {
        Cid[] actCids;
        foreach(ad; actDescs)
            actCids ~= ad.cid;
        appendActions(activation, actCids);
    }

    /**
            Append branch cids to an existing span.
        Parameters:
            activation = activation value to select span.
            branchCids = array of cids of appended branches.
    */
    final void appendBranches(float activation, Cid[] branchCids)
    in {
        assert(_effects.length > 0, "First add then append.");
        foreach(br; branchCids) {
            assert(br > MAX_STATIC_CID,
                    format!"The action cid %s is laying within the static concept range, which is not allowed."(br));
            assert(br in _hm_, format!"Cid %s must be present in the holy map"(br));
            assert(cast(Seed)_hm_[br] || cast(Breed)_hm_[br], format!"Cid %s - must be the Seed or Breed concept"(br));
        }
    }
    do {
        // find and append
        foreach(ref eff; _effects) {
            if      // activation fits the span?
                    (activation <= eff.upperBound)
            {
                eff.branches ~= branchCids;
                break;
            }
        }
    }

    /// Ditto.
    final void appendBranches(float activation, Cid branchCid) {
        appendBranches(activation, [branchCid]);
    }

    /// Ditto.
    final void appendBranches(float activation, CptDescriptor branchDesc) {
        appendBranches(activation, [branchDesc.cid]);
    }

    /// Ditto.
    final void appendBranches(float activation, CptDescriptor[] branchDescs) {
        Cid[] brCids;
        foreach(ad; branchDescs)
            brCids ~= ad.cid;
        appendBranches(activation, brCids);
    }

    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

    /// The Effect[] array represents effects (actions to be taken and branches to be set as the next step in the reasoning process),
    /// corresponding to spans of the neuron's activation value. Each span is represented by its higher boundary and goes from
    /// the higher boundary of the previous span excluding (from -float.infinity, including, if it is the first span)
    /// to its own higher boundary, including.
    /// Elements of the array are sorted in the ascending order of higer boundaries. For example, sequence 0, 1, 10 represents
    /// the following spans: [-float.infinity, 0]; ]1, 10]; [10, +float.infinity].
    /// If some span is not defined, in the previous example it is the span ]10, +float.infinity] it means actions "stop and wait"
    /// on the current branch.
    /// In all cases when the actions array is null or empty that means the action "stop and wait", and if the branches array
    /// is null or empty, it means no change of branch.
    protected Effect[] _effects;

    //---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
}

/// Ditto
abstract class Neuron: DynamicConcept, ActivationIfc {

    /// Constructor
    this(immutable SpiritNeuron holyNeuron) { super(holyNeuron); }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
                Calculate activation based on premises or lots.
        Returns: activation value. As a byproduct the _activation field is setup.
    */
    abstract float calculate_activation(Caldron);

    /**
                Calculate activation value and set up the activation_ variable.
        Returns: effects, corresponding calculated activation
    */

    /**
                Calculate activation value and set up the activation_ variable.
        Returns: effects, corresponding calculated activation
    */
    SpiritNeuron.Effect calculate_activation_and_get_effects(Caldron cald)
    {
        assert(cald is attn_circle_thread.caldron);
        return (cast(shared SpiritNeuron)sp).selectEffects(calculate_activation(cald));
    }
}

/**
            Base for neurons, that take its decisions by pure logic on premises, as opposed to weighing them.
*/
abstract class SpiritLogicalNeuron: SpiritNeuron, PremiseIfc {

    /**
                Constructor
        Parameters:
            Used for concepts with predefined cids.
            cid = concept identifier
    */
    this(Cid cid) { super(cid); }

    /// Clone
    override shared(SpiritLogicalNeuron) _deep_copy_() const {
        shared SpiritLogicalNeuron cpt = cast(shared SpiritLogicalNeuron)super._deep_copy_;
        cpt._premises = this._premises.dup;      // deep copy of premises

        return cpt;
    }

    override string toString() const {
        auto s = super.toString;
        s ~= "\n    premises: [";
        foreach(pr; _premises) {
            s ~= format!"\n        %s(%,?s)"(_nm_.name(pr), '_', pr);
        }
        s ~= "\n    ]";
        return s;
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    mixin PremiseImpl!SpiritLogicalNeuron;
}

/// Ditto
abstract class LogicalNeuron: Neuron, BinActivationIfc {

    /// Constructor
    this (immutable SpiritLogicalNeuron holyLogicalNeuron) { super(holyLogicalNeuron); }

    override string toString() const {
        auto s = super.toString;
        s ~= format!"\n    _activation = %s"(_activation);
        return s;
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    // implementation of the interface
    mixin BinActivationImpl!LogicalNeuron;
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
