package cpt

/*
        The action concept is an interface, bridge between the world of dynamic concepts,
    that knows nothing about the code and operates only with cids and the static world, which is a set of functions.
    Essentially what actions do - they call to those functions with given parameters.
*/

import atn.Branch
import basemain.Cid
import cpt.abs.DynamicConcept
import cpt.abs.SpiritDynamicConcept

/**
 *      Spirit action. It invokes functor of given static concept. The function of the functor has signature:
 *  fun(Branch): Unit
 *  @param cid Cid of the concept - real one or 0, if it is supposed to be assigned by the spirit map.
 */
open class SpA(cid: Cid): SpiritDynamicConcept(cid) {
    override fun liveFactory(): A {
        return A(this)
    }

    /**
     *      Run the static concept functor in the context of given branch.
     *  @param br The branch object to run in
     */
    fun run(br: Branch) {

    }
}

/**
 *      Live. To construct an instance do not use the constructor, use the liveFactory() of the spirit counterpart.
 *  @param spA - immutable spirit part
 */
open class A(spA: SpA): DynamicConcept(spA) {

    /**
     *      Run the static concept functor in the context of given branch.
     *  @param br The branch object to run in
     */
    fun run(br: Branch) = (sp as SpA).run(br)
}

/**
 *      Spirit action. It invokes functor of given static concept. The function of the functor has signature:
 *  fun(Branch, Cid): Unit
 *  @param cid Cid of the concept - real one or 0, if it is supposed to be assigned by the spirit map.
 */
open class SpA_Cid(cid: Cid): SpA(cid) {
    override fun liveFactory(): A_Cid {
        return A_Cid(this)
    }
}

/**
 *      Live. To construct an instance do not use the constructor, use the liveFactory() of the spirit counterpart.
 *  @param spA_Cid - immutable spirit part
 */
open class A_Cid(spA_Cid: SpA): A(spA_Cid) {

}

/**
 *      Spirit action. It invokes functor of given static concept. The function of the functor has signature:
 *  fun(Branch, Cid, Cid): Unit
 *  @param cid Cid of the concept - real one or 0, if it is supposed to be assigned by the spirit map.
 */
class SpA_2Cid(cid: Cid): SpA_Cid(cid) {
    override fun liveFactory(): A_2Cid {
        return A_2Cid(this)
    }
}

/**
 *      Live. To construct an instance do not use the constructor, use the liveFactory() of the spirit counterpart.
 *  @param spA_2Cid - immutable spirit part
 */
class A_2Cid(spA_2Cid: SpA_2Cid): A_Cid(spA_2Cid) {

}

/**
 *      Spirit action. It invokes functor of given static concept. The function of the functor has signature:
 *  fun(Branch, vararg Cid): Unit
 *  @param cid Cid of the concept - real one or 0, if it is supposed to be assigned by the spirit map.
 */
open class SpA_LCid(cid: Cid): SpA(cid) {
    override fun liveFactory(): A_LCid {
        return A_LCid(this)
    }
}

/**
 *      Live. To construct an instance do not use the constructor, use the liveFactory() of the spirit counterpart.
 *  @param spA_Cid - immutable spirit part
 */
open class A_LCid(spA_LCid: SpA_LCid): A(spA_LCid) {

}
