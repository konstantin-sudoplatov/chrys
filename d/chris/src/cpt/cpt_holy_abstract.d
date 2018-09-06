/// The HolyConcept and its descendants. All holy classes are shared, and they inherit the shared attribute from the root class
/// HolyConcept.
module cpt_holy_abstract;
import std.conv;

import global, tools;
import interfaces;
import cpt_holy;
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

    //---***---***---***---***---***--- functions ---***---***---***---***---***--
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

    //---***---***---***---***---***--- functions ---***---***---***---***---***--
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

    //---***---***---***---***---***--- functions ---***---***---***---***---***--
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

    /**
                Get effects corresponding to given activation.
        Parameters:
            activation = activation value
        Returns:
            the Effect struct as the Voldemort value.
    */
    final auto select_effects(float activation) {
        assert(activation !is float.nan);

        // find and return the span
        foreach(eff; effects_) {
            if      // activation fits the span?
                    (activation <= eff.upperBound)
            return eff;
        }

        // not found, return null effects
        return cast(shared)Effect(float.infinity, null, null);
    }

    /**
                Add actions and branches for a new span of the activation values.
        Parameters:
            upperBound = higher boundary of the span, including. The lower boundary of the span is the upper bound of the
                         previous span, excluding, or -float.infinity, if it is the first span.
    */
    final void  add_effects(float upperBound, Cid[] actions, Cid[] branches)
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
                Call add_effects() with various variations types of parameters. Actual parameters are converted into arrays of cids.
        Parameters:
            uppewBound = float upper bound as usual
            act = either Action or Action[] or Cid or Cid[]
            bran = either Neuron or Neuron[] or Cid or Cid[]
    */
    void add_effects(Tu: float, Ta, Tb)(Tu upperBound, Ta act, Tb bran)
        if      // Ta is Action or Action[] or Cid or Cid[] and Tb is Neuron or Neuron[] or Cid or Cid[]?
                (is(Ta : shared HolyAction) || (is(Ta Tact : Tact[]) && is(Tact : shared HolyAction)) ||
                                is(Ta: Cid) || (is(Ta Tcid : Tcid[]) && is(Tcid : Cid))
                        &&
                is(Tb : shared HolyNeuron) || (is(Tb TTact : TTact[]) && is(TTact : shared HolyNeuron)) ||
                                is(Tb: Cid) || (is(Tb TTcid : TTcid[]) && is(TTcid : Cid)))
    {
        // convert Action or Action[] to Cid[]
        static if   // is Ta an array?
                (is(Ta T : T[]))
            static if // is it array of actions?
                    (is(T : shared HolyAction))
            {   //yes: convert it into array of cids
                Cid[] a;
                foreach (ac; act)
                    a ~= ac.cid;
            }
            else //no: it is array of Cids
            {
                Cid[] a = act;
            }
        else    //no: it is a single value
            static if // is it an action?
                    (is(Ta : shared HolyAction))
            {  //yes, it is a single action object: convert it to array of cids
                Cid[] a = [act.cid];
            }
            else
            {
                Cid[] a = [act];
            }

        // convert Neuron or Neuron[] to Cid[]
        static if   // is Tb an array?
                (is(Tb TT : TT[]))
            static if // is it array of neurons?
                    (is(TT : shared HolyNeuron))
            {   //yes: convert it into array of cids
                Cid[] b;
                foreach(br; bran)
                    b ~= br.cid;
            }
            else //no: it is an array of Cids
            {
                Cid[] b = bran;
            }
        else    //no: it is a single value
            static if // is it a neuron?
                    (is(Tb : shared HolyNeuron))
            {  //yes, it is a single neuron object: convert it to array of cids
                Cid[] b = [bran.cid];
            }
            else    //no, it is as single cid: convert it to array of cids
            {
                Cid[] b = [bran];
            }

        add_effects(upperBound, a, b);
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

    /// Element of the effects_ array.
    private static struct Effect {
        float upperBound;    /// lower boundary of the span (excluding)
        Cid[] actions;          /// actions, that will be taken before the branching
        Cid[] branches;         /// list of branches where the first branch is the next head of the current branch and the rest will be spawned
    }
}

unittest {
    import cpt_holy: HolyWeightNeuron;

    shared HolyNeuron nrn = new shared HolyWeightNeuron;
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

    // check various forms of add_effects()
    nrn.add_effects(5, [new shared HolyAction(MAX_STATIC_CID+10)],
            [new shared HolySeed(MAX_STATIC_CID+11)]);
    assert(nrn.select_effects(5).actions[0] == MAX_STATIC_CID+10);
    nrn.add_effects(10, new shared HolyAction(MAX_STATIC_CID+12),
            new shared HolySeed(MAX_STATIC_CID+13));
    assert(nrn.select_effects(10).branches[0] == MAX_STATIC_CID+13);
    nrn.add_effects(15, [new shared HolyAction(MAX_STATIC_CID+14),
            new shared HolyAction(MAX_STATIC_CID+15)],
            new shared HolySeed(MAX_STATIC_CID+16));
    assert(nrn.select_effects(14).actions[1] == MAX_STATIC_CID+15);
    nrn.add_effects(20, MAX_STATIC_CID+17, [MAX_STATIC_CID+18]);
    assert(nrn.select_effects(20).actions[0] == MAX_STATIC_CID+17);
    nrn.add_effects(25, [MAX_STATIC_CID+19], MAX_STATIC_CID+20);
    assert(nrn.select_effects(25).branches[0] == MAX_STATIC_CID+20);
    nrn.add_effects(30, MAX_STATIC_CID+21, MAX_STATIC_CID+22);
    assert(nrn.select_effects(30).branches[0] == MAX_STATIC_CID+22);


    // check cloning
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
