package cpt

import atn.Branch
import basemain.Cid
import basemain.logit
import cpt.abs.*

/**
 *          Unconditionally, i.e. without consulting any premises, applies its acts stemCid and brans.
 */
open class SpActionNeuron(cid: Cid): SpiritNeuron(cid) {
    override fun liveFactory(): ActionNeuron {
        return ActionNeuron(this)
    }

    /**
     *      Load the action neuron with the acts, brans and stem.
     */
    fun load(acts: Array<out SpiritAction>? = null, brans: Array<SpBreed>? = null, stem: SpiritNeuron? = null) {
        assert(_effects == null) {"load() must work only once."}
        super.addEffs(Float.POSITIVE_INFINITY, acts, brans, stem)
    }

    init {
        disableCutoff()
    }
}

open class ActionNeuron(spActionNeuron: SpActionNeuron): Neuron(spActionNeuron) {

    override fun calculateActivation(br: Branch): Float {
        return 1f
    }

    override fun normalization() = ActivationIfc.NormalizationType.NONE
}

class SpSeed(cid: Cid): SpActionNeuron(cid) {
    override fun liveFactory(): Seed {
        return Seed(this)
    }
}

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

class SpAndNeuron(cid: Cid): SpiritLogicalNeuron(cid) {
    override fun liveFactory(): AndNeuron {
        return AndNeuron(this)
    }
}

class AndNeuron(spAndNeuron: SpAndNeuron): LogicalNeuron(spAndNeuron) {

    /**
                Calculate activation based on premises.
        Returns: activation value. Activation is 1 if all premises are active, else it is -1. If list of premises is empty,
                activation is 1.
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
            logit("Warning: premises are not defined. Assuming contidions are met. \n$this")
        }

        activate()
        return activation
    }
}
