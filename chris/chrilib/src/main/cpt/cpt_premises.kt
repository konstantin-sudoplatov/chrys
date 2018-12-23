package cpt

import basemain.Cid
import cpt.abs.Concept
import cpt.abs.Premise
import cpt.abs.SpiritPremise

class SpBreed(cid: Cid): SpiritPremise(cid) {

    override fun liveFactory(): Breed {
        return Breed(this)
    }
}

class Breed internal constructor(spBreed: SpBreed): Premise(spBreed)

class SpPegPrem(cid: Cid): SpiritPremise(cid) {

    override fun liveFactory(): PegPrem {
        return PegPrem(this)
    }
}

class PegPrem internal constructor(spPegPrem: SpPegPrem): Premise(spPegPrem)

class SpStringPrem(cid: Cid): SpiritPremise(cid) {
    override fun liveFactory(): StringPrem {
        return StringPrem(this)
    }
}

class StringPrem(spStringPrem: SpStringPrem): Premise(spStringPrem) {

    var text: String = ""

    override fun toString(): String {
        var s = super.toString()
        s += "\n    text = $text"
        return s
    }
}