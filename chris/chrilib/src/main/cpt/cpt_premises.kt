package cpt

import basemain.Cid
import cpt.abs.Concept
import cpt.abs.Premise
import cpt.abs.SpiritPremise

class SpPegPrem(cid: Cid = 0): SpiritPremise(cid) {

    override fun live_factory(): Concept {
        return PegPrem(this)
    }
}

class PegPrem internal constructor(spiritPegPrem: SpPegPrem): Premise(spiritPegPrem)

class SpStringPrem(cid: Cid = 0): SpiritPremise(cid) {
    override fun live_factory(): Concept {
        return StringPrem(this)
    }
}

class StringPrem(spiritStringPrem: SpStringPrem): Premise(spiritStringPrem) {

    var text: String = ""

    override fun toString(): String {
        var s = super.toString()
        s += "\n    text = $text"
        return s
    }
}