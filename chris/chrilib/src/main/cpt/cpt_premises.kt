package cpt

import atn.Brad
import basemain.Cid
import chribase_thread.CuteThread
import cpt.abs.Premise
import cpt.abs.SpiritDynamicConcept
import cpt.abs.SpiritPremise

/**
 *      Metadata for a branch - the pod, brid, and seedCid. The branch object is guaranteed to be injected with its
 *  breed concept on the start. Likewise it is guaranteed to get a valid breed concept of the child branch, when it
 *  initiates one.
 */
class SpBreed(cid: Cid): SpiritPremise(cid) {

    /** The seedCid's concept cid */
    var seedCid: Cid = 0
        private set(value) { field = value}     // to disable setter for the outer world

    /** Cids of concepts, that are injected into the branch on its creation by parent. */
    var ins: IntArray? = null
        private set(v) {field = v}

    /** Cids of concepts, that are injected into the parent branch on deletion of the child. */
    var outs: IntArray? = null
        private set(v) {field = v}

    override fun toString(): String {
        var s = super.toString()
        s += "\n    seedCid = $seedCid"

        return s
    }

    override fun liveFactory() = Breed(this)

    /**
     *      Load concept.
     *  @param seed Cid of the seed concept
     *
     */
    fun load(seed: SpSeed, ins: Array<SpiritDynamicConcept>?, outs: Array<SpiritDynamicConcept>?) {
        this.seedCid = seed.cid
        this.ins = if(ins == null || ins.size == 0) null else IntArray(ins.size) {ins[it].cid}
        this.outs = if(outs == null || outs.size == 0) null else IntArray(outs.size) {outs[it].cid}
    }
}

/** Live */
class Breed internal constructor(spBreed: SpBreed): Premise(spBreed) {

    /** Branch identifier (pod, brid) */
    var brad: Brad? = null

    override fun clone(): Breed {
        val o = super.clone() as Breed
        o.brad = this.brad?.clone()

        return o
    }

    override fun toString(): String {
        var s = super.toString()
        s += "\nownBrad = $brad".replace("\n", "\n    ")

        return s
    }
}

/**
 *      Premise, representing a cute thread object (as a special case a pod object). Used, e.g. for communication with user.
 */
class SpCuteThreadPrem(cid: Cid): SpiritPremise(cid) {

    override fun liveFactory() = CuteThreadPrem(this)
}

/** Live. */
class CuteThreadPrem(spCuteThreadPrem: SpCuteThreadPrem): Premise(spCuteThreadPrem) {

    var thread: CuteThread? = null      // note: cloned shallowly. Apparently deep cloning is senseless here.
}

class SpPegPrem(cid: Cid): SpiritPremise(cid) {

    override fun liveFactory() = PegPrem(this)
}

class PegPrem internal constructor(spPegPrem: SpPegPrem): Premise(spPegPrem)

class SpStringPrem(cid: Cid): SpiritPremise(cid) {
    override fun liveFactory() = StringPrem(this)
}

class StringPrem(spStringPrem: SpStringPrem): Premise(spStringPrem) {

    var text: String = ""

    override fun toString(): String {
        var s = super.toString()
        s += "\n    text = $text"
        return s
    }
}