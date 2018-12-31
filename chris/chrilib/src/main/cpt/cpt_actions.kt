package cpt

/*
        The action concept is an interface, bridge between the world of dynamic concepts,
    that knows nothing about the code and operates only with cids and the static world, which is a set of functions.
    Essentially what acts do - they call to those functions with given parameters.
*/

import atn.Branch
import basemain.Cid
import cpt.abs.Action
import cpt.abs.SpiritAction
import cpt.abs.SpiritDynamicConcept
import libmain._sm_

/**
 *      Spirit action. It invokes functor of given static concept. The function of the functor has signature:
 *  fun(Branch): Unit
 *  @param cid Cid of the concept - real one or 0, if it is supposed to be assigned by the spirit map.
 */
class SpA(cid: Cid): SpiritAction(cid) {

    override fun liveFactory(): A {
        return A(this)
    }

    /**
     *      Run the static concept functor in the context of the given branch.
     *  @param br The branch object to run the functor in
     */
    override fun run(br: Branch) {
        (_sm_[_statCid] as F).func(br)
    }

    /**
     *      Load the action of type fun(Branch): Unit
     *  @param statCpt The concept functor.
     */
    fun load(statCpt: SpStaticConcept) {
        _statCid = statCpt.cid
    }
}

/**
 *      Live. To construct an instance do not use the constructor, use the liveFactory() of the spirit counterpart.
 *  @param spA - immutable spirit part
 */
class A(spA: SpA): Action(spA)

/**
 *      Spirit action. It invokes functor of given static concept. The function of the functor has signature:
 *  fun(Branch, Cid): Unit
 *  @param cid Cid of the concept - real one or 0, if it is supposed to be assigned by the spirit map.
 */
class SpA_Cid(cid: Cid): SpiritAction(cid) {

    override fun liveFactory(): A_Cid {
        return A_Cid(this)
    }

    /**
     *      Run the static concept functor in the context of the given branch.
     *  @param br The branch object to run the functor in
     */
    override fun run(br: Branch) {
        (_sm_[_statCid] as FCid).func(br, p1Cid_)
    }

    /**
     *      Load the action of type fun(Branch): Unit
     *  @param statCpt The concept functor.
     *  @param p1 first parameter
     */
    fun load(statCpt: SpStaticConcept, p1: SpiritDynamicConcept) {
        _statCid = statCpt.cid
        p1Cid_ = p1.cid
    }

    /** Cid of the first parameter. */
    private var p1Cid_: Cid = 0
}

/**
 *      Live. To construct an instance do not use the constructor, use the liveFactory() of the spirit counterpart.
 *  @param spA_Cid - immutable spirit part
 */
class A_Cid(spA_Cid: SpA_Cid): Action(spA_Cid)

/**
 *      Spirit action. It invokes functor of given static concept. The function of the functor has signature:
 *  fun(Branch, Cid, Cid): Unit
 *  @param cid Cid of the concept - real one or 0, if it is supposed to be assigned by the spirit map.
 */
class SpA_2Cid(cid: Cid): SpiritAction(cid) {

    override fun liveFactory(): A_2Cid {
        return A_2Cid(this)
    }

    /**
     *      Run the static concept functor in the context of the given branch.
     *  @param br The branch object to run the functor in
     */
    override fun run(br: Branch) {
        (_sm_[_statCid] as F2Cid).func(br, p1Cid_, p2Cid_)
    }

    /**
     *      Load the action of type fun(Branch): Unit
     *  @param statCpt The concept functor.
     *  @param p1 first parameter
     *  @param p2 second parameter
     */
    fun load(statCpt: SpStaticConcept, p1: SpiritDynamicConcept, p2: SpiritDynamicConcept) {
        _statCid = statCpt.cid
        p1Cid_ = p1.cid
        p2Cid_ = p2.cid
    }

    /** Cid of the first parameter. */
    private var p1Cid_: Cid = 0

    /** Cid of the second parameter. */
    private var p2Cid_: Cid = 0
}

/**
 *      Live. To construct an instance do not use the constructor, use the liveFactory() of the spirit counterpart.
 *  @param spA_2Cid - immutable spirit part
 */
class A_2Cid(spA_2Cid: SpA_2Cid): Action(spA_2Cid)

/**
 *      Spirit action. It invokes functor of given static concept. The function of the functor has signature:
 *  fun(Branch, vararg Cid): Unit
 *  @param cid Cid of the concept - real one or 0, if it is supposed to be assigned by the spirit map.
 */
class SpA_LCid(cid: Cid): SpiritAction(cid) {

    override fun liveFactory(): A_LCid {
        return A_LCid(this)
    }

    /**
     *      Run the static concept functor in the context of the given branch.
     *  @param br The branch object to run the functor in
     */
    override fun run(br: Branch) {
        (_sm_[_statCid] as FLCid).func(br, *pVar_ as IntArray)
    }

    /**
     *      Load the action of type fun(Branch): Unit
     *  @param statCpt The concept functor.
     *  @param pVar Variable number of concepts
     */
    fun load(statCpt: SpStaticConcept, vararg pVar: SpiritDynamicConcept) {
        _statCid = statCpt.cid
        pVar_ = IntArray(pVar.size) { pVar[it].cid }
    }

    /** Cid of the first parameter. */
    private var pVar_: IntArray? = null
}

/**
 *      Live. To construct an instance do not use the constructor, use the liveFactory() of the spirit counterpart.
 *  @param spA_LCid - immutable spirit part
 */
class A_LCid(spA_LCid: SpA_LCid): Action(spA_LCid)
