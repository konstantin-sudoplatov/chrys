package cpt.abs

import basemain.Cid
import cpt.ActivationIfc

abstract class SpiritNeuron(cid: Cid): SpiritDynamicConcept(cid) {

}

abstract class Neuron(spNeuron: SpiritNeuron): DynamicConcept(spNeuron), ActivationIfc {

    override var activation = -1F

}

abstract class SpiritLogicalNeuron(cid: Cid): SpiritNeuron(cid) {

}

abstract class LogicalNeuron(spLogicalNeuron: SpiritLogicalNeuron): Neuron(spLogicalNeuron) {

    override fun normalization() = ActivationIfc.NormalizationType.BIN
}

class Effects {

    private val _effects_ = mutableListOf<Effect>()
}

class Effect(
    val upperBound: Float,
    val stem: Cid = 0,
    val actions: IntArray? = null,
    val branches: IntArray? = null
)
