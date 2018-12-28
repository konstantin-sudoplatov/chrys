package cpt.abs

import basemain.Cid
import basemain.Cvr
import basemain.MAX_DYNAMIC_CID
import basemain.MIN_DYNAMIC_CID
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
        var s = this::class.qualifiedName?: ""
        s += "\n    cid = $cid"
        s += "\n    ver = $ver"

        return s
    }

    /**
        Create "live" wrapper for this object.
    */
    abstract fun liveFactory(): Concept
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

    public override fun clone(): Any {
        return super.clone()
    }

    override fun toString(): String {
        var s = this::class.qualifiedName as String
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
    init {
        assert(cid.toUInt() >= MIN_DYNAMIC_CID && cid.toUInt() <= MAX_DYNAMIC_CID)
    }
}

/**
 *      Base for live dynamic concepts.
 *
 *  @param spiritDynamicConcept
 */
abstract class DynamicConcept(spiritDynamicConcept: SpiritDynamicConcept): Concept(spiritDynamicConcept) {

}