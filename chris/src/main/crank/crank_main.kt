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

    val resetBranchDebugSettings_act = SpA(-1_160_608_042)
    val setBranchDebugLevelTo1_act = SpA_Cid(608_100_245)
    val setBranchDebugLevelTo2_act = SpA_Cid(1_523_139_252)

    val resetPodDebugSettings_act = SpA(1_216_614_327)
    val setPodDebugLevelTo1_act = SpA_Cid(-796_168_638)
    val setPodDebugLevelTo2_act = SpA_Cid(-1_458_387_333)

    val resetPodpoolDebugSettings_act = SpA(1_527_119_733)
    val setPodpoolDebugLevelTo1_act = SpA_Cid(-514_014_822)
    val setPodpoolDebugLevelTo2_act = SpA_Cid(157_492_212)

    // Log separate concepts (specified by loading)
    val logCpt0_act = SpA_Cid(-1_808_768_002)
    val logCpt1_act = SpA_Cid(209_458_482)
    val logCpt2_act = SpA_Cid(-1_380_871_710)

    // Some numbers to work with in cranking
    val num0_numprim = SpNumPrim(2_059_457_726)
    val num1_numprim = SpNumPrim(57_701_987)
    val num2_numprim = SpNumPrim(-1_269_839_473)

    override fun crank() {

        setBreakPoint_act.load(commonStat.setBreakPoint)

        resetBranchDebugSettings_act.load(commonStat.resetBranchDebugLevel)
        setBranchDebugLevelTo1_act.load(commonStat.setBranchDebugLevel, num1_numprim)
        setBranchDebugLevelTo2_act.load(commonStat.setBranchDebugLevel, num2_numprim)

        resetPodDebugSettings_act.load(commonStat.resetPodDebugLevelAndFilter)
        setPodDebugLevelTo1_act.load(commonStat.setPodDebugLevelAndFilter, num1_numprim)
        setPodDebugLevelTo2_act.load(commonStat.setPodDebugLevelAndFilter, num2_numprim)

        resetPodpoolDebugSettings_act.load(commonStat.resetPodpoolDebugLevelAndFilter)
        setPodpoolDebugLevelTo1_act.load(commonStat.setPodpoolDebugLevelAndFilter, num1_numprim)
        setPodpoolDebugLevelTo2_act.load(commonStat.setPodpoolDebugLevelAndFilter, num2_numprim)

        num0_numprim.load(0.0)
        num1_numprim.load(1.0)
        num2_numprim.load(2.0)
    }

}   //  -937_858_466 -1_491_380_944 -936_769_357 -1_978_110_017 -848_757_907 -1_193_562_290 389_616_405

// Attention circle branch
object circle: CrankGroup {

    val seed = SpSeed(2_063_171_572)

    val shakeHandsWithUlread_anrn = SpAndNeuron(1_732_167_551)

    // The own breed is formed and active at the start of the branch.
    val copyOwnBreedToUserInputRequest_act = SpA_2Cid(-2_059_132_975)

    // Send ulread activated copy of the own breed as both a request for user line and the address of requester
    val sendUserInputRequest_act = SpA_2Cid(-2_089_689_065)

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
            // Copy breed to userInputRequest_breed. It is activated on the side.
            acts(
                copyOwnBreedToUserInputRequest_act,
common.logCpt0_act.also{ it.loadlog(ulread.userInputRequest_breed) },
common.setBreakPoint_act
            ),

            // Spawn the ulread branch
            brans(ulread.breed),

            stem = shakeHandsWithUlread_anrn
        )
        copyOwnBreedToUserInputRequest_act.load(mainStat.copyCpt0ToCpt1, hardCrank.hardCid.circle_breed, ulread.userInputRequest_breed)

        shakeHandsWithUlread_anrn.loadPrems(
            ulread.breed    // Wait until ulread starts
        ).loadEffs(
            Eft(
                Float.POSITIVE_INFINITY,
                acts(
                    sendUserInputRequest_act
                ),
                brans = null,
                stem = userInputValve_anrn
            )
        )
        sendUserInputRequest_act.load(mainStat.transportSingleConcept, ulread.breed, ulread.userInputRequest_breed)

        userInputValve_anrn.loadPrems(

        ).loadEffs(

        )
    }
}   //  517_308_633 1_873_323_521 -1_275_797_463 2_091_624_554 313_424_276 -1_874_867_101 345_223_608 445_101_230

// User line reading branch.
object ulread: CrankGroup {

    val breed = SpBreed(-1_636_443_905)
    val seed = SpSeed(-2_063_171_572)

    // Send user own address, so enabling him to speak to our branch
    val sendUserUlineBrad_act = SpA_Cid(432_419_405)

    // Line of text from user.
    val userInputLine_strprem = SpStringPrem(1_674_041_321)

    // When active it contains injected breed of the branch-requester
    val userInputRequest_breed = SpBreed(1_145_833_341)

    // Waiting for the request and sending him new user input line
    val userInputRequestValve_anrn = SpAndNeuron(-2_067_698_057)

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


        userInputRequestValve_anrn.loadPrems(
            userInputRequest_breed      // either injected or activated remotely by requester
        ).loadEffs(
            Float.POSITIVE_INFINITY,
            acts(
common.logCpt0_act
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
