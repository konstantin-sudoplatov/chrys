package cpt

import atn.Branch
import basemain.Cid
import cpt.abs.*

/**
 *      Applies its acts stemCid and brans unconditionally, i.e. without consulting any premises.
 */
open class SpActionNeuron(cid: Cid): SpiritNeuron(cid) {
    override fun liveFactory(): ActionNeuron {
        return ActionNeuron(this)
    }

    /**
     *      Load the action neuron with the acts, brans and stem.
     */
    fun load(acts: Array<out SpiritAction>? = null, brans: Array<SpBreed>? = null, stem: SpiritNeuron? = null): SpActionNeuron {
        assert(_effects == null) {"load() must work only once."}
        val actCids = if(acts != null) IntArray(acts.size) { acts[it].cid } else null
        val branCids = if(brans != null) IntArray(brans.size, { brans[it].cid }) else null
        super.addEffect(Float.POSITIVE_INFINITY, actCids, branCids, stem?.cid?: 0)

        return this
    }

    override fun addEffect(upperBound: Float, actions: IntArray?, branches: IntArray?, stemCid: Cid) {
        throw IllegalStateException("Not applicable")
    }

    override fun addEff(upBound: Float, acts: Array<out SpiritAction>?, brans: Array<SpBreed>?, stem: SpiritNeuron?): SpiritNeuron {
        throw IllegalStateException("Not applicable")
    }

    /**
     *      Assign/reassign the stem.
     *  @param newStem
     */
    fun stem(newStem: SpiritNeuron) {
        assert(_effects != null && _effects!!.size == 1) {"This neuron action must be loaded by the time."}
        _effects!![0].stemCid = newStem.cid
    }

    init {
        disableCutoff()
    }
}

/** Live. */
open class ActionNeuron(spActionNeuron: SpActionNeuron): Neuron(spActionNeuron) {

    override fun calculateActivation(br: Branch): Float {
        return 1f
    }

    override fun normalization() = ActivationIfc.NormalizationType.NONE
}

/**
 *      An action neuron, used for initial setting up the branches.
 */
class SpSeed(cid: Cid): SpActionNeuron(cid) {
    override fun liveFactory(): Seed {
        return Seed(this)
    }
}

/** Live. */
class Seed(spSeed: SpSeed): ActionNeuron(spSeed)

class SpWeightNeuron(cid: Cid): SpiritNeuron(cid) {
    override fun liveFactory(): WeightNeuron {
        return WeightNeuron(this)
    }
}

class WeightNeuron(spWeightNeuron: SpWeightNeuron): Neuron(spWeightNeuron) {

    override fun calculateActivation(br: Branch): Float {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun normalization() = ActivationIfc.NormalizationType.ESQUASH
}

/**
 *      Premises comply boolean logic "And" (with respect to negations).
 */
class SpAndNeuron(cid: Cid): SpiritLogicalNeuron(cid) {
    override fun liveFactory(): AndNeuron {
        return AndNeuron(this)
    }
}

/** Live. */
class AndNeuron(spAndNeuron: SpAndNeuron): LogicalNeuron(spAndNeuron) {

    /**
            Calculate activation based on premises.
        Returns: activation value. Activation is 1 if all premises are active, else it is -1. If the list of premises is empty,
                activation is -1.
    */
    override fun calculateActivation(br: Branch): Float {

        val premises = (sp as SpiritLogicalNeuron).premises
        if (premises != null && !premises.isEmpty())
            for(prem in premises) {
                val premCpt = br[prem.premCid] as ActivationIfc
                if(premCpt.activation <= 0 && !prem.negated || premCpt.activation > 0 && prem.negated) {
                    anactivate()
                    return activation
                }
            }
        else {
            anactivate()
            return activation
        }

        activate()
        return activation
    }
}

/**
 *      Neuron, whose premises are checked one after another (in respect of negations) until an active one found. Then
 *  corresponding effect is chosen. The "corresponding" exactly means, that the first premise has activation 1, the second
 *  2 and so on. The matching effects has upper bounds 1, 2... respectively.
 *
 *      The cutoff is taken care for, meaning it would take effect if no active premises were found. Cutoff can be disabled
 *  by putting null as the first premise on the conceptual level or by calling disableCutoff() in code.
 */
class SpPickNeuron(cid: Cid): SpiritLogicalNeuron(cid) {
    override fun liveFactory(): PickNeuron {
        return PickNeuron(this)
    }
}

class PickNeuron(spPickNeuron: SpPickNeuron): LogicalNeuron(spPickNeuron) {
    override fun calculateActivation(br: Branch): Float {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
