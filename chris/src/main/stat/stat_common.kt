package stat

import atn.Branch
import basemain.Cid
import basemain.logit
import cpt.F
import cpt.FCid
import cpt.SpNumPrim
import libmain._pp_

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
 *          Set the branch debug level for the current branch to the specified value
 */
object setBranchDebugLevel: FCid(14_338) {
    /**
     *  @param br current branch
     *  @param level Cid of the numeric primitive concept
     */
    override fun func(br: Branch, level: Cid) {
        br.dlv = (br[level].sp as SpNumPrim).num.toInt()
    }
}

/**
 *          Reset the branch debug level to -1
 */
object resetBranchDebugLevel: F(50_023) {
    /**
     *  @param br current branch
     */
    override fun func(br: Branch) {
        br.dlv = -1
    }
}

/**
 *          Set the pod debug level for the current pod to the specified value and set its branch filter to the current branch.
 */
object setPodDebugLevelAndFilter: FCid(53_148) {
    /**
     *  @param br current branch
     *  @param level Cid of the numeric primitive concept
     */
    override fun func(br: Branch, level: Cid) {
        val pod = br.ownBrad.pod
        val dbgLvl = (br[level].sp as SpNumPrim).num.toInt()
        pod.dlv = dbgLvl
        pod.dBranchFilter = br.ownBrad.brid
    }
}

/**
 *          Reset the pod debug level and the branch filter to -1
 */
object resetPodDebugLevelAndFilter: F(51_211) {
    /**
     *  @param br current branch
     */
    override fun func(br: Branch) {
        val pod = br.ownBrad.pod
        pod.dlv = -1
        pod.dBranchFilter = -1
    }
}

/**
 *          Set the pod pool debug level to the specified value and set its pod filter to the current pod.
 */
object setPodpoolDebugLevelAndFilter: FCid(62_408) {
    /**
     *  @param br current branch
     *  @param level Cid of the numeric primitive concept
     */
    override fun func(br: Branch, level: Cid) {
        val pod = br.ownBrad.pod
        val dbgLvl = (br[level].sp as SpNumPrim).num.toInt()
        _pp_.dlv = dbgLvl
        _pp_.dPodFilter = pod.pid
    }
}

/**
 *          Reset the pod pool debug level and the pod filter to -1
 */
object resetPodpoolDebugLevelAndFilter: F(89_866) {
    /**
     *  @param br current branch
     */
    override fun func(br: Branch) {
        _pp_.dlv = -1
        _pp_.dPodFilter = -1
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

}   //   69_448 21_893 69_359 53_479 75_671 59_771 34_395 95_519 40_417 33_598