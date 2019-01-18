package crank.word_processing

import basemain.acts
import basemain.ins
import basemain.outs
import cpt.SpA_2Cid
import cpt.SpBreed
import cpt.SpSeed
import cpt.SpStringQueuePrem
import crank.mnCr
import libmain.CrankGroup
import libmain.CrankModule
import stat.word_processing.puiSt

/**
 *      Parse user line cranks.
 */
object pulCr: CrankModule() {

/**
 *      Take a line of user input and split it into a queue of words and punctuation marks.
 */
object splitUl: CrankGroup {

    val seed = SpSeed(-996_889_663)
    val breed = SpBreed(-1_596_525_606)

    // Parsed queue of words and punctuation marks as the branch's output
    val userChain_strqprem = SpStringQueuePrem(942_431_920)

    // Parse user line into the user chain
    val splitUserLineIntoChain_act = SpA_2Cid(-397_711_011).
        load(puiSt.splitUserLineIntoChain, mnCr.ulread.userInputLine_strprem, userChain_strqprem)

    override fun crank() {

        breed.load(
            seed,
            ins(mnCr.ulread.userInputLine_strprem),
            outs(userChain_strqprem)
        )
        seed.load(
            acts(
                splitUserLineIntoChain_act,
                mnCr.cmn.requestParentFinishThisBranch_act
            )
        )
    }
}   //  1_230_588_599 304_785_243 -103_273_421 -2_045_546_495
}