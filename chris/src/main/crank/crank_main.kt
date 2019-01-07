package crank

import basemain.acts
import basemain.brans
import basemain.ins
import cpt.*
import libmain.CrankGroup
import libmain.CrankModule
import libmain.hCr
import stat.cmnSt
import stat.mnSt

/*
 *          Main crank module.
 */
object mnCr: CrankModule() {

/**
 *      Commonly used concepts.
 */
object cmn: CrankGroup {

    // Raise the Branch.breakPoint flag
    val setBreakPoint_act = SpA(-1_426_110_123).load(cmnSt.setBreakPoint)

    val debugOn_act = SpA(-1_160_608_042).load(cmnSt.debugOn)
    val debugOff_act = SpA(1_216_614_327).load(cmnSt.debugOff)

    // Some numbers to work with in cranking
    val num0_numprim = SpNumPrim(2_059_457_726).load(0.0)
    val num1_numprim = SpNumPrim(57_701_987).load(1.0)
    val num2_numprim = SpNumPrim(-1_269_839_473).load(2.0)

    override fun crank() {}

}   //  -937_858_466 -1_491_380_944 -936_769_357 -1_978_110_017 -848_757_907 -1_193_562_290 389_616_405 -1_808_768_002 209_458_482 -1_380_871_710

// Attention circle branch
object circle: CrankGroup {

    val seed = SpSeed(2_063_171_572)

    val waitForUlreadUlwriteToStart_andn = SpAndNeuron(1_732_167_551)

    // The own breed is formed and active at the start of the branch.
    val copyOwnBradToUserInputRequest_act = SpA_2Cid(-2_059_132_975)
        .load(cmnSt.copyPremise, hCr.hardCid.circle_breed, ulread.userInputRequest_bradprem)

    // Send ulread activated copyLive of the own breed as both a request for user line and the address of requester
    val transportUlreadInputRequest_act = SpA_2Cid(-2_089_689_065)
        .load(cmnSt.transportSingleConcept, ulread.breed, ulread.userInputRequest_bradprem)

    // Wait for the user line to become active, then process it
    val userInputValve_andn = SpAndNeuron(-1_384_487_145)

    // After processing the local instance of user line it is anactivated to make the valve wait for the next one
    val anactivateUserInputLine_act = SpA_Cid(-139_070_542)
        .load(cmnSt.anactivate, ulread.userInputLine_strprem)

    // Before requesting to output line of text, place it to the request concept (together with the activation field)
    val copyUserInputLineToOutputRequest_act = SpA_2Cid(517_308_633)
        .load(cmnSt.copyPremise, ulread.userInputLine_strprem, ulwrite.userOutputLine_strprem)

    // Request sending text of line to user
    val sendUlwriteOutputRequest_act = SpA_2Cid(1_873_323_521)
        .load(cmnSt.transportSingleConcept, ulwrite.breed, ulwrite.userOutputLine_strprem)

    // Signal ulread to send next user line with the userInputLine_strprem premise
    val activateRemotelyInputRequest_act = SpA_2Cid(794_381_089)
        .load(cmnSt.activateRemotely, ulread.breed, ulread.userInputRequest_bradprem)

    // For all brans in the project when branch is started it gets an activated breed with its own origBrad. This is
    // true for the circle branch too. Besides, it gets the userThread_cthreadprem concept with user's thread reference (also
    // activated), even if it isn't present in the breed.ins.
    override fun crank() {

        // Circle's breed. Ins and outs are null since this is a root branch, meaning is is not started/finished the usual way.
        hCr.hardCid.circle_breed.load(seed, null, null)
        seed.load(
            acts(
                copyOwnBradToUserInputRequest_act  // Copy breed to userInputRequest_bradprem. It is activated on the side.
            ),
            brans(
                ulread.breed,
                ulwrite.breed
            ),
            stem = waitForUlreadUlwriteToStart_andn
        )

        waitForUlreadUlwriteToStart_andn.loadPrems(
            ulread.breed,       // ulread and
            ulwrite.breed       // ulwrite started?
        ).addEff(
            Float.POSITIVE_INFINITY,
            acts(
                transportUlreadInputRequest_act
            ),
            stem = userInputValve_andn
        )

        userInputValve_andn.loadPrems(
            ulread.userInputLine_strprem    // user text line has come?
        ).addEff(
            Float.POSITIVE_INFINITY,
            acts(
                copyUserInputLineToOutputRequest_act,
                anactivateUserInputLine_act,
                sendUlwriteOutputRequest_act,
                activateRemotelyInputRequest_act
            )
        )
    }
}   //    -1_275_797_463 2_091_624_554 313_424_276 -1_874_867_101 345_223_608 445_101_230

// Reading user input lines.
object ulread: CrankGroup {

    val breed = SpBreed(-1_636_443_905)
    val seed = SpSeed(-2_063_171_572)

    // Send user own address, so enabling him to speak to our branch
    val ulreadSendsUserOwnBrad_act = SpA_Cid(432_419_405)
        .load(mnSt.branchSendsUserItsBrad, hCr.hardCid.userThread_cthreadprem)

    // Line of text from user.
    val userInputLine_strprem = SpStringPrem(1_674_041_321)

    // When active it contains injected breed of the branch-requester
    val userInputRequest_bradprem = SpBradPrem(1_145_833_341)

    // Waiting for the request and sending him new user input line
    val userInputValve_andn = SpAndNeuron(-2_067_698_057)

    // Take line from userInputBuffer_strqprem to userInputLine_strprem
    val moveLineFromBufferToLinePremise_act = SpA_2Cid(165_789_924)
        .load(mnSt.extractLineFromStringQueue, hCr.hardCid.userInputBuffer_strqprem, userInputLine_strprem)

    // Take user input line to branch from which came request
    val transportUserLineToRequester_act = SpA_2Cid(-1_438_089_413)
        .load(cmnSt.transportSingleConcept, userInputRequest_bradprem, userInputLine_strprem)

    // The request is fulfilled, reset it
    val anactivateUserInputRequest_act = SpA_Cid(-691_499_635)
        .load(cmnSt.anactivate, userInputRequest_bradprem)

    override fun crank() {
        breed.load(
            seed,
            ins(
                hCr.hardCid.userThread_cthreadprem      // let ulread know user's thread to be able to communicate with it
            )
        )

        seed.load(
            acts(
                ulreadSendsUserOwnBrad_act
            ),
            stem = userInputValve_andn
        )

        userInputValve_andn.loadPrems(
            userInputRequest_bradprem,              // either injected or activated remotely by requester
            hCr.hardCid.userInputBuffer_strqprem  // filled and activated by the UserTellsCircleMsg user message
        ).addEff(
            Float.POSITIVE_INFINITY,
            acts(
                moveLineFromBufferToLinePremise_act,
                transportUserLineToRequester_act,
                anactivateUserInputRequest_act
            )
        )
    }
}   //  -1_988_590_990 -1_412_401_364 2_074_339_503 -888_399_507

// Writing to user's console.
object ulwrite: CrankGroup {

    val breed = SpBreed(207_026_886)
    val seed = SpSeed(-1_918_726_653)

    // Branch, that requesting the output injects this premise.
    val userOutputLine_strprem = SpStringPrem(-1_186_670_642)

    // Checks the request premise and sends it to user.
    val outputRequestValve_andn = SpAndNeuron(-333_614_575)

    // Send text to user
    val sendTextToUser_act = SpA_2Cid(2_005_795_367)
        .load(mnSt.circleTellsUser, hCr.hardCid.userThread_cthreadprem, userOutputLine_strprem)

    // After text is sent to user, the request needs to be anactivated
    val anactivateOutputRequest_act = SpA_Cid(913_222_153)
        .load(cmnSt.anactivate, userOutputLine_strprem)

    val promptUser_act = SpA_Cid(-367_082_727)
        .load(mnSt.circlePromtsUser, hCr.hardCid.userThread_cthreadprem)

    override fun crank() {

        breed.load(
            seed,
            ins(
                hCr.hardCid.userThread_cthreadprem      // let ulread know user's thread to be able to communicate with it
            )
        )

        seed.load(
            stem = outputRequestValve_andn
        )

        outputRequestValve_andn.loadPrems(
            userOutputLine_strprem
        ).addEff(
            Float.POSITIVE_INFINITY,
            acts(
                sendTextToUser_act,
                anactivateOutputRequest_act
            )
        )
    }
}   //     342_661_687 -1_419_169_404

}
