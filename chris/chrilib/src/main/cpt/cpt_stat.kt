package cpt

import atn.Branch
import basemain.Cid
import cpt.abs.SpiritConcept

open class SpStaticConcept(cid: Cid): SpiritConcept(cid)

/**
 *      Functor: fun(Branch): Unit
 */
abstract class F(cid: Cid): SpStaticConcept(cid) {
    abstract fun func(br: Branch): Unit
}


/**
 *      Functor: fun(Branch, Cid): Unit
 */
abstract class FCid(cid: Cid): SpStaticConcept(cid) {
    abstract fun func(br: Branch, cid: Cid): Unit
}

/**
 *      Functor: fun(Branch, Cid, Cid): Unit
 */
abstract class F2Cid(cid: Cid): SpStaticConcept(cid) {
    abstract fun func(br: Branch, cid0: Cid, cid1: Cid): Unit
}

/**
 *      Functor: fun(Branch, vararg Cid): Unit
 */
abstract class FLCid(cid: Cid): SpStaticConcept(cid) {
    abstract fun func(br: Branch, vararg cids: Cid): Unit
}
