package crank

import basemain.Cid
import cpt.abs.SpiritConcept
import kotlin.reflect.KClass

/**
 *          Fields and method of crank enums. To implement this interface the enums overload the spiritConcept field. It is
 *      the type of enum by the way.
 */
interface CrankEnumIfc {
    val spiritConcept: SpiritConcept

    val cid: Cid
    get() { return spiritConcept.cid }

    val clazz: KClass<out SpiritConcept>
    get() { return spiritConcept::class}

    val className: String
    get() { return spiritConcept::class.simpleName?:""}
}