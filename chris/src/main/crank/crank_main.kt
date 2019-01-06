package crank

import basemain.acts
import basemain.brans
import basemain.ins
import cpt.*
import cpt.abs.Eft
import libmain.CrankGroup
import libmain.CrankModule
import libmain.hardCrank
import stat.commonStat
import stat.mainStat

/*
 *          Main crank module.
 */
object mainCrank: CrankModule() {

object common: CrankGroup {

    // Raise the Branch.breakPoint flag
    val setBreakPoint_act = SpA(-1_426_110_123)

    val debugOn_act = SpA(-1_160_608_042)
    val debugOff_act = SpA(1_216_614_327)

    // Some numbers to work with in cranking
    val num0_numprim = SpNumPrim(2_059_457_726)
    val num1_numprim = SpNumPrim(57_701_987)
    val num2_numprim = SpNumPrim(-1_269_839_473)

    override fun crank() {

        setBreakPoint_act.load(commonStat.setBreakPoint)

        debugOn_act.load(commonStat.debugOn)
        debugOff_act.load(commonStat.debugOff)

        num0_numprim.load(0.0)
        num1_numprim.load(1.0)
        num2_numprim.load(2.0)
    }

}   //  -937_858_466 -1_491_380_944 -936_769_357 -1_978_110_017 -848_757_907 -1_193_562_290 389_616_405 -1_808_768_002 209_458_482 -1_380_871_710

// Attention circle branch
object circle: CrankGroup {

    val seed = SpSeed(2_063_171_572)

    val waitForUlreadToStart_andn = SpAndNeuron(1_732_167_551)

    // The own breed is formed and active at the start of the branch.
    val copyOwnBradToUserInputRequest_act = SpA_2Cid(-2_059_132_975)

    // Send ulread activated copyLive of the own breed as both a request for user line and the address of requester
    val sendUserInputRequest_act = SpA_2Cid(-2_089_689_065)

    // Wait for the user line to become active, then process it
    val userInputValve_andn = SpAndNeuron(-1_384_487_145)

    // Signal for ulread to send next user line with the userInputLine_strprem premise
    val ActivateUserInputRequest_act = SpA_2Cid(794_381_089)

    // After processing the local instance of user line it is anactivated to make the valve wait for the next one
    val anactivateUserInputLine_act = SpA_Cid(-139_070_542)

    // For all brans in the project when branch is started it gets an activated breed with its own origBrad. This is
    // true for the circle branch too. Besides, it gets the userThread_prem concept with user's thread reference (also
    // activated), even if it isn't present in the breed.ins.
    override fun crank() {

        // Circle's breed. Ins and outs are null since this is a root branch, meaning is is not started/finished the usual way.
        hardCrank.hardCid.circle_breed.load(seed, null, null)
        seed.load(
            acts(
                copyOwnBradToUserInputRequest_act  // Copy breed to userInputRequest_bradprem. It is activated on the side.
            ),
            brans(ulread.breed),
            stem = debugOn_actn() then waitForUlreadToStart_andn
        )
        copyOwnBradToUserInputRequest_act.load(mainStat.copyPremise, hardCrank.hardCid.circle_breed, ulread.userInputRequest_bradprem)

        waitForUlreadToStart_andn.loadPrems(
            ulread.breed    // Wait until ulread starts
        ).loadEffs(
            Eft(
                Float.POSITIVE_INFINITY,
                acts(
                    sendUserInputRequest_act
                ),
                brans = null,
                stem = userInputValve_andn
            )
        )
        sendUserInputRequest_act.load(mainStat.transportSingleConcept, ulread.breed, ulread.userInputRequest_bradprem)

        userInputValve_andn.loadPrems(

        ).loadEffs(

        )
    }
}   //  517_308_633 1_873_323_521 -1_275_797_463 2_091_624_554 313_424_276 -1_874_867_101 345_223_608 445_101_230

// User line reading branch.
object ulread: CrankGroup {

    val breed = SpBreed(-1_636_443_905)
    val seed = SpSeed(-2_063_171_572)

    // Send user own address, so enabling him to speak to our branch
    val ulreadSendsUserOwnBrad_act = SpA_Cid(432_419_405)

    // Line of text from user.
    val userInputLine_strprem = SpStringPrem(1_674_041_321)

    // When active it contains injected breed of the branch-requester
    val userInputRequest_bradprem = SpBradPrem(1_145_833_341)

    // Waiting for the request and sending him new user input line
    val userInputRequestValve_andn = SpAndNeuron(-2_067_698_057)

    override fun crank() {
        breed.load(
            seed,
            ins(
                hardCrank.hardCid.userThread_prem      // let ulread know user's thread to be able to communicate with it
            ),
            outs = null
        )

        seed.load(
            acts(
                ulreadSendsUserOwnBrad_act
            ),
            null,
            stem = debugOn_actn() then userInputRequestValve_andn
        )
        ulreadSendsUserOwnBrad_act.load(mainStat.sendUserBranchBrad, hardCrank.hardCid.userThread_prem)

        userInputRequestValve_andn.loadPrems(
            userInputRequest_bradprem      // either injected or activated remotely by requester
        ).addEffs(
            Float.POSITIVE_INFINITY,
            acts(
            ),
            brans = null,
            stem = null
        )

    }
}   //   -1_438_089_413 -691_499_635 -367_082_727 -1_988_590_990 -1_412_401_364 2_074_339_503 -888_399_507

object ulwrite: CrankGroup {

    override fun crank() {

    }
}   // 165_789_924 207_026_886 -1_918_726_653 -1_186_670_642 -333_614_575 913_222_153 2_005_795_367 342_661_687 -1_419_169_404

}
