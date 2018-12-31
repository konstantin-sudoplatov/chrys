package crank

import basemain.acts
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
        val shakeHandsWithUline_anrn = SpAndNeuron(1_732_167_551)
        val userInputLine_strprem = SpStringPrem(1_674_041_321)         // line of text from user. Premise is used for requesting line from the ulread branch.
        val requestContainer_cptprem = SpConceptPrem(-2_089_689_065)// Container for the userInputLine_strprem
        val requestUserInputLine_act = SpA_2Cid(794_381_089)            // this action transports the userInputLine_cptprem to ulread, see the activation note in the comments of the stat concept.
        val userInputValve_anrn = SpAndNeuron(-1_384_487_145)           // does requesting and checks the result

        // For all brans in the project when branch is started it gets an activated breed with its own ownBrad. This is
        // true for the circle branch too. Besides, it gets the userThread_prem concept with user's thread reference (also
        // activated), even if it isn't present in the breed.ins.
        override fun crank() {

            // Circle's breed. Ins and outs are null since this is a root branch, meaning is is not started/finished the usual way.
            hardCrank.hardCid.circle_breed.load(seed, null, null)
            seed.load(
                null,
                brans(ulread.breed),
                stem = shakeHandsWithUline_anrn
            )

            shakeHandsWithUline_anrn.loadPrems(
                ulread.breed
            ).loadEffs(
                Eft(
                    Float.POSITIVE_INFINITY,
                    acts(requestUserInputLine_act),
                    brans = null,
                    stem = userInputValve_anrn
                )
            )
            requestUserInputLine_act.load(mainStat.requestUserInputLine, ulread.breed, requestContainer_cptprem)

            requestContainer_cptprem.load(userInputLine_strprem)
            userInputValve_anrn.loadPrems(

            ).loadEffs(

            )
        }
    }   //  517_308_633

    // User line reading branch.
    object ulread: CrankGroup {

        val breed = SpBreed(-1_636_443_905)
        val seed = SpSeed(-2_063_171_572)
        val sendUserUlineBrad_act = SpA_Cid(432_419_405)

        override fun crank() {
            breed.load(
                seed,
                ins(
                    hardCrank.hardCid.circle_breed,        // let ulread know circle's breed to be able to communicate with it
                    hardCrank.hardCid.userThread_prem      // let ulread know user's thread to be able to communicate with it
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

    object ulwrite: CrankGroup {

        override fun crank() {

        }
    }   // 165_789_924 207_026_886 -1_918_726_653 -1_186_670_642 -333_614_575 913_222_153 2_005_795_367
}
