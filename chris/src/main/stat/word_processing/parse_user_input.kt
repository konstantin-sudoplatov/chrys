package stat.word_processing

import atn.Branch
import basemain.Cid
import cpt.*
import cpt.abs.F2Cid
import libmain._sm_
import stat.StatModule

/**
 *      Parse user line stats.
 */
object pulSt: StatModule() {

/**
 *      Parse user line into sequence of words and punctuation marks.
 */
object splitUserLineIntoChain: F2Cid(40_288) {

    /**
     *  @param br
     *  @param uLineCid user line to parse string premise
     *  @param uChainCid string queue premise to parse the line to. It is activated in the end.
     */
    override fun func(br: Branch, uLineCid: Cid, uChainCid: Cid) {
        val uLine = br[uLineCid] as StringPrem
        val uChain = br[uChainCid] as StringQueuePrem
        val queue = uChain.queue
        uLine.text.split(" ").forEach { queue.add(it) }
        uChain.activate()
    }
}

/**
 *      Take new wordforms from a string queue premise and create primitives for them and add them to the wordform map.
 */
object adoptNewWordForms: F2Cid(61_969) {

    /**
     *  @param br
     *  @param wordformChainCid string queue of candidate wordforms.
     *  @param wordformMapCid wordform map.
     */
    override fun func(br: Branch, wordformChainCid: Cid, wordformMapCid: Cid) {
        val chain = br[wordformChainCid] as StringQueuePrem
        val map = br[wordformMapCid] as StringCidDict

        for(w in chain.queue)
            if(w !in map) {
println("here") //todo
                val newSpStringPrim = SpStringPrim(0)
                _sm_.add(newSpStringPrim)
                val cid = newSpStringPrim.cid
                (br[cid] as StringPrim).set(w)
                map[w] = cid
            }
    }
}

}   //  404 97_283 29_046 48_703 4_213 21_345 62_076 42_444 13_759 70_651 30_783