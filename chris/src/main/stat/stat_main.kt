package stat

import atn.Brad
import atn.Branch
import basemain.Cid
import chribase_thread.CuteThread
import cpt.*
import cpt.abs.DynamicConcept
import libmain.BranchSendsUserItsBrad
import libmain.TransportSingleConceptIbr
import libmain._pp_

/** Stat container */
object mainStat: StatModule() {

/**
 *      Send branch address to the user.
 */
object sendUserBranchBrad: FCid(19_223) {

    /**
     *  @param br current branch
     *  @param userThread_premCid Cid of the premise, containing the user thread object reference.
    */
    @Suppress
    override fun func(br: Branch, userThread_premCid: Cid) {
        val userThread = (br[userThread_premCid] as CuteThreadPrem).thread as CuteThread
        userThread.putInQueue(BranchSendsUserItsBrad(br.ownBrad.clone()))
    }
}

/**
 *      Copy one live concept to another
 */
object copyCpt0ToCpt1: F2Cid(43_137) {

    /**
     *  @param br current branch
     *  @param cpt0Cid Source concept.
     *  @param cpt1Cid Destination concept.
    */
    override fun func(br: Branch, cpt0Cid: Cid, cpt1Cid: Cid) {
        br[cpt0Cid].copy(br[cpt1Cid])
    }
}

object transportSingleConcept: F2Cid(72_493) {
    /**
     *  @param br current branch
     *  @param whereTo Cid of the destination branch breed
     *  @param load Cid of the concept to transport. The concept is cloned on sending.
     */
    override fun func(br: Branch, whereTo: Cid, load: Cid) {
        val destBrad = (br[whereTo] as Breed).brad as Brad
        _pp_.putInQueue(TransportSingleConceptIbr(destBrad, br[load].clone() as DynamicConcept))
    }

}

    object test4Stat: FLCid(4) {
        override fun func(br: Branch, vararg cptCids: Cid) {

        }
    }
}   //   24_107 93_270 27_383 40_288 26_161 92_115 86_381 92_962 42_567 37_654 56_252 52_663 46_910
