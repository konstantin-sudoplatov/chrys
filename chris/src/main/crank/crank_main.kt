package crank

import basemain.acts
import basemain.brans
import basemain.ins
import cpt.*
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
}   //    2_059_457_726 57_701_987 -1_269_839_473

// Attention circle branch
object circle: CrankGroup {

    val seed = SpSeed(2_063_171_572)

    val shakeHandsWithUlread_anrn = SpAndNeuron(1_732_167_551)

    // The own breed is formed and active at the start of the branch.
    val copyOwnBreedToUserInputRequestBreed_act = SpA_2Cid(-2_059_132_975)

    // Send ulread activated copy of the own breed as both a request for user line and the address of requester
    val sendUserInputRequestBreed_act = SpA_2Cid(-2_089_689_065)

    // Wait for the user line to become active, then process it
    val userInputValve_anrn = SpAndNeuron(-1_384_487_145)


    // Signal for ulread to send next user line with the userInputLine_strprem premise
    val ActivateUserInputRequest_act = SpA_2Cid(794_381_089)

    // After processing the local instance of user line it is anactivated to make the valve wait for the next one
    val anactivateUserInputLine_act = SpA_Cid(-139_070_542)

    // For all brans in the project when branch is started it gets an activated breed with its own ownBrad. This is
    // true for the circle branch too. Besides, it gets the userThread_prem concept with user's thread reference (also
    // activated), even if it isn't present in the breed.ins.
    override fun crank() {

        // Circle's breed. Ins and outs are null since this is a root branch, meaning is is not started/finished the usual way.
        hardCrank.hardCid.circle_breed.load(seed, null, null)
        seed.load(
            // Copy breed to U
            acts(copyOwnBreedToUserInputRequestBreed_act),

            // Spawn the ulread branch
            brans(ulread.breed),

            stem = shakeHandsWithUlread_anrn
        )
        copyOwnBreedToUserInputRequestBreed_act.load(mainStat.copyCpt0ToCpt1, hardCrank.hardCid.circle_breed, ulread.userInputRequest_breed)

        shakeHandsWithUlread_anrn.loadPrems(

            // Wait until ulread has started
            ulread.breed
        ).loadEffs(
//            Eft(
//                Float.POSITIVE_INFINITY,
//                acts(requestUserInput_act),
//                brans = null,
//                stem = userInputValve_anrn
//            )
        )

        userInputValve_anrn.loadPrems(

        ).loadEffs(

        )
    }
}   //  517_308_633 1_873_323_521 -1_275_797_463 2_091_624_554 313_424_276 -1_874_867_101 345_223_608 445_101_230

// User line reading branch.
object ulread: CrankGroup {

    val breed = SpBreed(-1_636_443_905)
    val seed = SpSeed(-2_063_171_572)
    val sendUserUlineBrad_act = SpA_Cid(432_419_405)
    val userInputLine_strprem = SpStringPrem(1_674_041_321)         // line of text from user.
    val userInputRequest_breed = SpBreed(1_145_833_341)          // when active it contains injected breed of the branch-requester

    override fun crank() {
        breed.load(
            seed,
            ins(
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
}   //  -2_067_698_057 -1_438_089_413 -691_499_635 -367_082_727 -1_988_590_990 -1_412_401_364 2_074_339_503 -888_399_507

object ulwrite: CrankGroup {

    override fun crank() {

    }
}   // 165_789_924 207_026_886 -1_918_726_653 -1_186_670_642 -333_614_575 913_222_153 2_005_795_367 342_661_687 -1_419_169_404

}
