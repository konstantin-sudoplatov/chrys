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

object test1Stat: F(14_338) { override fun func(br: Branch) {
    println("in test1Stat()")
}}

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


//    /**
//     *      Send branch address of the current branch to user.
//     *
//     *  Note1: the container is filled in with its load - the string premise, cid of which is specified in the spirit part
//     *  of the container, in the get() function of the branch, when the live concepts are created.
//     *
//     *  Note2: this functor takes care of switchin activation for the container premise. On sending the activation of the sent
//     *      copy is set to 1 and activation of the remaining is set to -1. So on the receiver side it will mean that the request
//     *      is active and requires processing, and on our side request is fulfilled and deactivated. The string premise inside
//     *      the container will be anactivated on both sides.
//     */
//    object requestUserInputLine: F2Cid(47_628) {
//
//        /**
//         *  @param br current branch
//         *  @param containerCid This concept contains the userInputLine_strprem premise, which will be holding
//         *          actual user line.
//         */
//        override fun func(br: Branch, destBridCid: Cid, containerCid: Cid) {
//            val destBrad = (br[destBridCid] as Branch).ownBrad
//            val remainingContainer = (br[containerCid] as ConceptPrem)
//            (remainingContainer.load as StringPrem).anactivate()     // anactivate load
//            remainingContainer.anactivate()                         // anactivate remaining container
//            val outgoingContainer = remainingContainer.clone() as ConceptPrem
//            outgoingContainer.activate()                            // activate outgoing container
//            _pp_.putInQueue(TransportSingleConceptIbr(destBrad, outgoingContainer, br.ownBrad))
//        }
//    }

    object test4Stat: FLCid(4) {
        override fun func(br: Branch, vararg cptCids: Cid) {

        }
    }
}   //       53_148 51_211 50_023 62_408 89_866 24_107
