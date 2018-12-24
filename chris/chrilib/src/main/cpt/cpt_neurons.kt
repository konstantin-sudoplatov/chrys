package cpt

import basemain.Cid
import cpt.abs.*

/**
 *          Unconditionally, i.e. without consulting any premises, applies its actions stem and branches.
 */
open class SpActionNeuron(cid: Cid): SpiritNeuron(cid) {
    override fun liveFactory(): ActionNeuron {
        return ActionNeuron(this)
    }
}

open class ActionNeuron(spActionNeuron: SpActionNeuron): Neuron(spActionNeuron) {

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

}

class SpAndNeuron(cid: Cid): SpiritLogicalNeuron(cid) {
    override fun liveFactory(): AndNeuron {
        return AndNeuron(this)
    }
}

class AndNeuron(spAndNeuron: SpAndNeuron): LogicalNeuron(spAndNeuron) {

}
