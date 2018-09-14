module cpt_concrete;
import std.format;

import global, tools;
import cpt_abstract;
import interfaces;

/**
            Static concept.
    Actually, it is immutable since all fields are immutable. Making the class or constructor immutable, however would introduce
    unneccessary complexity in the code, that uses this class.
*/
final class HolyStaticConcept: HolyConcept {

    immutable void* fp;                     /// function pointer to the static concept function
    immutable StatCallType _callType_;       /// call type of the static concept function

    /**
                Constructor
        Parameters:
            cid = Concept identifier. Must lay in the range of static concepts.
            fp = function pointer to the static concept function
            callType = call type of the static concept function
    */
    this(Cid cid, void* fp, StatCallType callType){
        super(cid);
        cast()flags |= HolyCptFlags.STATIC;
        cast()this.fp = cast(immutable)fp;
        cast()_callType_ = callType;
    }

    /// Create live wrapper for the holy static concept.
    override StaticConcept live_factory() const {
        return new StaticConcept(cast(immutable)this);
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--
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
                Constructor
        Parameters:
            cid = predefined concept identifier
    */
    this(Cid cid) { super(cid); }

    /// Create live wrapper for the holy static concept.
    override Action live_factory() const {
        return new Action(cast(immutable)this);
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /// Call static concept function
    void _do_() {
        _checkCid_!HolyStaticConcept(statActionCid_);
        assert((cast(HolyStaticConcept)_hm_[statActionCid_])._callType_ == StatCallType.p0Cal,
                format!"Static concept: %s( cid:%s) in HolyAction must have StatCallType none and it has %s."
                      (_nm_[statActionCid_], statActionCid_, (cast(HolyStaticConcept)_hm_[statActionCid_])._callType_));

        auto statCpt = (cast(HolyStaticConcept)_hm_[statActionCid_]);
        (cast(void function())statCpt.fp)();
    }

    /// Getter
    @property Cid _statActionCid_() {
        return statActionCid_;
    }

    /// Setter
    @property Cid _statActionCid_(Cid cid) {
        debug _checkCid_!HolyStaticConcept(cid);
        return statActionCid_ = cid;
    }

    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

    // Static action.
    private Cid statActionCid_;
}

/// Live.
final class Action: DynamicConcept {

    /// Private constructor. Use HolyTidPrimitive.live_factory() instead.
    private this(immutable HolyAction holyAction) { super(holyAction); }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /// Call static concept function
    void _do_() {
        (cast(shared HolyAction)holy)._do_;
    }
}

/**
            Peg premise.
    This type of premise don't store its activation in the field but gets dynamically.
*/
final class HolyPegPremise: HolyPremise {

    /**
                Constructor
        Parameters:
            cid = predefined concept identifier
    */
    this(Cid cid) { super(cid); }

    /// Create live wrapper for the holy static concept.
    override PegPremise live_factory() const {
        return new PegPremise(cast(immutable)this);
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--
}

/// Live.
final class PegPremise: Premise {
    this(immutable HolyPegPremise holyPegPremise) { super(holyPegPremise); }
}

/**
            Active premise.
    This type of premise don't store its activation in the field but gets dynamically.
*/
final class HolyStringPremise: HolyPremise {

    /**
                Constructor
        Parameters:
            cid = predefined concept identifier
    */
    this(Cid cid) { super(cid); }

    /// Create live wrapper for the holy static concept.
    override StringPremise live_factory() const {
        return new StringPremise(cast(immutable)this);
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--
}

/// Live.
final class StringPremise: Premise {
    this(immutable HolyStringPremise holyStringPremise) { super(holyStringPremise); }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /// Getter
    @property string str() {
        return str_;
    }

    /// Setter
    @property string str(string s) {
        return str_ = s;
    }

    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

    /// String
    private string str_;
}

/**
            Branch identifier.
        On one hand it is a container for TID. TID itself is stored in the live part, since it is a changeable entity. On the
    other, it is a pointer to the seed of the branch. Its cid is stored in the holy part.

        This concept can be used to start new branch instead of the seed, if we want to have in the parent branch a handler
    to a child to send it messages. This concept will be that handler. After the new branch started, its tid will be put
    in the tid_ field of the live part.
*/
final class HolyBreed: HolyPremise {

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
        debug _checkCid_!HolySeed(seedCid);

        return seedCid_ = seedCid;
    }

    /// Adapter.
    @property Cid _seed_(CptDescriptor seedDesc) {
        debug _checkCid_!HolySeed(seedDesc.cid);

        return seedCid_ = seedDesc.cid;
    }

    /// Getter.
    @property Cid _parentBreed_() const {
        return parentBreedCid_;
    }

    /// Setter.
    @property Cid _parentBreed_(Cid parentBreedCid) {
        debug _checkCid_!HolyBreed(parentBreedCid);

        return parentBreedCid_ = parentBreedCid;
    }

    /// Adapter.
    @property Cid _parentBreed_(CptDescriptor parentBreedDesc) {
        debug _checkCid_!HolyBreed(parentBreedDesc.cid);

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

    /// Private constructor. Use HolyBreed.live_factory() instead.
    private this(immutable HolyBreed holyBreed) { super(holyBreed); }

    /// Getter.
    @property Tid _tid_() {
        return tid_;
    }

    /// Setter.
    @property Tid _tid_(Tid tid) {
        return tid_ = tid;
    }

    /// Getter.
    const(Cid) _seed_() const {
        return (cast(immutable HolyBreed)holy)._seed_;
    }

    /// Getter.
    const(Cid) _parentBreed_() const {
        return (cast(immutable HolyBreed)holy)._parentBreed_;
    }

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
                Constructor
        Parameters:
            cid = predefined concept identifier
    */
    this(Cid cid) { super(cid); }

    /// Create live wrapper for the holy static concept.
    override UnconditionalNeuron live_factory() const {
        return new UnconditionalNeuron(cast(immutable)this);
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
                Calculate activation based on premises or lots.
        Returns: activation value.
    */
    override float calculate_activation() {
        return 1;
    }

    /**
                Adding effects analogous to the holy neuron concept, except that the span is not needed in this case.
        Parameters:
            act = either Action or Action[] or Cid or Cid[]
            bran = either Neuron or Neuron[] or Cid or Cid[]
    */
    void add_effects(Ta, Tb)(Ta act, Tb bran) {
        super.add_effects(float.infinity, act, bran);
    }

    /**
            Append action cids analogous to holy neuron, except that span selection is not needed here.
        Parameters:
            actCids = array of cids of appended actions.
    */
    final void add_actions(Cid[] actCids) {
        if(_effects.length == 0)
            add_effects(actCids, null);
        else
            super.append_actions(float.infinity, actCids);
    }

    /// Adapter.
    final void add_actions(Cid actCid) {
        if(_effects.length == 0)
            add_effects(actCid, null);
        else
            super.append_actions(float.infinity, actCid);
    }

    /// Adapter.
    final void add_actions(CptDescriptor actDesc) {
        if(_effects.length == 0)
            add_effects(actDesc, null);
        else
            super.append_actions(float.infinity, actDesc);
    }

    /// Adapter.
    final void add_actions(CptDescriptor[] actDescs) {
        if(_effects.length == 0)
            add_effects(actDescs, null);
        else
            super.append_actions(float.infinity, actDescs);
    }

    /**
            Append branch cids analogous to holy neuron, except that span selection is not needed here.
        Parameters:
            branchCids = array of cids of appended branches.
    */
    final void add_branches(Cid[] branchCids) {
        if(_effects.length == 0)
            add_effects(branchCids, null);
        else
            super.append_branches(float.infinity, branchCids);
    }

    /// Adapter.
    final void add_branches(Cid branchCid) {
        if(_effects.length == 0)
            add_effects(branchCid, null);
        else
            super.append_branches(float.infinity, branchCid);
    }

    /// Adapter.
    final void add_branches(CptDescriptor branchDesc) {
        if(_effects.length == 0)
            add_effects(branchDesc, null);
        else
            super.append_branches(float.infinity, branchDesc);
    }

    /// Adapter.
    final void add_branches(CptDescriptor[] branchDescs) {
        if(_effects.length == 0)
            add_effects(branchDescs, null);
        else
            super.append_branches(float.infinity, branchDescs);
    }
}

/// Live.
class UnconditionalNeuron: Neuron, ActivationIfc {

    /// Private constructor. Use HolyTidPrimitive.live_factory() instead.
    private this(immutable HolyUnconditionalNeuron holyUnconditionalNeuron) { super(holyUnconditionalNeuron);}

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
                Calculate activation based on premises or lots.
        Returns: activation value
    */
    override float calculate_activation() {
        return (cast(shared HolyUnconditionalNeuron)holy).calculate_activation;
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
                Constructor
        Parameters:
            cid = predefined concept identifier
    */
    this(Cid cid) { super(cid); }

    /// Create live wrapper for the holy static concept.
    override Seed live_factory() const {
        return new Seed(cast(immutable)this);
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--
}

/// Live.
final class Seed: UnconditionalNeuron {

    /// Private constructor. Use HolyTidPrimitive.live_factory() instead.
    private this(immutable HolySeed holySeed) { super(holySeed); }
}

/**
            Base for neurons, that take its decisions by pure logic on premises, as opposed to weighing them.
*/
final class HolyAndNeuron: HolyLogicalNeuron {

    /**
                Constructor
        Parameters:
            cid = predefined concept identifier
    */
    this(Cid cid) { super(cid); }

    // Create live wrapper for the holy static concept.
    override AndNeuron live_factory() const {
        return new AndNeuron(cast(immutable)this);
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
                Calculate activation based on premises.
        Returns: activation value. Activation is 1 if all premises are active, else it is -1. If list of premises is empty,
                activation is 1.
    */
    override float calculate_activation() {
        float res = 1;
        foreach(pr; _premises) {
            assert(cast(ActivationIfc)_hm_[pr],
                    format!"Cid %s, ActivationIfs must be realised for %s"(pr, typeid(_hm_[pr])));
            if ((cast(ActivationIfc)_hm_[pr]).activation <= 0) {
                res = -1;
                break ;
            }
        }

        return res;
    }
}

/// Live.
final class AndNeuron: LogicalNeuron {

    /// Constructor
    this (immutable HolyAndNeuron holyAndNeuron) { super(holyAndNeuron); }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--
}

/**
            Base for all weighing neurons.
*/
final class HolyWeightNeuron: HolyNeuron {

    /**
                Constructor
        Parameters:
            cid = predefined concept identifier
    */
    this(Cid cid) {
        super(cid);
    }

    /// Create live wrapper for the holy static concept.
    override WeightNeuron live_factory() const {
        return new WeightNeuron(cast(immutable)this);
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
                Calculate activation based on premises or lots.
        Returns: activation value
    */
    override float calculate_activation() {
        assert(true, "Not realized yet");
        return float.nan;
    }
}

/// Live.
final class WeightNeuron: Neuron, EsquashActivationIfc {

    /// Private constructor. Use HolyTidPrimitive.live_factory() instead.
    private this (immutable HolyWeightNeuron holyWeightNeuron) { super(holyWeightNeuron); }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    // implementation of the interface
    mixin EsquashActivationImpl!WeightNeuron;

    /**
                Calculate activation based on premises or lots.
        Returns: activation value
    */
    override float calculate_activation() {
        return _activation = (cast(shared HolyWeightNeuron)holy).calculate_activation;
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
