package cpt

import atn.Brad
import basemain.Cid
import chribase_thread.CuteThread
import cpt.abs.Premise
import cpt.abs.SpiritDynamicConcept
import cpt.abs.SpiritPremise
import db.SerializedConceptData
import libmain.arrayOfCidsNamed
import libmain.cidNamed
import java.util.*

/**
 *          Premise, whose live part contains address of a branch. Direct ancestor of the breed concept.
 */
open class SpBradPrem(cid: Cid): SpiritPremise(cid) {
    override fun liveFactory(): BradPrem {
        return BradPrem(this)
    }
}

/** Live */
open class BradPrem internal constructor(spBradPrem: SpBradPrem): Premise(spBradPrem) {

    /** Branch identifier (pod, brid) */
    var brad: Brad? = null

    override fun clone(): BradPrem {
        val o = super.clone() as BradPrem
        o.brad = this.brad?.clone()

        return o
    }

    override fun copyLive(dest: Premise) {
        super.copyLive(dest)
        dest as BradPrem
        dest.brad = this.brad?.clone()
    }

    override fun toString(): String {
        var s = super.toString()
        s += "\nbrad = $brad".replace("\n", "\n    ")
        return s
    }
}

/**
 *      Metadata for a branch - the pod, brid, and seedCid. The branch object is guaranteed to be injected with its
 *  breed concept on the start. Likewise it is guaranteed to get a valid breed concept of the child branch, when it
 *  initiates one.
 */
class SpBreed(cid: Cid): SpBradPrem(cid) {

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
        s += "\n    seedCid = ${cidNamed(seedCid)}"
        s += "\n${arrayOfCidsNamed("ins", ins)}".replace("\n", "\n    ")
        s += "\n${arrayOfCidsNamed("outs", outs)}".replace("\n", "\n    ")

        return s
    }

    override fun equals(other: Any?): Boolean {
        if(super.equals(other) == false)
            return false
        else {
            val o = other as SpBreed
            if(seedCid != o.seedCid) return false

            val insSize = ins?.size?:0
            if(insSize != o.ins?.size?:0) return false
            for(i in 0 until insSize)
                if(ins!![i] != o.ins!![i]) return false

            val outsSize = outs?.size?:0
            if(outsSize != o.outs?.size?:0) return false
            for(i in 0 until outsSize)
                if(outs!![i] != o.outs!![i]) return false

            return true
        }
    }

    override fun serialize(stableSuccSpace: Int, tranSuccSpace: Int): SerializedConceptData {
        val insSize = ins?.size?:0
        val outsSize = outs?.size?:0

        val sCD = super.serialize(
            stableSuccSpace + Cid.SIZE_BYTES + Int.SIZE_BYTES + Int.SIZE_BYTES*insSize  // seedCid + ints size + ints +
                    + Int.SIZE_BYTES + Int.SIZE_BYTES*outsSize,                                       // outs size + outs
            tranSuccSpace + 0
        )

        val stable = sCD.stable!!
        stable.putInt(seedCid)
        stable.putInt(insSize)
        for(i in 0 until insSize)
            stable.putInt(ins!![i])
        stable.putInt(outsSize)
        for(i in 0 until outsSize)
            stable.putInt(outs!![i])

        return sCD
    }

    override fun deserialize(sCD: SerializedConceptData) {
        super.deserialize(sCD)

        val stable = sCD.stable!!

        seedCid = stable.getInt()

        val insSize = stable.getInt()
        if(insSize == 0)
            ins = null
        else
            ins = IntArray(insSize) { stable.getInt() }

        val outsSize = stable.getInt()
        if(outsSize == 0)
            outs = null
        else
            outs = IntArray(outsSize) { stable.getInt() }
    }

    override fun liveFactory() = Breed(this)

    /**
     *      Load concept.
     *  @param seed Cid of the seed concept
     *  @param ins Cids of concepts, that must be injected into the created branch
     *  @param outs Cids of concepts, that are injected back to the parent branch on finishing of the child
     */
    fun load(seed: SpSeed, ins: Array<SpiritDynamicConcept>? = null, outs: Array<SpiritDynamicConcept>? = null): SpBreed {
        this.seedCid = seed.cid
        this.ins = if(ins == null || ins.size == 0) null else IntArray(ins.size) {ins[it].cid}
        this.outs = if(outs == null || outs.size == 0) null else IntArray(outs.size) {outs[it].cid}
        return this
    }
}

/** Live */
class Breed internal constructor(spBreed: SpBreed): BradPrem(spBreed)

/**
 *      Premise, representing a cute thread object (as a special case a pod object). Used, e.g. for communication with user.
 *  It is very similar to the brad premise, except that this one allows to address any cute thread, while the brad premise
 *  is specialized on the inter branch communication.
 */
class SpCuteThreadPrem(cid: Cid): SpiritPremise(cid) {
    override fun liveFactory() = CuteThreadPrem(this)
}

/** Live. */
class CuteThreadPrem internal constructor(spCuteThreadPrem: SpCuteThreadPrem): Premise(spCuteThreadPrem) {
    var thread: CuteThread? = null      // note: cloned shallowly. Apparently deep cloning is senseless here.

    override fun copyLive(dest: Premise) {
        super.copyLive(dest)
        dest as CuteThreadPrem
        dest.thread = thread
    }
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

    override fun copyLive(dest: Premise) {
        super.copyLive(dest)
        dest as StringPrem
        dest.text = text
    }

    override fun toString(): String {
        var s = super.toString()
        s += "\n    text = $text"
        return s
    }
}

/**
 *      Queue premise. It accumulates strings. For example, when messages from user come, they may be coming faster than
 *  they get processed, so they are buffered in the queue.
*/
class SpStringQueuePrem(cid: Cid): SpiritPremise(cid) {
    override fun liveFactory() = StringQueuePrem(this)
}

/** Live. */
class StringQueuePrem internal constructor(spStringQueuePrem: SpStringQueuePrem): Premise(spStringQueuePrem) {

    /** The queue */
    var queue = ArrayDeque<String>()

    override fun clone(): StringQueuePrem {
        val o = super.clone() as StringQueuePrem
        o.queue = queue.clone()
        return o
    }

    override fun copyLive(dest: Premise) {
        super.copyLive(dest)
        dest as StringQueuePrem
        dest.queue = queue.clone()
    }

    override fun toStr(): String {
        var s = super.toStr()
        s += ", queue.size = ${queue.size}"
        return s
    }

    override fun toString(): String {
        var s = super.toString()
        s += "\n    queue (size ${queue.size}) = ["
        for(ss in queue.take(5))
            s += "\n        $ss"
        s += "\n    ]"

        return s
    }
}

///**
// *      Premise, which live part holds a concept, may be another premise.
// */
//class SpConceptPrem(cid: Cid): SpiritPremise(cid) {
//
//    /** Cid of the carried concept. Currently 0 is not allowed(assert in the Branch.get()). In the future 0 could allow any dynamic concept, may be? */
//    var cptCid: Cid = 0
//
//    override fun toString(): String {
//        var s = super.toString()
//        s += "\n    cptCid = $cptCid"
//        return s
//    }
//
//    override fun liveFactory(): ConceptPrem = ConceptPrem(this)
//
//    /**
//     *      Set up the carried concept cid.
//     *  @param load Concept to carry.
//     */
//    fun load(load: SpiritDynamicConcept) {
//        cptCid = load.cid
//    }
//}
//
///** Live */
//class ConceptPrem(spConceptPrem: SpConceptPrem): Premise(spConceptPrem) {
//    var load: DynamicConcept? = null
//
//    override fun clone(): Concept {
//        var o = super.clone() as ConceptPrem
//        o.load = load?.clone() as DynamicConcept
//        return o
//    }
//
//    override fun toStr(): String {
//        var s = super.toStr()
//        s += ", load = ${load?.toStr()}"
//        return s
//    }
//
//    override fun toString(): String {
//        var s = super.toString()
//        s += "\nload$load".replace("\n", "\n    ")
//
//        return s
//    }
//}