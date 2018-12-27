package cpt

import basemain.Cid
import cpt.abs.*

/**
 *          Unconditionally, i.e. without consulting any premises, applies its actions stemCid and branches.
 */
open class SpActionNeuron(cid: Cid): SpiritNeuron(cid) {
    override fun liveFactory(): ActionNeuron {
        return ActionNeuron(this)
    }
}

open class ActionNeuron(spActionNeuron: SpActionNeuron): Neuron(spActionNeuron) {

    override fun calculateActivation(): Float {
        return 1f
    }

    override fun normalization() = ActivationIfc.NormalizationType.NONE

}

class SpSeed(cid: Cid): SpActionNeuron(cid) {
    override fun liveFactory(): Seed {
        return Seed(this)
    }
}

class Seed(spSeed: SpSeed): ActionNeuron(spSeed) {

}

class SpWeightNeuron(cid: Cid): SpiritNeuron(cid) {
    override fun liveFactory(): WeightNeuron {
        return WeightNeuron(this)
    }
}

class WeightNeuron(spWeightNeuron: SpWeightNeuron): Neuron(spWeightNeuron) {

    override fun calculateActivation(): Float {
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
    override fun calculateActivation(): Float {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
