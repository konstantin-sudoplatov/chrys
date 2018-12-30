package crank

import basemain.acts
import basemain.ar
import basemain.brans
import basemain.ins
import cpt.*
import cpt.abs.Eft
import libmain.CrankGroup
import libmain.CrankModule
import libmain.hardCrank
import stat.mainStat

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

        // For all branches in the project when branch is started it gets an activated breed with its own ownBrad. This is
        // true for the circle branch too. Besides, it gets the userThread_prem concept with user's thread reference (also
        // activated), even if it isn't present in the breed.ins.
        override fun crank() {

            // Circle's breed. Ins and outs are null since this is a root branch, meaning is is not started/finished the usual way.
            hardCrank.hardCid.circle_breed.load(seed, null, null)
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

        }
    }   //   1_674_041_321 794_381_089 -1_384_487_145 -2_089_689_065 517_308_633

    // User line branch
    object uline: CrankGroup {

        val breed = SpBreed(-1_636_443_905)
        val seed = SpSeed(-2_063_171_572)
        val sendUserUlineBrad_act = SpA_Cid(432_419_405)

        override fun crank() {
            breed.load(
                seed,
                ins(
                    hardCrank.hardCid.circle_breed,        // let uline know circle's breed to be able to communicate with it
                    hardCrank.hardCid.userThread_prem      // let uline know user's thread to be able to communicate with it
                ),
                outs = null
            )

            seed.load(
                acts(sendUserUlineBrad_act),
                null,
                null
            )
            sendUserUlineBrad_act.load(mainStat.sendUserBranchBrad, hardCrank.hardCid.userThread_prem)
        }
    }   // 1_145_833_341 -2_067_698_057 -1_438_089_413 -691_499_635 -367_082_727 -1_988_590_990 -1_412_401_364
}
