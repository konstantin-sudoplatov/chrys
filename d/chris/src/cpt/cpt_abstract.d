/// The HolyConcept and its descendants. All holy classes are shared, and they inherit the shared attribute from the root class
/// HolyConcept.
module cpt_abstract;
import std.conv, std.format;

import global, tools;
import cpt_templates;
import interfaces;
import cpt_concrete;

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
enum HolyCptFlags: short {

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
    "Holy" means stable and storable as opposed to "Live" concepts, that are in a constant using and change and living only
    in memory. All live concepts contain reference to its holy counterpart. There can be many sin instances that corresponds
    to only one holy partner, which is considered immutable by them.

    "shared" attribute is inherrited by successors and cannot be changed.
*/
shared abstract class HolyConcept {

    /// Concept identifier.
    immutable Cid cid = 0;

    /// Attributes of the concept.
    immutable HolyCptFlags flags =  cast(HolyCptFlags)0;

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

    /**
        Create "live" wrapper for this object.
    */
    abstract Concept live_factory() const;

    /// Cannot override Object.toString with shared function, so live with it.
    string toString() const {
        import std.format: format;

        if      // is there a name to this concept?
                (auto name = cid in _nm_)
            return format!"cid: %s, name: %s"(cid, *name);
        else
            return format!"cid: %s"(cid);
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
    immutable HolyConcept holy;

    /// Constructor
    this(immutable HolyConcept holy) {
        this.holy = holy;
    }

    /// Getter
    @property cid() {
        return holy.cid;
    }

    /// Overrided default Object.toString()
    override string toString() const {
        import std.format: format;

        return (cast(shared)holy).toString;
    }
}

/**
            Base for all holy dynamic concepts.
*/
abstract class HolyDynamicConcept: HolyConcept {

    /**
                Constructor
        Used for concepts with predefined cids.
        Parameters:
            cid = Concept identifier. Can be a preassigned value or 0. If it is 0, then actual value is generated when you
                  add the concept to the holy map.
    */
    this(Cid cid) {
        super(cid);
        cast()flags |= HolyCptFlags.PERM;       // all dynamic concepts are permanent until further notice
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--
}

/// Ditto
abstract class DynamicConcept: Concept {
    this(immutable HolyDynamicConcept holyDynamicConcept) { super(holyDynamicConcept); }
}

/**
            Base for all holy primitives. They store data. Examples of primitives are a string, a number, a list of strings (log),
    a map of strings/cid (vocabulary) and so on.
    All concrete descendants will have the "_pri" suffix.
*/
abstract class HolyPrimitive: HolyDynamicConcept {

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
    this(immutable HolyPrimitive holyPrimitive) { super(holyPrimitive); }
}

/**
            Base for all premises.
    All concrete descendants will have the "_pre" suffix.
*/
abstract class HolyPremise: HolyDynamicConcept {

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
abstract class Premise: DynamicConcept {
    this(immutable HolyPremise holyPremise) { super(holyPremise); }
}

/**
            Base for all neurons.
*/
abstract class HolyNeuron: HolyDynamicConcept {

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

    /**
                Get effects corresponding to given activation.
        Parameters:
            activation = activation value
        Returns:
            the Effect struct as the Voldemort value.
    */
    final Effect select_effects(float activation) {
        assert(activation !is float.nan);

        // find and return the span
        foreach(eff; effects_) {
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
    final void add_effects(float upperBound, Cid[] actions, Cid[] branches)
    in {
        if(effects_.length > 0)
            assert(upperBound > effects_[$-1].upperBound, "The upper bound " ~ to!string(upperBound) ~
                    " must be bigger than the upper bound of the previous span, which is " ~
                    to!string(effects_[$-1].upperBound));
        foreach(act; actions)
            assert(act > MAX_STATIC_CID, "The action cid " ~ to!string(act) ~
                    " is laying within the static concept range, which is not allowed.");
        foreach(br; branches)
            assert(br > MAX_STATIC_CID, "The action cid " ~ to!string(br) ~
                    " is laying within the static concept range, which is not allowed.");
    }
    do {
        effects_ ~= cast(shared)Effect(upperBound, actions, branches);
    }

    /**
            Adaptor to the add_effects() function.
        Parameters:
            upperBound = the upper boundary of the span
            acts = Actions. It can be null, a single cid, an array of cids, single CptDescriptor, an array of CptDescriptors.
            brans = Branches. It can be null, a single cid, an array of cids, single CptDescriptor, an array of CptDescriptors.
    */
    void add_effects(Tu: float, Ta, Tb)(Tu upperBound, Ta acts, Tb brans)
    if ((isOf!(Ta, Cid) || isArrayOf!(Ta, Cid) || isOf!(Ta, CptDescriptor) || isArrayOf!(Ta, CptDescriptor))
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
                    debug if(_maps_fully_setup_)
                        assert(cast(shared HolyAction)_hm_[cd.cid], "Cid: " ~ to!string(cd.cid) ~
                        " must be an action, and it is a " ~ cd.className);
                    a ~= cd.cid;
                }
            }
            else //no: it is array of Cids
            {
                debug if(_maps_fully_setup_)
                    foreach (cid; acts) {
                        assert(cast(shared HolyAction)_hm_[cid], "Cid: " ~ to!string(cid) ~
                        " must be an action, and it is a " ~ typeid(_hm_[cid]));
                    }
                Cid[] a = acts;
            }
        else static if // is it a concept descriptor?
            (is(Ta == CptDescriptor))
            {  //yes: convert it to array of cids
                debug if(_maps_fully_setup_)
                    assert(cast(shared HolyAction)_hm_[acts.cid], "Cid: " ~ to!string(acts.cid) ~
                    " must be an action, and it is a " ~ acts.className);
                Cid[] a = [acts.cid];
            }
            else
            {
                debug if(_maps_fully_setup_)
                    assert(cast(shared HolyAction)_hm_[acts], "Cid: " ~ to!string(acts) ~
                    " must be an action, and it is a " ~ typeid(_hm_[acts]));
                Cid[] a = [acts];
            }

        // convert bran to Cid[]
        static if      // is array of branches null?
        (is(Tb == typeof(null)))
        {   // leave the branch parameter null
            Cid[] b;
        }
        static if   // is Tb an array?
        (is(Tb TT : TT[]))
            static if // is it array of the concept descriptors?
            (is(TT == CptDescriptor))
            {   //yes: convert it into array of cids
                Cid[] b;
                foreach (cd; brans) {
                    debug if(_maps_fully_setup_)
                        assert(cast(shared HolySeed)_hm_[cd.cid] || cast(shared HolyBreed)_hm_[cd.cid], "Cid: " ~
                        to!string(cd.cid) ~ " must be HolySeed or HolyBreed, and it is a " ~ cd.className);
                    b ~= cd.cid;
                }
            }
            else //no: it is array of Cids
            {
                debug if(_maps_fully_setup_)
                    foreach (cid; acts) {
                        assert(cast(shared HolySeed)_hm_[cid] || cast(shared HolyBreed)_hm_[cid],
                        "Cid: " ~ to!string(cid) ~ " must be HolySeed or HolyBreed, and it is a " ~
                        typeid(_hm_[cid]));
                    }
                Cid[] b = brans;
            }
        else    //no: it is a single value
            static if // is it a concept descriptor?
            (is(Ta == CptDescriptor))
            {  //yes: convert it to array of cids
                debug if(_maps_fully_setup_)
                    assert(cast(shared HolySeed)_hm_[acts.cid] || cast(shared HolyBreed)_hm_[acts.cid],
                    "Cid: " ~ to!string(acts.cid) ~ " must be HolySeed or HolyBreed, and it is a " ~
                    acts.className);
                Cid[] b = [brans.cid];
            }
            else
            {
                debug if(_maps_fully_setup_)
                    assert(cast(shared HolySeed)_hm_[acts] || cast(shared HolyBreed)_hm_[acts],
                    "Cid: " ~ to!string(acts) ~ " must be HolySeed or HolyBreed, and it is a " ~
                    typeid(_hm_[acts]));
                Cid[] b = [brans];
            }

        add_effects(upperBound, a, b);
    }

    ///
    unittest{
        import cpt_concrete: HolyWeightNeuron;
        shared HolyNeuron nrn = new shared HolyWeightNeuron(MIN_DYNAMIC_CID);

        // If no spans are defined, select_effects() will produce an initial span [-inF, +inF], actions: null, branches:null
        assert(nrn.select_effects(0).upperBound == float.infinity && nrn.select_effects(0).actions is null &&
        nrn.select_effects(0).branches is null);
        nrn.add_effects(0, [MAX_STATIC_CID+1, MAX_STATIC_CID+2], [MAX_STATIC_CID+3, MAX_STATIC_CID+4]);
        nrn.add_effects(1, [MAX_STATIC_CID+5, MAX_STATIC_CID+6], [MAX_STATIC_CID+7, MAX_STATIC_CID+8]);

        // check select_effects()
        assert(nrn.select_effects(-0.5).upperBound == 0);
        assert(nrn.select_effects(-float.infinity).branches[1] == MAX_STATIC_CID+4);
        assert(nrn.select_effects(0).upperBound == 0);
        assert(nrn.select_effects(0+float.epsilon).upperBound == 1);
        assert(nrn.select_effects(10).upperBound == float.infinity);
        assert(nrn.select_effects(float.infinity).branches is null);
    }

    /**
            Append action cids to an existing span.
        Parameters:
            activation = activation value to select span.
            actCids = array of cids of appended actions.
    */
    final void append_actions(float activation, Cid[] actCids)
    in {
        assert(effects_.length > 0, "First add then append.");
        foreach(act; actCids) {
            assert(act > MAX_STATIC_CID, "The action cid " ~ to!string(act) ~
            " is laying within the static concept range, which is not allowed.");
            assert(act in _hm_, "Cid " ~ to!string(act) ~ " must be present in the holy map");
            assert(cast(Seed)_hm_[act] || cast(Breed)_hm_[act],
                    "Cid " ~ to!string(act) ~ " - must be the Seed or Breed concept");
        }
    }
    do {
        // find and append
        foreach(ref eff; effects_) {
            if      // activation fits the span?
                    (activation <= eff.upperBound)
            {
                eff.actions ~= actCids;
                break;
            }
        }
    }

    /// Ditto.
    final void append_actions(float activation, Cid actCid) {
        append_actions(activation, [actCid]);
    }

    /// Ditto.
    final void append_actions(float activation, CptDescriptor actDesc) {
        append_actions(activation, [actDesc.cid]);
    }

    /// Ditto.
    final void append_actions(float activation, CptDescriptor[] actDescs) {
        Cid[] actCids;
        foreach(ad; actDescs)
            actCids ~= ad.cid;
        append_actions(activation, actCids);
    }

    /**
            Append branch cids to an existing span.
        Parameters:
            activation = activation value to select span.
            branchCids = array of cids of appended branches.
    */
    final void append_branches(float activation, Cid[] branchCids)
    in {
        assert(effects_.length > 0, "First add then append.");
        foreach(br; branchCids) {
            assert(br > MAX_STATIC_CID, "The action cid " ~ to!string(br) ~
            " is laying within the static concept range, which is not allowed.");
            assert(br in _hm_, "Cid " ~ to!string(br) ~ " must be present in the holy map");
            assert(cast(Seed)_hm_[br] || cast(Breed)_hm_[br], "Cid " ~ to!string(br) ~
                    " - must be the Seed or Breed concept");
        }
    }
    do {
        // find and append
        foreach(ref eff; effects_) {
            if      // activation fits the span?
                    (activation <= eff.upperBound)
            {
                eff.branches ~= branchCids;
                break;
            }
        }
    }

    /// Ditto.
    final void append_branches(float activation, Cid branchCid) {
        append_branches(activation, [branchCid]);
    }

    /// Ditto.
    final void append_branches(float activation, CptDescriptor branchDesc) {
        append_branches(activation, [branchDesc.cid]);
    }

    /// Ditto.
    final void append_branches(float activation, CptDescriptor[] branchDescs) {
        Cid[] brCids;
        foreach(ad; branchDescs)
            brCids ~= ad.cid;
        append_branches(activation, brCids);
    }

    override string toString() const {
        string s = super.toString;
        s ~= format!"\neffects_: %s"(effects_);

        return s;
    }

    //===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
    //
    //                                  Private
    //
    //===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@

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
    private Effect[] effects_;

    //---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
}

/// Ditto
abstract class Neuron: DynamicConcept, ActivationIfc {

    /// Constructor
    this(immutable HolyNeuron holyNeuron) { super(holyNeuron); }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
                Calculate activation value and set up the activation_ variable.
        Returns: effects, corresponding calculated activation as Voldemort type:
                 .upperBound as float; .actions as Cid[]; .branches as Cid[]
    */
    abstract HolyNeuron.Effect calculate_activation_and_get_effects();
}

/**
            Base for neurons, that take its decisions by pure logic on premises, as opposed to weighing them.
*/
abstract class HolyLogicalNeuron: HolyNeuron {

    /**
                Constructor
        Parameters:
            Used for concepts with predefined cids.
            cid = concept identifier
    */
    this(Cid cid) { super(cid); }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //                                 Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$--- data ---$$$---$$$---$$$---$$$---$$$--

    /// Array of premise cids.
    protected Cid[] premises_;
}

/// Ditto
abstract class LogicalNeuron: Neuron, BinActivationIfc {

    /// Constructor
    this (immutable HolyLogicalNeuron holyLogicalNeuron) { super(holyLogicalNeuron); }

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
