package cpt.abs

import atn.Branch
import basemain.*
import libmain._nm_
import kotlin.test.assert

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

    override fun toString(): String {
        var s: String

        if      // is there a name for this concept?
                (_nm_ != null && cid in _nm_)
            s = "%s (%s)".format(_nm_[cid], this::class.qualifiedName?: "")
        else
            s = "%s (%s)".format("noname", this::class.qualifiedName?: "")

        s += "\n    cid = $cid"
        s += "\n    ver = $ver"

        return s
    }

    /**
     *      Minimal form of toString()
     */
    fun toStr() = if(DEBUG_ON) _nm_!![cid]?: "noname" else this::class.qualifiedName
}

/**
 *      Live wrapper for the SpiritConcept.
 *  The spiritConcept concepts contain stable data. In fact all branches consider them immutable. Their live mates in contrast
 *  operate with changeable data, like activation or prerequisites. While the spiritConcept concepts are shared by all caldrons,
 *  the live ones are referenced only in their own branches.
 *
 *  We don't create live concepts directly through constructors, instead we use the liveFactory() method of their
 *  holy partners.
 *
 *  @param spiritConcept
*/
abstract class Concept(spiritConcept: SpiritConcept): Cloneable {

    /** Spiritual part */
    val sp = spiritConcept

    val cid
        get() = sp.cid

    public override fun clone(): Concept {
        return super.clone() as Concept
    }

    override fun toString(): String {
        var s: String = this::class.qualifiedName as String
        s += "\nsp = $sp".replace("\n", "\n    ")
        return s
    }

    /**
     *      Minimal form of toString()
     */
    fun toStr() = if(DEBUG_ON) _nm_!![cid]?: "noname" else this::class.qualifiedName
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
        assert(cid.toUInt() >= MIN_DYNAMIC_CID && cid.toUInt() <= MAX_DYNAMIC_CID)
    }
}

/**
 *      Base for live dynamic concepts.
 *
 *  @param spiritDynamicConcept
 */
abstract class DynamicConcept(spiritDynamicConcept: SpiritDynamicConcept): Concept(spiritDynamicConcept)

/**
 *       Base for all actions.
 *
 *  @param cid
*/
abstract class SpiritAction(cid: Cid): SpiritDynamicConcept(cid) {

    /**
     *      Run the static concept functor in the context of the given branch.
     *  @param br The branch object to run the functor in
     */
    abstract fun run(br: Branch)

    /** Cid of the static concept. */
    protected var _statCid: Cid = 0
}

/**
 *      Base for live actions.
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