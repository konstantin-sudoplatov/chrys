package stat

import atn.Branch
import basemain.Cid
import basemain.logit
import cpt.F
import cpt.FCid

object commonStat: StatModule() {

/**
 *          Set the Branch.breakPoint flag to let the debugger stop at the right moment.
 */
object setBreakPoint: F(50_055) {
    override fun func(br: Branch) {
        br.breakPoint = true
    }
}

/**
 *          Set debug level of branch, pod and pod pool to 1. Set up pod and pod pool filtering to the current branch.
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
 *          Reset debug level of branch, pod and pod pool to -1. Reset pod and pod pool filters.
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
 *          Log the toString() for a specified concept.
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

}   //     69_359 53_479 75_671 59_771 34_395 95_519 40_417 33_598