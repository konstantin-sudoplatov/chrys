package crank.word_processing

import basemain.acts
import basemain.ins
import basemain.outs
import cpt.*
import crank.mnCr
import libmain.CrankGroup
import libmain.CrankModule
import stat.cmnSt
import stat.word_processing.pulSt

/**
 *      Parse user line cranks.
 */
object pulCr: CrankModule() {

/**
 *      Take a line of user input and split it into a queue of words and punctuation marks.
 */
object splitUl: CrankGroup {

    val breed = SpBreed(-1_596_525_606)
    val seed = SpSeed(-996_889_663)

    // Parsed queue of words and punctuation marks as the branch's output
    val userChain_strqprem = SpStringQueuePrem(942_431_920)

    // Parse user line into the user chain
    val splitUserLineIntoChain_act = SpA_2Cid(-397_711_011).
        load(pulSt.splitUserLineIntoChain, mnCr.ulread.userInputLine_strprem, userChain_strqprem)

    override fun crank() {

        breed.load(
            seed,
            ins(mnCr.ulread.userInputLine_strprem),
            outs(userChain_strqprem)
        )
        seed.load(
            acts(
                splitUserLineIntoChain_act,
                mnCr.cmn.finishBranch_act
            )
        )
    }
}

/**
 *      Take words from the user chain and put them into the word primitives and a dictionary. Those will eventually come
 *  into the data base.
 */
object storeWordsFromUserChain: CrankGroup {

    val breed = SpBreed(1_230_588_599)
    val seed = SpSeed(304_785_243)

    // Do actual storing
//    val do_act = SpA_Cid(-2_075_622_757).load(pulSt.adoptNewWordForms, )

    val branchFinished_pegprem = SpPegPrem(-310_425_497)
    val activateBranchFinishedPeg_act = SpA_Cid(1_742_532_149).load(cmnSt.activate, branchFinished_pegprem)

    override fun crank() {
        breed.load(
            seed,
            ins(splitUl.userChain_strqprem),
            outs(branchFinished_pegprem)
        )
        seed.load(
            acts(
                activateBranchFinishedPeg_act,
                mnCr.cmn.finishBranch_act
            )
        )
    }
}

}   //    1_756_690_313 298_222_796 485_556_822 -426_273_917 -103_273_421 -2_045_546_495