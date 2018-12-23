package cpt.abs

import basemain.Cid

abstract class SpiritNeuron(cid: Cid): SpiritDynamicConcept(cid) {

}

abstract class Neuron(spNeuron: SpiritNeuron): DynamicConcept(spNeuron) {

}

abstract class SpiritLogicalNeuron(cid: Cid): SpiritNeuron(cid) {

}

abstract class LogicalNeuron(spLogicalNeuron: SpiritLogicalNeuron): Neuron(spLogicalNeuron) {

}
