package crank

import cpt.SpPegPrem
import cpt.SpSeed
import cpt.SpStringPrem
import libmain.CrankGroup
import libmain.CrankModule
import libmain.hardCrank

object mainCrank: CrankModule() {

    object common: CrankGroup {
        val testConcept1_pegprem = SpPegPrem(2_000_001)
        val testConcept2_strprem = SpStringPrem(2_000_002)

        override fun crank() {
            testConcept1_pegprem.ver = 1
            testConcept2_strprem.ver = 1
        }
    }

    object circle: CrankGroup {
        val seed = SpSeed(2_000_003)

        override fun crank() {
            hardCrank.hardCids.circle_breed.load(seed)
        }
    }
}
