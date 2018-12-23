package cpt.abs

import basemain.Cid


abstract class SpiritPrimitive(cid: Cid): SpiritDynamicConcept(cid) {

}

abstract class Primitive(spPrimitive: SpiritPrimitive): DynamicConcept(spPrimitive) {

}
