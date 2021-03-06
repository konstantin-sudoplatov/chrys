package cpt

/*
        The action concept is an interface, bridge between the world of dynamic concepts,
    that knows nothing about the code and operates only with cids and the static world, which is a set of functions.
    Essentially what acts do - they call to those functions with given parameters.
*/

import atn.Branch
import basemain.Cid
import cpt.abs.*
import db.SerializedConceptData
import libmain._sm_
import libmain.cidNamed
import libmain.namedCid

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
        assert(_statCid != 0 ) {"Action ${namedCid(cid)} is not initialized"}
        (_sm_[_statCid] as F).func(br)
    }

    /**
     *      Load the action of type fun(Branch): Unit
     *  @param statCpt The concept functor.
     */
    fun load(statCpt: SpStaticConcept): SpA {
        assert(_statCid == 0) {"${namedCid(cid)}: can be loaded only once"}
        _statCid = statCpt.cid
        return this
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

    override fun toStr(): String? {
        return super.toStr() + "\n    p1Cid_ = ${cidNamed(p1Cid_)}"
    }

    override fun liveFactory(): A_Cid {
        return A_Cid(this)
    }

    override fun equals(other: Any?): Boolean {
        if(super.equals(other) == false)
            return false
        else {
            val o = other as SpA_Cid
            return _statCid == o._statCid && p1Cid_ == o.p1Cid_
        }
    }

    override fun serialize(stableSuccSpace: Int, tranSuccSpace: Int): SerializedConceptData {
        val sCD = super.serialize(
            stableSuccSpace + Cid.SIZE_BYTES + Cid.SIZE_BYTES,
            tranSuccSpace + 0
        )

        val stable = sCD.stable!!
        stable.putInt(_statCid)
        stable.putInt(p1Cid_)

        return sCD
    }

    override fun deserialize(sCD: SerializedConceptData) {
        super.deserialize(sCD)

        val stable = sCD.stable!!
        _statCid = stable.getInt()
        p1Cid_ = stable.getInt()
    }

    /**
     *      Run the static concept functor in the context of the given branch.
     *  @param br The branch object to run the functor in
     */
    override fun run(br: Branch) {
        assert(_statCid != 0 && p1Cid_ != 0) {"Action ${namedCid(cid)} is not initialized"}
        (_sm_[_statCid] as FCid).func(br, p1Cid_)
    }

    /**
     *      Load the action of type fun(Branch): Unit
     *  @param statCpt The concept functor.
     *  @param p1 first parameter
     */
    fun load(statCpt: SpStaticConcept, p1: SpiritDynamicConcept): SpA_Cid {
        _statCid = statCpt.cid
        p1Cid_ = p1.cid
        return this
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

    override fun toStr(): String? {
        return super.toStr() +
            "\n    p1Cid_ = ${cidNamed(p1Cid_)}" +
            "\n    p2Cid_ = ${cidNamed(p2Cid_)}"
    }

    override fun liveFactory(): A_2Cid {
        return A_2Cid(this)
    }

    override fun equals(other: Any?): Boolean {
        if(super.equals(other) == false)
            return false
        else {
            val o = other as SpA_2Cid
            return _statCid == o._statCid && p1Cid_ == o.p1Cid_ && p2Cid_ == o.p2Cid_
        }
    }

    override fun serialize(stableSuccSpace: Int, tranSuccSpace: Int): SerializedConceptData {
        val sCD = super.serialize(
            stableSuccSpace + 3*Cid.SIZE_BYTES,
            tranSuccSpace + 0
        )

        val stable = sCD.stable!!
        stable.putInt(_statCid)
        stable.putInt(p1Cid_)
        stable.putInt(p2Cid_)

        return sCD
    }

    override fun deserialize(sCD: SerializedConceptData) {
        super.deserialize(sCD)

        val stable = sCD.stable!!
        _statCid = stable.getInt()
        p1Cid_ = stable.getInt()
        p2Cid_ = stable.getInt()
    }

    /**
     *      Run the static concept functor in the context of the given branch.
     *  @param br The branch object to run the functor in
     */
    override fun run(br: Branch) {
        assert(_statCid != 0 && p1Cid_ != 0 && p2Cid_ != 0) {"Action ${namedCid(cid)} is not initialized"}
        (_sm_[_statCid] as F2Cid).func(br, p1Cid_, p2Cid_)
    }

    /**
     *      Load the action of type fun(Branch): Unit
     *  @param statCpt The concept functor.
     *  @param p1 first parameter
     *  @param p2 second parameter
     */
    fun load(statCpt: SpStaticConcept, p1: SpiritDynamicConcept, p2: SpiritDynamicConcept): SpA_2Cid {
        assert(_statCid == 0) {"${namedCid(cid)}: can be loaded only once"}
        _statCid = statCpt.cid
        p1Cid_ = p1.cid
        p2Cid_ = p2.cid
        return this
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

    override fun toStr(): String? {
        var s = super.toStr()
        s += "\n    pVar = "
        if(pVar_ == null)
            s += "null"
        else {
            s += "\n    ["
            for(cid in pVar_!!)
                s += "\n        ${cidNamed(cid)}"
            s += "\n    ]"
        }
        return s
    }

    override fun liveFactory(): A_LCid {
        return A_LCid(this)
    }

    override fun equals(other: Any?): Boolean {
        if(super.equals(other) == false)
            return false
        else {
            val o = other as SpA_LCid
            if(_statCid != o._statCid) return false
            val pVarSize = pVar_?.size?:0
            if(pVarSize != o.pVar_?.size?:0) return false
            if(pVarSize != 0)
                for((i, cid) in pVar_!!.withIndex())
                    if(cid != o.pVar_!![i]) return false

            return true
        }
    }

    override fun serialize(stableSuccSpace: Int, tranSuccSpace: Int): SerializedConceptData {
        val pVarSize = pVar_?.size?:0
        val sCD = super.serialize(
            stableSuccSpace + 2*Cid.SIZE_BYTES + pVarSize*Cid.SIZE_BYTES,   // _statCid + data size + data
            tranSuccSpace + 0
        )

        val stable = sCD.stable!!
        stable.putInt(_statCid)
        stable.putInt(pVarSize)
        for(i in 0 until pVarSize)
            stable.putInt(pVar_!![i])
        return sCD
    }

    override fun deserialize(sCD: SerializedConceptData) {
        super.deserialize(sCD)

        val stable = sCD.stable!!
        _statCid = stable.getInt()
        val pVarSize = stable.getInt()
        if(pVarSize == 0)
            pVar_ = null
        else
            pVar_ = IntArray(pVarSize) { stable.getInt() }
    }

    /**
     *      Run the static concept functor in the context of the given branch.
     *  @param br The branch object to run the functor in
     */
    override fun run(br: Branch) {
        assert(_statCid != 0 && pVar_ != null) {"Action ${namedCid(cid)} is not initialized"}
        (_sm_[_statCid] as FLCid).func(br, *pVar_ as IntArray)
    }

    /**
     *      Load the action of type fun(Branch): Unit
     *  @param statCpt The concept functor.
     *  @param pVar Variable number of concepts
     */
    fun load(statCpt: SpStaticConcept, vararg pVar: SpiritDynamicConcept): SpA_LCid {
        assert(_statCid == 0) {"${namedCid(cid)}: can be loaded only once"}
        _statCid = statCpt.cid
        pVar_ = IntArray(pVar.size) { pVar[it].cid }
        return this
    }

    /** Cid of the first parameter. */
    private var pVar_: IntArray? = null
}

/**
 *      Live. To construct an instance do not use the constructor, use the liveFactory() of the spirit counterpart.
 *  @param spA_LCid - immutable spirit part
 */
class A_LCid(spA_LCid: SpA_LCid): Action(spA_LCid)
