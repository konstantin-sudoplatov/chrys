package stat

import atn.Branch
import basemain.Cid
import libmain.F
import libmain.F2Cid
import libmain.FCid
import libmain.FLCid

/** Stat container */
object mainStat: StatModule() {

    object test1Stat: F(1) { override fun func(br: Branch) {
        println("in test1Stat()")
    }}

    object test2Stat: FCid(2) { override fun func(br: Branch, cid: Cid) {
        println("in test1Stat()")
    }}

    object test3Stat: F2Cid(3) { override fun func(br: Branch, cid0: Cid, cid1: Cid) {
        println("in test1Stat()")
    }}

    object test4Stat: FLCid(4) { override fun func(br: Branch, vararg cids: Cid) {
        println("in test1Stat()")
    }}
}   // 14_338 27_585 19_223 47_628 43_137 72_493 53_148 51_211 50_023 62_408 89_866 24_107
