package crank

import basemain.acts
import basemain.ar
import basemain.brans
import cpt.*
import cpt.abs.Eft
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
    }   //   -139_070_542 2_059_457_726 57_701_987 -1_269_839_473 -2_059_132_975

    // Attention circle branch
    object circle: CrankGroup {
        val seed = SpSeed(2_063_171_572)
        val shakeHandsWithUline = SpAndNeuron(1_732_167_551)

        override fun crank() {

            hardCrank.hardCids.circle_breed.load(seed)
            seed.load(
                null,
                brans(uline.breed),
                stem = shakeHandsWithUline
            )

            shakeHandsWithUline.loadPrems(
                uline.breed
            ).loadEffs(
                Eft(
                    10f,
                    null,
                    null,
                    null
                ),
                Eft(
                    Float.POSITIVE_INFINITY,
                    null,
                    null,
                    null
                )
            )

        }   //  432_419_405 1_674_041_321 794_381_089 -1_384_487_145 -2_089_689_065 517_308_633
    }

    // User line branch
    object uline: CrankGroup {

        val breed = SpBreed(-1_636_443_905)
        val seed = SpSeed(-2_063_171_572)

        override fun crank() {
            breed.load(seed)
        }
    }
}
