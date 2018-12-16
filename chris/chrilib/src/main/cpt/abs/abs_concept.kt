package cpt.abs

import basemain.Cid
import basemain.Cvr

/**
*       Base class for all concepts.
*   "Spiritual" means stable and storable as opposed to the "Live" concepts, that are in a constant use, changing and live
*   only in memory. All live concepts contain reference to its spirit counterparts.
*/
abstract class SpiritConcept(cid: Cid = 0) {

    /** Concept identifier */
    var cid: Cid = cid

    /** Version number */
    var ver: Cvr = 0

    /**
        Create "live" wrapper for this object.
    */
    abstract fun live_factory(): Concept;
}

/**
        Live wrapper for the SpiritConcept.
    The spirit concepts contain stable data. In fact all branches consider them immutable. Their live mates in contrast
    operate with changeable data, like activation or prerequisites. While the spirit concepts are shared by all caldrons,
    the live ones are referenced only in their own branches.

        We don't create live concepts directly through constructors, instead we use the live_factory() method of their
    holy partners.
*/
abstract class Concept private constructor(spirit: SpiritConcept): Cloneable {

    /** Spiritual part */
    val spr = spirit

    override public fun clone(): Any {
        return super.clone()
    }
}
