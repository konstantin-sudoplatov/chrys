package stat

import atn.Brad
import atn.Branch
import basemain.Cid
import basemain.logit
import cpt.ActivationIfc
import cpt.BradPrem
import cpt.Breed
import cpt.SpBreed
import cpt.abs.*
import libmain.*

/**
 *      Common stats.
 */
object cmnSt: StatModule() {

/**
 *      Request stopping the current branch.
 *  All outs are packed into the ChildReportsParentItsTerminationIbr message and sent to the parent. To actually finish
 *  the branch we send request to the pod.
 */
object requestParentFinishBranch: F(34_395) {
    override fun func(br: Branch) {
        val breed = br[br.breedCid] as Breed
        val outs = (breed.sp as SpBreed).outs
        val outCpts = if
                    (outs != null)
                Array<DynamicConcept>(outs.size) { br[outs[it]] }
            else
                null

        val parentBrad = br.parentBrad
        assert(parentBrad != null) {"Ordinary branch ${br.branchName()} must have not null parent."}
        parentBrad!!.pod.putInQueue(ChildReportsParentItsTerminationIbr(parentBrad.brid, outCpts, br.ownBrad, br.breedCid))
        br.ownBrad.pod.putInQueue(BranchRequestsPodToTerminateItMsg(br.ownBrad.brid))
    }
}

 /**
 *      Set the Branch.breakPoint flag to let the debugger stop at the right moment.
 */
object setBreakPoint: F(50_055) {
    override fun func(br: Branch) {
        br.breakPoint = true
    }
}

/**
 *      Set debug level of branch, pod and pod pool to 1. Set up pod and pod pool filtering to the current branch.
 */
object debugOn: F(69_448) {
    override fun func(br: Branch) {
        br.dlv = 1
        val pod = br.ownBrad.pod
        pod.dlv = 1
        pod.dBranchFilter = br.ownBrad.brid
//        _pp_.dlv = 1
//        _pp_.dPodFilter = pod.pid
    }
}

/**
 *      Reset debug level of branch, pod and pod pool to -1. Reset pod and pod pool filters.
 */
object debugOff: F(21_893) {
    override fun func(br: Branch) {
        br.dlv = -1
        val pod = br.ownBrad.pod
        pod.dlv = -1
        pod.dBranchFilter = -1
//        _pp_.dlv = -1
//        _pp_.dPodFilter = -1
    }
}

/**
 *      Log the toString() for a specified concept.
 */
object logConcept: FCid(22_788) {
    /**
     *  @param br current branch
     *  @param cptCid Concept to log_act
     */
    override fun func(br: Branch, cptCid: Cid) {
        logit(br[cptCid].toString())
    }
}

/**
 *      Activate concept.
 */
object activate: FCid(69_359) {
    /**
     *  @param br current branch
     *  @param cptCid Concept to activate
     */
    override fun func(br: Branch, cptCid: Cid) {
        (br[cptCid] as ActivationIfc).activate()
    }
}

/**
 *      Anactivate concept.
 */
object anactivate: FCid(53_479) {
    /**
     *  @param br current branch
     *  @param cptCid Concept to anactivate
     */
    override fun func(br: Branch, cptCid: Cid) {
        (br[cptCid] as ActivationIfc).anactivate()
    }
}

/**
 *      Activate concept in another branch.
 */
object activateRemotely: F2Cid(75_671) {
    /**
     *  @param br current branch
     *  @param destBradPrem Cid of the Brad or Breed premise for the destination branch
     *  @param cptCid Concept to activate
     */
    override fun func(br: Branch, destBradPrem: Cid, cptCid: Cid) {
        val destBrad = (br[destBradPrem] as BradPrem).brad as Brad
        destBrad.pod.putInQueue(ActivateRemotelyIbr(destBrad.brid, cptCid))
    }
}

/**
 *      Anactivate concept in another branch.
 */
object anactivateRemotely: F2Cid(59_771) {
    /**
     *  @param br current branch
     *  @param destBradPrem Cid of the Brad or Breed premise for the destination branch
     *  @param cptCid Concept to anactivate
     */
    override fun func(br: Branch, destBradPrem: Cid, cptCid: Cid) {
        val destBrad = (br[destBradPrem] as BradPrem).brad as Brad
        destBrad.pod.putInQueue(AnactivateRemotelyIbr(destBrad.brid, cptCid))
    }
}

/**
 *      Copy one live concept to another
 */
object copyPremise: F2Cid(43_137) {
    /**
     *  @param br current branch
     *  @param srcCid Source premise.
     *  @param destCid Destination premise.
    */
    override fun func(br: Branch, srcCid: Cid, destCid: Cid) {
        (br[srcCid] as Premise).copyLive(br[destCid] as Premise)
    }
}

/**
 *      Clone live concept and pass to given branch.
 */
object transportSingleConcept: F2Cid(72_493) {
    /**
     *  @param br current branch
     *  @param whereTo Cid of the destination branch brad premise
     *  @param load Cid of the concept to transport. The concept is cloned on sending.
     */
    override fun func(br: Branch, whereTo: Cid, load: Cid) {
        val destBrad = (br[whereTo] as BradPrem).brad as Brad
        destBrad.pod.putInQueue(TransportSingleConceptIbr(destBrad.brid, br[load].clone() as DynamicConcept))
    }
}

}   //    95_519 40_417 33_598