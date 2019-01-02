package cpt

import atn.Brad
import basemain.Cid
import chribase_thread.CuteThread
import cpt.abs.Concept
import cpt.abs.Premise
import cpt.abs.SpiritDynamicConcept
import cpt.abs.SpiritPremise
import java.util.*

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
     *  @param ins Cids of concepts, that must be injected into the created branch
     *  @param outs Cids of concepts, that are injected back to the parent branch on finishing of the child
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

    override fun copy(dest: Concept) {
        super.copy(dest)
        dest as Breed
        dest.brad = this.brad?.clone()
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
class CuteThreadPrem internal constructor(spCuteThreadPrem: SpCuteThreadPrem): Premise(spCuteThreadPrem) {

    var thread: CuteThread? = null      // note: cloned shallowly. Apparently deep cloning is senseless here.


    override fun copy(dest: Concept) {
        super.copy(dest)
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

    override fun copy(dest: Concept) {
        super.copy(dest)
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

    override fun copy(dest: Concept) {
        super.copy(dest)
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
        s += "\n    queue (size ${queue.size} = ["
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