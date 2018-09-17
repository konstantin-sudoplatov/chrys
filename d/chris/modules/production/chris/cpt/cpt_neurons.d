module cpt_neurons;
import std.stdio;
import std.format;

import global, tools;
import interfaces;
import cpt_abstract;
import attn_circle_thread: Caldron;

/**
            An uncontitional neuron.
        It is a degenerate neuron, capable only of applying its effects without consulting any premises. Its activation is always 1.
 */
class SpActionNeuron: SpiritNeuron {

    /**
                Constructor
        Parameters:
            cid = predefined concept identifier
    */
    this(Cid cid) { super(cid); }

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
            Seed. It is a special case of the action neuron.
        It used for anonymous branching as apposed to the Breed. After a branch is started with seed there is no branch identifier
    left in the parent branch, so there is no way to communicate to it except waiting for a result concept or set of concepts,
    that the branch will send to the parent when it finishes.
*/
final class SpSeed: SpActionNeuron {

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

    /// Private constructor. Use HolyTidPrimitive.live_factory() instead.
    private this(immutable SpSeed spSeed) { super(spSeed); }
}

/**
            Base for neurons, that take its decisions by pure logic on premises, as opposed to weighing them.
*/
final class SpAndNeuron: SpiritLogicalNeuron {

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
        foreach(pr; (scast!(immutable SpiritLogicalNeuron)(sp)).premises) {
            assert(cast(ActivationIfc)cald[pr],
                    format!"Cid %s, ActivationIfs must be realised for %s"(pr, typeid(cald[pr])));
            if ((cast(ActivationIfc)cald[pr]).activation <= 0) {
                res = -1;
                break ;
            }
        }

        return res;
    }
}

/**
            Base for all weighing neurons.
*/
final class SpWeightNeuron: SpiritNeuron {

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
