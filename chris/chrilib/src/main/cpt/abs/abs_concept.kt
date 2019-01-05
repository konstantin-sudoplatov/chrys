package cpt.abs

import atn.Branch
import basemain.*
import libmain._nm_
import libmain.namedCid

/**
 *      Base class for all concepts.
 *  "Spiritual" means stable and storable as opposed to the "Live" concepts, that are in a constant use, changing and live
 *  only in memory. All live concepts contain reference to its spirit counterparts.
 *  @param cid Concept identifier. If 0, then the spirit map will assign it a real one.
*/
abstract class SpiritConcept(cid: Cid) {

    /** Concept identifier */
    var cid: Cid = cid

    /** Version number */
    var ver: Cvr = 0

    /**
     *      Minimal form of toString()
     */
    open fun toStr() = if(GDEBUG_LV >= 0) _nm_!![cid]?: "noname" else this::class.qualifiedName

    override fun toString(): String {
        var s = this::class.qualifiedName?: "anonymous"
        s += "\n    cid = ${namedCid(cid)}"
        s += "\n    ver = $ver"

        return s
    }

}

/**
 *      Live wrapper for the SpiritConcept.
 *  The spiritConcept concepts contain stable data. In fact all brans consider them immutable. Their live mates in contrast
 *  operate with changeable data, like activation or prerequisites. While the spiritConcept concepts are shared by all caldrons,
 *  the live ones are referenced only in their own brans.
 *
 *  We don't create live concepts directly through constructors, instead we use the liveFactory() method of their
 *  holy partners.
 *
 *  @param spiritConcept
*/
abstract class Concept(spiritConcept: SpiritConcept): Cloneable {

    /** Spiritual part */
    var sp = spiritConcept

    val cid
        get() = sp.cid

    public override fun clone(): Concept {
        return super.clone() as Concept
    }

    /**
     *      Like cloning, but without copying data not relevant to the reasoning, such as cid, ver, or the usage statistics.
     *  @param dest Live concept to copy data to
     */
    open fun copy(dest: Concept) {
        assert(dest::class == this::class) { "Types of the concepts must match exactly, we have this = %s, dest = %s".
            format(this::class.qualifiedName, dest::class.qualifiedName)}
        dest.sp = sp
    }

    /**
     *      Minimal form of toString()
     */
    open fun toStr() = if(GDEBUG_LV >= 0) _nm_!![cid]?: "noname" else this::class.qualifiedName?: "anonymous"

    override fun toString(): String {
        var s: String = this::class.qualifiedName as String
        s += "\nsp = $sp".replace("\n", "\n    ")
        return s
    }
}

/**
 *       Base for all spirit dynamic concepts.
 *
 *  @param cid
*/
abstract class SpiritDynamicConcept(cid: Cid): SpiritConcept(cid) {

    /**
        Create "live" wrapper for this object.
    */
    abstract fun liveFactory(): DynamicConcept

    init {
        assert(cid == 0 || cid.toUInt() >= MIN_DYNAMIC_CID && cid.toUInt() <= MAX_DYNAMIC_CID)
    }
}

/**
 *      Base for live dynamic concepts.
 *
 *  @param spiritDynamicConcept
 */
abstract class DynamicConcept(spiritDynamicConcept: SpiritDynamicConcept): Concept(spiritDynamicConcept)

/**
 *       Base for all acts.
 *
 *  @param cid
*/
abstract class SpiritAction(cid: Cid): SpiritDynamicConcept(cid) {

    override fun toString(): String {
        return super.toString() + "\n    _statCid = ${namedCid(_statCid)}"
    }

    /**
     *      Run the static concept functor in the context of the given branch.
     *  @param br The branch object to run the functor in
     */
    abstract fun run(br: Branch)

    /** Cid of the static concept. */
    protected var _statCid: Cid = 0
}

/**
 *      Base for live acts.
 *
 *  @param spiritAction
 */
abstract class Action(spiritAction: SpiritAction): DynamicConcept(spiritAction) {

    /**
     *      Run the static concept functor in the context of given branch.
     *  @param br The branch object to run in
     */
    fun run(br: Branch) = (sp as SpiritAction).run(br)
}