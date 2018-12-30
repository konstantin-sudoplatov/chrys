package stat

import atn.Branch
import basemain.Cid
import chribase_thread.CuteThread
import cpt.*
import libmain.BranchSendsUserItsBrad

/** Stat container */
object mainStat: StatModule() {

    object test1Stat: F(14_338) { override fun func(br: Branch) {
        println("in test1Stat()")
    }}

    /**
     *      Send branch address of the current branch to user.
     *  @param br current branch
     *  @param userThread_premCid Cid of the premise, containing the user thread object reference.
     */
    object sendUserBranchBrad: FCid(19_223) { override fun func(br: Branch, userThread_premCid: Cid) {
        val userThread = (br[userThread_premCid] as CuteThreadPrem).thread as CuteThread
        userThread.putInQueue(BranchSendsUserItsBrad(br.ownBrad))
    }}

    /**
     *
     */
    object test2Stat: F2Cid(27_585) { override fun func(br: Branch, cid0: Cid, cid1: Cid) {
        println("in test1Stat()")
    }}

    object test4Stat: FLCid(4) { override fun func(br: Branch, vararg cids: Cid) {
        println("in test1Stat()")
    }}
}   //    47_628 43_137 72_493 53_148 51_211 50_023 62_408 89_866 24_107
