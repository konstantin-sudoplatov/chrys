module cpt.cpt_neurons;
import std.stdio;
import std.format;

import proj_data, proj_funcs;

import cpt.cpt_types;
import cpt.abs.abs_concept, cpt.abs.abs_neuron;
import atn.atn_circle_thread;
import cpt.cpt_interfaces;
import chri_types;

/**
            An uncontitional neuron.
        It is a degenerate neuron, capable only of applying its effects without consulting any premises. Its activation is always 1.
 */
@(7) class SpActionNeuron: SpiritNeuron {

    /**
                Constructor
        Parameters:
            cid = predefined concept identifier
    */
    this(Cid cid) {
        super(cid);
        this.disableCutoff;
    }

    /// Create live wrapper for the holy static concept.
    override ActionNeuron live_factory() const {
        return new ActionNeuron(cast(immutable)this);
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
                Adding effects analogous to the holy neuron concept, except that the span is not needed in this case.
        Parameters:
            act = either Action or Action[] or Cid or Cid[]
            bran = either Neuron or Neuron[] or Cid or Cid[]
    */
    void addEffects(Ta, Tb)(Ta act, Tb bran) {
        super.addEffects(float.infinity, act, bran);
    }

    /**
            Append action cids analogous to holy neuron, except that span selection is not needed here.
        Parameters:
            actCids = array of cids of appended actions.
    */
    final void addActions(Cid[] actCids) {
        if(_effects.length == 0)
            addEffects(actCids, null);
        else
            super.appendActions(float.infinity, actCids);
    }

    /// Adapter.
    final void addActions(Cid actCid) {
        if(_effects.length == 0)
            addEffects(actCid, null);
        else
            super.appendActions(float.infinity, actCid);
    }

    /// Adapter.
    final void addActions(DcpDescriptor actDesc) {
        if(_effects.length == 0)
            addEffects(actDesc, null);
        else
            super.appendActions(float.infinity, actDesc);
    }

    /// Adapter.
    final void addActions(DcpDescriptor[] actDescs) {
        if(_effects.length == 0)
            addEffects(actDescs, null);
        else
            super.appendActions(float.infinity, actDescs);
    }

    /**
            Append branch cids analogous to holy neuron, except that span selection is not needed here.
        Parameters:
            branchCids = array of cids of appended branches.
    */
    final void addBranches(Cid[] branchCids) {
        if(_effects.length == 0)
            addEffects(branchCids, null);
        else
            super.appendBranches(float.infinity, branchCids);
    }

    /// Adapter.
    final void addBranches(Cid branchCid) {
        if(_effects.length == 0)
            addEffects(branchCid, null);
        else
            super.appendBranches(float.infinity, branchCid);
    }

    /// Adapter.
    final void addBranches(DcpDescriptor branchDesc) {
        if(_effects.length == 0)
            addEffects(branchDesc, null);
        else
            super.appendBranches(float.infinity, branchDesc);
    }

    /// Adapter.
    final void addBranches(DcpDescriptor[] branchDescs) {
        if(_effects.length == 0)
            addEffects(branchDescs, null);
        else
            super.appendBranches(float.infinity, branchDescs);
    }
}

/// Live.
class ActionNeuron: Neuron, ActivationIfc {

    /// Private constructor. Use HolyTidPrimitive.live_factory() instead.
    private this(immutable SpActionNeuron spUnconditionalNeuron) { super(spUnconditionalNeuron);}

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
                Calculate activation based on premises or lots.
        Returns: activation value.
    */
    override float calculate_activation(Caldron cald) {
        return 1;
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
            Graft. It is a special case of the action neuron.
        It used much as the branching with Seed, but without actually branching and in the current name space. All grafting
        are executed sequentially and in the order they are presented in the effects.branches of the last processed neuron.
*/
//@(8) final class SpGraft: SpActionNeuron {
//
//    /// Constructor
//    this(Cid cid) { super(cid); }
//
//    /// Create live wrapper for the holy static concept.
//    override Graft live_factory() const {
//        return new Graft(cast(immutable)this);
//    }
//
//    //---***---***---***---***---***--- functions ---***---***---***---***---***--
//}
//
///// Live.
//final class Graft: ActionNeuron {
//
//    /// Private constructor. Use live_factory() instead.
//    private this(immutable SpGraft spGraft) { super(spGraft); }
//}

/**
            Seed. It is a special case of the action neuron.
        It used for anonymous branching as apposed to the Brid. After a branch is started with seed there is no branch identifier
    left in the parent branch, so there is no way to communicate to it except waiting for a result concept or set of concepts,
    that the branch will send to the parent when it finishes.
*/
@(8) final class SpSeed: SpActionNeuron {

    /// Constructor
    this(Cid cid) { super(cid); }

    /// Create live wrapper for the holy static concept.
    override Seed live_factory() const {
        return new Seed(cast(immutable)this);
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--
}

/// Live.
final class Seed: ActionNeuron {

    /// Private constructor. Use live_factory() instead.
    private this(immutable SpSeed spSeed) { super(spSeed); }
}

/**
            Base for neurons, that take its decisions by pure logic on premises, as opposed to weighing them.
*/
@(9) final class SpAndNeuron: SpiritLogicalNeuron {

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
}

unittest {
    auto a = new SpAndNeuron(42);
    a.addEffects(1, [5_000_000, 5_000_001], [5_000_010, 5_000_011]);
    a.addEffects(10, [5_000_002, 5_000_003], [5_000_012, 5_000_013]);

    a.addPremises([5_000_0100, 5_000_0101, 5_000_0102]);
    Serial ser = a.serialize;

    auto b = cast(SpAndNeuron)SpiritConcept.deserialize(ser.cid, ser.ver, ser.clid, ser.stable, ser.transient);
    assert(a.cid == b.cid && a.ver == b.ver && typeid(a) == typeid(b) && a.cutoff == b.cutoff);
    assert(a == b);
}

/// Live.
final class AndNeuron: LogicalNeuron {

    /// Constructor
    this (immutable SpAndNeuron holyAndNeuron) { super(holyAndNeuron); }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
                Calculate activation based on premises.
        Returns: activation value. Activation is 1 if all premises are active, else it is -1. If list of premises is empty,
                activation is 1.
    */
    override float calculate_activation(Caldron cald) {
        float res = 1;
        foreach(pr; (scast!(immutable SpiritLogicalNeuron)(spirit)).premises) {
            assert(cast(ActivationIfc)cald[pr],
                    format!"Cid %s, ActivationIfs must be realised for %s"(pr, typeid(cald[pr])));
            if ((cast(ActivationIfc)cald[pr]).activation <= 0) {
                res = -1;
                anactivate;
                goto FINISH;
            }
        }
        activate;

    FINISH:
        return res;
    }
}

/**
            Base for all weighing neurons.
*/
@(10) final class SpWeightNeuron: SpiritNeuron {

    /**
                Constructor
        Parameters:
            cid = predefined concept identifier
    */
    this(Cid cid) { super(cid); }

    /// Create live wrapper for the holy static concept.
    override WeightNeuron live_factory() const {
        return new WeightNeuron(cast(immutable)this);
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--
}

unittest {
    auto a = new SpWeightNeuron(42);
    a.addEffects(1, [5_000_000, 5_000_001], [5_000_010, 5_000_011]);
    a.addEffects(10, [5_000_002, 5_000_003], [5_000_012, 5_000_013]);

    Serial ser = a.serialize;

    auto b = cast(SpWeightNeuron)SpiritConcept.deserialize(ser.cid, ser.ver, ser.clid, ser.stable, ser.transient);
    assert(a.cid == b.cid && a.ver == b.ver && typeid(a) == typeid(b) && a.cutoff == b.cutoff);
    assert(a == b);
}

/// Live.
final class WeightNeuron: Neuron, EsquashActivationIfc {

    /// Private constructor. Use HolyTidPrimitive.live_factory() instead.
    private this (immutable SpWeightNeuron holyWeightNeuron) { super(holyWeightNeuron); }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    // implementation of the interface
    mixin EsquashActivationImpl!WeightNeuron;

    /**
                Calculate activation based on premises or lots.
        Returns: activation value
    */
    override float calculate_activation(Caldron cald) {
        assert(true, "Not realized yet");
        return float.nan;
    }
}
