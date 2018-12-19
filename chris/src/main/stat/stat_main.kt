package stat

import atn.Branch
import basemain.Cid
import libmain.SF
import libmain.SFC
import libmain.SFLC
import libmain.StatModule

/** Stat container */
object statMain: StatModule() {

    object test1Stat: SF(1) { override fun func(br: Branch, vararg par: Cid) {
        println("in test1Stat()")
    }}

    object test2Stat: SFC(1) { override fun func(br: Branch, vararg par: Cid): Cid {
        println("in test1Stat()")
        return 0
    }}

    object test3Stat: SFLC(1) { override fun func(br: Branch, vararg par: Cid): List<Cid> {
        println("in test1Stat()")
        return listOf()
    }}
}
