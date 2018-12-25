package cpt

import atn.Brid
import basemain.Cid
import cpt.abs.Premise
import cpt.abs.SpiritPremise

/**
 *      Metadata for a branch - the pod, sockid, and seedCid. The branch object is guaranteed to be injected with its
 *  breed concept on the start. Likewise it is guaranteed to get a valid breed concept of the child branch, when it
 *  initiates one.
 */
class SpBreed(cid: Cid): SpiritPremise(cid) {

    /** The seedCid's concept cid */
    var seedCid: Cid = 0
        private set(value) { field = value}     // to disable setter for the outer world

    override fun toString(): String {
        var s = super.toString()
        s += "\n    seedCid = $seedCid"

        return s
    }

    override fun liveFactory(): Breed {
        return Breed(this)
    }

    /**
     *      Load concept.
     *  @param seed The seedCid concept
     */
    fun load(seed: SpSeed) {
        this.seedCid = seed.cid
    }
}

/** Live */
class Breed internal constructor(spBreed: SpBreed): Premise(spBreed) {

    /** Branch identifier (pod, sockid) */
    var brid: Brid? = null

    override fun toString(): String {
        var s = super.toString()
        s += "\nbrid = $brid".replace("\n", "\n    ")

        return s
    }
}

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