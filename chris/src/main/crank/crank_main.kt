package crank

import basemain.Cid
import cpt.SpPegPrem
import cpt.SpStringPrem
import cpt.abs.Concept
import cpt.abs.SpiritConcept

    enum class TestEnum(override val spiritConcept: SpiritConcept) : CrankEnumIfc {
        aaa(SpPegPrem(1)),
        bbb(SpStringPrem(2));
    }

    enum class TestEnum2(override val spiritConcept: SpiritConcept) : CrankEnumIfc {
        aaa(SpPegPrem(1)),
        bbb(SpStringPrem(2));
    }

    fun testEnum() {
        println("in testEnum")
    }

    fun testEnum2() {
        println("in testEnum2")
    }
