package crank

import basemain.Cid
import cpt.SpPegPrem
import cpt.SpStringPrem
import cpt.abs.SpiritConcept
import libmain.CrankEnumIfc
import sun.security.provider.ConfigFile
import kotlin.reflect.KClass

/** Crank container */
class CrankMain {
    enum class TestEnum(concept: SpiritConcept) : CrankEnumIfc {
        aaa(SpPegPrem(1)),
        bbb(SpStringPrem(2));

        override val conceptClass: KClass<out SpiritConcept> = concept::class
        override val cid = concept.cid
    }

    enum class TestEnum2(concept: SpiritConcept) : CrankEnumIfc {
        aaa(SpPegPrem(1)),
        bbb(SpStringPrem(2));

        override val conceptClass: KClass<out SpiritConcept> = concept::class
        override val cid = concept.cid
    }

    fun testEnum() {
        println("in testEnum")
    }


    fun testEnum2() {
        println("in testEnum2")
        (::testEnum3)()
    }
}

fun testEnum3() {

    println("in testEnum3")
}
