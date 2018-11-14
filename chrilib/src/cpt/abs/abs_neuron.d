module cpt.abs.abs_neuron;
import std.stdio;
import std.string, std.typecons, std.math;

import proj_data, proj_funcs;

import cpt.cpt_types, cpt.abs.abs_concept;
import cpt.cpt_actions, cpt.cpt_neurons, cpt.cpt_premises;

import atn.atn_caldron;
import chri_types, chri_data;
import cpt.cpt_interfaces;

/**
            Base for all neurons.
*/
abstract class SpiritNeuron: SpiritDynamicConcept {

    /**
                Constructor
        Parameters:
            Used for concepts with predefined cids.
            cid = concept identifier
    */
    this(Cid cid) { super(cid); }

    /// Ditto.
    //override SpiritNeuron clone() const {
    //
    //    // Take shallow copy
    //    SpiritNeuron clon = cast(SpiritNeuron)super.clone;
    //
    //    // Make it deep.
    //    clon._effects = (cast(Effect[])this._effects).dup;
    //    foreach(int i, eff; clon._effects) {
    //        clon._effects[i].branches = _effects[i].branches.dup;
    //        clon._effects[i].actions = _effects[i].actions.dup;
    //    }
    //
    //    return clon;
    //}

    /// Serialize concept
    override Serial serialize() const {
        Serial res = Serial(cid, ver, _spReg_[typeid(this)]);

        // Calculate size of the stable buffer and allocate it
        Cind len = cutoff_.sizeof;                                // space for cutoff_
        len += Cind.sizeof;                                       // space for the number elements of _effects
        foreach(i; 0.._effects.length) {
            len += float.sizeof + 2*Cind.sizeof;            // space for upperBound and sizes of the action and branch arrays
            len += _effects[i].actions.length*Cid.sizeof;   // space for the action array
            len += _effects[i].branches.length*Cid.sizeof;  // space for the branch array
        }
        res.stable.reserve(len);        // get rid of unnesessary allocations

        // Fill in the cutoff_ and _effect.length
        static assert(is(typeof(cutoff_) == const float));
        res.stable ~= (cast(byte*)&cutoff_)[0..float.sizeof];                   // cutoff_
        len = cast(Cind)_effects.length;
        res.stable ~= (cast(byte*)&len)[0..Cind.sizeof];                        // _effects.length

        // serialize _effects
        foreach(i; 0..len) {
            static assert(is(typeof(_effects[i].upperBound) == const float));
            res.stable ~= (cast(byte*)&_effects[i].upperBound)[0..float.sizeof];         // _effects[i].upperBound

            // actions
            res.stable ~= serializeArray(_effects[i].actions);

            // branches
            res.stable ~= serializeArray(_effects[i].branches);
        }

        return res;
    }

    /// Equality test
    override bool opEquals(Object sc) const {
        if(!super.opEquals(sc)) return false;

        auto o = scast!(typeof(this))(sc);
        if(!(cutoff_.isNaN && o.cutoff_.isNaN) && cutoff_ != o.cutoff_) return false;
        if(_effects.length != o._effects.length) return false;
        foreach(i; 0.._effects.length){
            if(!(_effects[i].upperBound.isNaN && o._effects[i].upperBound.isNaN) &&
                    _effects[i].upperBound != o._effects[i].upperBound) return false;
            if(_effects[i].actions != o._effects[i].actions) return false;
            if(_effects[i].branches != o._effects[i].branches) return false;
        }
        return true;
    }

    override string toString() const {
        string s = super.toString;
        s ~= format!"\n    cutoff_ = %s"(cutoff_);
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
    final Effect selectEffs(float activation)
    in {
        import std.math: isNaN;
        if      // cutoff is off and there are effects?
                (!isNaN(cutoff_) && _effects.length)
            assert(_effects[0].upperBound > cutoff_,
                    format!"Cutoff overlaps first span: %s(cutoff) >= %s(upper bound of the first span)"
                    (cutoff_, _effects[0].upperBound));

        if(_effects.length)
            foreach(i, ef; _effects[1..$])
                assert(_effects[i+1].upperBound > _effects[i].upperBound, "Spans overlap.");
    }
    do {
        if      // is cutoff on and activation in the cutoff span? (if cutoff nan, then result is false)
                (activation <= cutoff_)
            return Effect(cutoff_, null, null);

        // find and return the span
        foreach(eff; _effects) {
            if      // activation fits the span?
                    (activation <= eff.upperBound)
            return eff;
        }

        // not found, return null effects
        return Effect(float.infinity, null, null);
    }

    /// Getter.
    final @property float cutoff() {
        return cutoff_;
    }

    /**
            Set new value for cutoff. Setting float.nan disables it.
        Parameters:
            cutoff = new value for cutoff
    */
    final @property void cutoff(float cutoff) {
        debug if(_effects.length >= 0) assert(cutoff < _effects[0].upperBound,
                format!"cutoff (%s) must be less then the upper bound of the first span (%s)"
                (cutoff, _effects[0].upperBound));
        cutoff_ = cutoff;
    }

    /// Disable cutoff.
    final void disableCutoff() {
        cutoff_ = float.nan;
    }

    /**
                Add actions and branches for a new span of the activation values. If cutoff is enabled, which is the default,
            the the number of spans is bigger by one, than defined in the effects array. The first dummy span of
        Effect(cutoff, null, null) ocupies the region [-float.infinity, cutoff]. You can disable cutoff in three ways:
        1. the function disableCutoff(); 2. the property neuron.cutoff = float.nan; 3. defining the first span with the
        upperBound <= cutoff.
        Parameters:
            upperBound = higher boundary of the span, including. The lower boundary of the span is the upper bound of the
                         previous span, excluding, or -float.infinity, if it is the first span.
    */
    final void addEffs(float upperBound, Cid[] actions, Cid[] branches)
    in {
        import std.math: isNaN;
        if      // is it not the first span assignment?
                (_effects.length > 0)
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
        _effects ~= Effect(upperBound, actions, branches);
        if      // is the firsts span overlapping the cutoff?
                (_effects[0].upperBound <= cutoff_)
            //yes: disable cutoff
            cutoff_ = float.nan;
    }

    /**
            Adaptor to the add_effects() function.
        Parameters:
            upperBound = the upper boundary of the span
            acts = Actions. It can be null, a single cid, an array of cids, single CptDescriptor, an array of CptDescriptors.
            brans = Branches. It can be null, a single cid, an array of cids, single CptDescriptor, an array of CptDescriptors.
    */
    void addEffs(Tu: float, Ta, Tb)(Tu upperBound, Ta acts, Tb brans)
    if
            ((isOf!(Ta, Cid) || isArrayOf!(Ta, Cid) || isOf!(Ta, DcpDsc) || isArrayOf!(Ta, DcpDsc))
                                                    &&
            (isOf!(Tb, Cid) || isArrayOf!(Tb, Cid) || isOf!(Tb, DcpDsc) || isArrayOf!(Tb, DcpDsc)))
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
                    (is(T == DcpDsc))
            {   //yes: convert it into array of cids
                Cid[] a;
                foreach (cd; acts) {
                    debug if(_maps_filled_)
                        assert(cast(shared SpA)_sm_[cd.cid],
                                format!"Cid: %s must be an action, and it is a %s."(cd.cid, cd.className));
                    a ~= cd.cid;
                }
            }
            else //no: it is array of Cids
            {
                debug if(_maps_filled_)
                    foreach (cid; acts) {
                        assert(cast(shared SpA)_sm_[cid],
                                format!"Cid: %s must be an action, and it is a %s."(cid, typeid(_sm_[cid])));
                    }
                Cid[] a = acts;
            }
        else static if // is it a concept descriptor?
                    (is(Ta == DcpDsc))
            {  //yes: convert it to array of cids
                debug if(_maps_filled_)
                    assert(cast(shared SpA)_sm_[acts.cid],
                            format!"Cid: %s must be an action, and it is a %s."(acts.cid, acts.className));
                Cid[] a = [acts.cid];
            }
            else//no: it is a cid; convert it to an array of cids
            {
                debug if(_maps_filled_)
                    assert(cast(shared SpA)_sm_[acts],
                            format!"Cid: %s must be an action, and it is a %s."(acts, typeid(_sm_[acts])));
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
                    (is(TT == DcpDsc))
            {   //yes: convert it into array of cids
                Cid[] b;
                foreach (cd; brans) {
                    debug if(_maps_filled_)
                        assert(cast(shared SpBreed)_sm_[cd.cid] || cast(shared SpGraft)_sm_[cd.cid] ||
                                cast(shared SpiritNeuron)_sm_[cd.cid],
                                "Cid: %s(%,?s) must be SpiritNeuron, or SpBreed, or SpGraft and it is a %s.".
                                format(cptName(cd.cid), '_', cd.cid, '_', cd.className));
                    b ~= cd.cid;
                }
            }
            else //no: it is array of Cids
            {
                debug if(_maps_filled_)
                    foreach (cid; brans) {
                        assert(cast(shared SpBreed)_sm_[cid] || cast(shared SpGraft)_sm_[cid] ||
                        cast(shared SpiritNeuron)_sm_[cid],
                                "Cid: %s(%,?s) must be SpiritNeuron, or SpBreed, or SpGraft and it is a %s.".
                                format(cptName(cid), '_', cid, '_', typeid(_sm_[cid])));
                    }
                Cid[] b = brans;
            }
        else    //no: it is a single value
            static if // is it a concept descriptor?
                    (is(Tb == DcpDsc))
            {  //yes: convert it to array of cids
                debug if(_maps_filled_)
                    assert(cast(shared SpBreed)_sm_[brans.cid] || cast(shared SpBreed)_sm_[brans.cid] ||
                    cast(shared SpiritNeuron)_sm_[brans.cid],
                            "Cid: %s(%,?s) must be SpiritNeuron, or SpBreed, or SpGraft and it is a %s.".
                            format(cptName(brans.cid), '_', brans.cid, '_', brans.className));
                Cid[] b = [brans.cid];
            }
            else//no: it is a cid; convert it an to array of cids
            {
                debug if(_maps_filled_)
                    assert(cast(shared SpBreed)_sm_[brans] || cast(shared SpiritNeuron)_sm_[brans],
                            "Cid: %s(%,?s) must be SpiritNeuron, or SpBreed, or SpGraft and it is a %s.".
                            format(cptNamp(brans), '_', brans, '_', typeid(_sm_[brans])));
                Cid[] b = [brans];
            }

        addEffs(upperBound, a, b);
    }

    /**
            Append action cids to an existing span.
        Parameters:
            activation = activation value to select span.
            actCids = array of cids of appended actions.
    */
    final void appendActs(float activation, Cid[] actCids)
    in {
        assert(_effects.length > 0, "First add then append.");
        foreach(act; actCids) {
            assert(act > MAX_STATIC_CID,
                    format!"The action cid %s is laying within the static concept range, which is not allowed."(act));
            assert(act in _sm_, format!"Cid %s must be present in the holy map"(act));
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
    final void appendActs(float activation, Cid actCid) {
        appendActs(activation, [actCid]);
    }

    /// Adapter.
    final void appendActs(float activation, DcpDsc actDesc) {
        appendActs(activation, [actDesc.cid]);
    }

    /// Adapter.
    final void appendActs(float activation, DcpDsc[] actDescs) {
        Cid[] actCids;
        foreach(ad; actDescs)
            actCids ~= ad.cid;
        appendActs(activation, actCids);
    }

    /**
            Append branch cids to an existing span.
        Parameters:
            activation = activation value to select span.
            branchCids = array of cids of appended branches.
    */
    final void appendBrans(float activation, Cid[] branchCids)
    in {
        assert(_effects.length > 0, "First add then append.");
        foreach(br; branchCids) {
            assert(br > MAX_STATIC_CID,
                    format!"The action cid %s is laying within the static concept range, which is not allowed."(br));
            assert(br in _sm_, format!"Cid %s must be present in the holy map"(br));
            assert(cast(Neuron)_sm_[br] || cast(Breed)_sm_[br], format!"Cid %s - must be the Seed or Breed concept"(br));
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
    final void appendBrans(float activation, Cid branchCid) {
        appendBrans(activation, [branchCid]);
    }

    /// Ditto.
    final void appendBrans(float activation, DcpDsc branchDesc) {
        appendBrans(activation, [branchDesc.cid]);
    }

    /// Ditto.
    final void appendBrans(float activation, DcpDsc[] branchDescs) {
        Cid[] brCids;
        foreach(ad; branchDescs)
            brCids ~= ad.cid;
        appendBrans(activation, brCids);
    }

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

        /// Equality test.
        bool opEquals()(auto ref const Effect e) const {
            return upperBound == e.upperBound && actions == e.actions && branches == e.branches;
        }
    }

    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //                                 Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$--- data ---$$$---$$$---$$$---$$$---$$$--

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

    //---$$$---$$$---$$$---$$$---$$$--- functions ---$$$---$$$---$$$---$$$---$$$---

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
        Cind ofs;     // offset in the stable buffer
        static assert(is(typeof(cutoff_) == float));
        cutoff_ = *cast(float*)&stable[ofs];                    // cutoff_
        ofs += cutoff_.sizeof;
        Cind len = *cast(Cind*)&stable[ofs];                // _effects.length
        ofs += Cind.sizeof;
        _effects.length = len;                          // allocate

        byte[] buf = cast(byte[])stable[ofs..$];
        foreach(i; 0..len) {
            static assert(is(typeof(_effects[0].upperBound) == float));
            _effects[i].upperBound = *cast(float*)&buf[0];         // _effects[i].upperBound

            // actions
            auto dserAct = deserializeArray!(Cid[])(buf[float.sizeof..$]);
            _effects[i].actions = dserAct.array;

            // branches
            auto dserBr = deserializeArray!(Cid[])(dserAct.restOfBuffer);
            _effects[i].branches = dserBr.array;
            buf = cast(byte[])dserBr.restOfBuffer;
        }

        return tuple!(const byte[], "stable", const byte[], "transient")(buf, transient);
    }

    /// If activation <= cutoff then result of the selectEffects() function is automatically Effect(cutoff, null, null),
    /// which means action stopAndWait and no branches. This allows to get rid of the first span from -infinity to 0, which
    /// is most oftenly used as antiactive span.
    private float cutoff_ = 0;
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
    SpiritNeuron.Effect calculate_activation_and_get_effects(Caldron cld)
    {
        return (cast(SpiritNeuron)spirit).selectEffs(calculate_activation(cld));
    }
}

/**
            Base for neurons, that take its decisions by pure logic on premises, as opposed to weighing them.
*/
abstract class SpiritLogicalNeuron: SpiritNeuron {

    /**
                Constructor
        Parameters:
            Used for concepts with predefined cids.
            cid = concept identifier
    */
    this(Cid cid) { super(cid); }

    /// Clone
    //override SpiritLogicalNeuron clone() const {
    //    SpiritLogicalNeuron cpt = cast(SpiritLogicalNeuron)super.clone;
    //    cpt._premises = this._premises.dup;      // deep copy of premises
    //
    //    return cpt;
    //}

    /// Serialize concept
    override Serial serialize() const {
        Serial res = super.serialize;

        // add _premises
        res.stable ~= serializeArray(_premises);

        return res;
    }

    /// Equality test
    override bool opEquals(Object sc) const {

        if(!super.opEquals(sc)) return false;
        auto o = scast!(typeof(this))(sc);
        return _premises == o._premises;
    }

    override string toString() const {
        auto s = super.toString;
        s ~= "\n    premises: [";
        foreach(pr; _premises)
            if(auto p = pr.cid in _nm_)
                s ~= format!"\n        %s(%,?s),"(*p, '_', pr);
            else
                s ~= format!"\n        noname(%,?s),"('_', pr);

        s ~= "\n    ]";
        return s;
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /// Getter.
    const(Premise[]) premises() const {
        return cast(const Premise[])_premises;
    }

    /// Add premise by cid.
    void addPrem(Flag!"negate" negation, Cid premise) {
        debug assert(!_maps_filled_ || cast(BinActivationIfc)_sm_[premise].live_factory,     // !_maps_filled_ in case of calls from unittest
                "Cid %s must implement BinActivationIfc. Check the class %s.".format(premise, typeid(_sm_[premise])));

        _premises ~= Premise(negation, premise);
    }

    /// Adapter.
    void addPrem(Flag!"negate" negation, DcpDsc premise) {
        addPrem(negation, premise.cid);
    }

    /// Adapter.
    void addPrem(Cid premise) {
        addPrem(No.negate, premise);
    }

    /// Adapter.
    void addPrem(DcpDsc premise) {
        addPrem(No.negate, premise.cid);
    }

    /// Structure of logical premises.
    static struct Premise {
        Cid cid;
        Flag!"negate" negation;

        this(Flag!"negate" negation, Cid premiseCid) {
            this.cid = premiseCid;
            this.negation = negation;
        }
    }

    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //                                 Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$--- data ---$$$---$$$---$$$---$$$---$$$--

    /// Array of premise cids.
    protected Premise[] _premises;

    //---$$$---$$$---$$$---$$$---$$$--- functions ---$$$---$$$---$$$---$$$---$$$---

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
        auto theRest = super._deserialize(stable, transient);

        auto dser = deserializeArray!(Premise[])(theRest.stable);
        _premises = dser.array;

        return tuple!(const byte[], "stable", const byte[], "transient")(dser.restOfBuffer, transient);
    }
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
