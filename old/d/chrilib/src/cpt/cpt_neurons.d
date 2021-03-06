module cpt.cpt_neurons;
import std.stdio;
import std.string, std.typecons;

import proj_data, proj_funcs;

import cpt.cpt_types;
import cpt.abs.abs_concept, cpt.abs.abs_neuron;
import atn.atn_caldron;
import cpt.cpt_interfaces;
import chri_types;

/**
            An uncontitional neuron.
        It is a degenerate neuron, capable only of applying its effects without consulting any premises. Its activation is always 1.
 */
@(8) class SpActionNeuron: SpiritNeuron {

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
                Adding effects analogous to the spirit neuron concept, except that the span is not needed in this case.
        Parameters:
            act = either Action or Action[] or Cid or Cid[]
            bran = either Neuron or Neuron[] or Cid or Cid[]
    */
    void addEffs(Ta, Tb)(Ta act, Tb bran) {
        super.addEffs(float.infinity, act, bran);
    }

    /**
            Append action cids analogous to holy neuron, except that span selection is not needed here.
        Parameters:
            actCids = array of cids of appended actions.
    */
    final void addActs(Cid[] actCids) {
        if(_effects.length == 0)
            addEffs(actCids, null);
        else
            super.appendActs(float.infinity, actCids);
    }

    /// Adapter.
    final void addActs(Cid actCid) {
        if(_effects.length == 0)
            addEffs(actCid, null);
        else
            super.appendActs(float.infinity, actCid);
    }

    /// Adapter.
    final void addActs(DcpDsc actDesc) {
        if(_effects.length == 0)
            addEffs(actDesc, null);
        else
            super.appendActs(float.infinity, actDesc);
    }

    /// Adapter.
    final void addActs(DcpDsc[] actDescs) {
        if(_effects.length == 0)
            addEffs(actDescs, null);
        else
            super.appendActs(float.infinity, actDescs);
    }

    /**
            Append branch cids analogous to holy neuron, except that span selection is not needed here.
        Parameters:
            branchCids = array of cids of appended branches.
    */
    final void addBrans(Cid[] branchCids) {
        if(_effects.length == 0)
            addEffs(branchCids, null);
        else
            super.appendBrans(float.infinity, branchCids);
    }

    /// Adapter.
    final void addBrans(Cid branchCid) {
        if(_effects.length == 0)
            addEffs(branchCid, null);
        else
            super.appendBrans(float.infinity, branchCid);
    }

    /// Adapter.
    final void addBrans(DcpDsc branchDesc) {
        if(_effects.length == 0)
            addEffs(branchDesc, null);
        else
            super.appendBrans(float.infinity, branchDesc);
    }

    /// Adapter.
    final void addBrans(DcpDsc[] branchDescs) {
        if(_effects.length == 0)
            addEffs(branchDescs, null);
        else
            super.appendBrans(float.infinity, branchDescs);
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
            Base for neurons, that take its decisions by pure logic on premises, as opposed to weighing them.
*/
@(10) final class SpAndNeuron: SpiritLogicalNeuron {

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

    /// Allow using overloads along with virtuals.
    alias addEffs = SpiritNeuron.addEffs;

    /**
                Adding effects for only one span, the one, that is selected if activation > 0. If activation <= 0, then
        the cutoff would work out and the branch will wait.
        Parameters:
            act = either Action or Action[] or Cid or Cid[]
            bran = either Neuron or Neuron[] or Cid or Cid[]
    */
    void addEffs(Ta, Tb)(Ta act, Tb bran) {
        super.addEffs(float.infinity, act, bran);
    }
}

unittest {
    auto a = new SpAndNeuron(42);
    a.addEffs(1, [5_000_000, 5_000_001], [5_000_010, 5_000_011]);
    a.addEffs(10, [5_000_002, 5_000_003], [5_000_012, 5_000_013]);

    a.addPrem(5_000_0100);
    a.addPrem(5_000_0101);
    a.addPrem(5_000_0102);
    Serial ser = a.serialize;

    auto b = cast(SpAndNeuron)SpiritConcept.deserialize(ser.cid, ser.ver, ser.clid, ser.stable, ser.transient);
    assert(a.cid == b.cid && a.ver == b.ver && typeid(a) == typeid(b) && a.cutoff == b.cutoff);
    assert(a == b);
}

/// Live.
final class AndNeuron: LogicalNeuron {

    /// Constructor
    this (immutable SpAndNeuron spAndNeuron) { super(spAndNeuron); }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
                Calculate activation based on premises.
        Returns: activation value. Activation is 1 if all premises are active, else it is -1. If list of premises is empty,
                activation is 1.
    */
    override float calculate_activation(Caldron cald) {
        float res = 1;
        foreach(pr; (scast!(immutable SpiritLogicalNeuron)(spirit)).premises) {
            assert(cast(ActivationIfc)cald[pr.cid],
                    "Cid %s, ActivationIfs must be realised for %s".format(pr, typeid(cald[pr.cid])));
            if ((cast(ActivationIfc)cald[pr.cid]).activation <= 0 && pr.negation == No.negate
                                                    ||
                (cast(ActivationIfc)cald[pr.cid]).activation > 0 && pr.negation == Yes.negate)
            {
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
@(11) final class SpWeightNeuron: SpiritNeuron {

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
    a.addEffs(1, [5_000_000, 5_000_001], [5_000_010, 5_000_011]);
    a.addEffs(10, [5_000_002, 5_000_003], [5_000_012, 5_000_013]);

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
