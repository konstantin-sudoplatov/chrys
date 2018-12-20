package crank

import cpt.SpPegPrem
import cpt.SpStringPrem
import libmain.CrankGroup
import libmain.CrankModule

object crankMain: CrankModule() {

    object crankGroup1: CrankGroup {
        val testConcept1_pegprem = SpPegPrem(2_000_001)
        val testConcept2_strprem = SpStringPrem(2_000_002)

        override fun crank() {
            testConcept1_pegprem.ver = 1
            testConcept2_strprem.ver = 1
        }
    }

    object crankGroup2: CrankGroup {
        val testConcept1_pegprem = SpPegPrem(2_000_003)
        val testConcept2_strprem = SpStringPrem(2_000_004)

        override fun crank() {
            testConcept1_pegprem.ver = 2
            testConcept2_strprem.ver = 2
        }
    }


}

//import cpt.SpPegPrem
//import cpt.SpStringPrem
//import cpt.abs.SpiritConcept
//import libmain.CrankEnumIfc
//import kotlin.reflect.KClass
//
///** Crank container */
//object crankMain {
//    enum class TestEnum(concept: SpiritConcept) : CrankEnumIfc {
//        aaa(SpPegPrem(1)),
//        bbb(SpStringPrem(2));
//
//        override val conceptClass: KClass<out SpiritConcept> = concept::class
//        override val cid = concept.cid
//    }
//
//    enum class TestEnum2(concept: SpiritConcept) : CrankEnumIfc {
//        aaa(SpPegPrem(11)),
//        bbb(SpStringPrem(12));
//
//        override val conceptClass: KClass<out SpiritConcept> = concept::class
//        override val cid = concept.cid
//    }
//
//    fun TestEnum.testEnum() {
//        println("in testEnum")
//        val en = TestEnum.aaa
//        val en1 = cid
////        println("aaa ${this.aaa}")
//    }
//
//
//    fun TestEnum2.testEnum2() {
//        println("in testEnum2")
//    }
//}
//
//fun testEnum3() {
//
//    println("in testEnum3")
//}
