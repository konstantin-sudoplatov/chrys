package crank

import basemain.acts
import basemain.brans
import basemain.ins
import cpt.*
import crank.word_processing.pulCr
import libmain.CrankGroup
import libmain.CrankModule
import libmain.hCr
import stat.cmnSt
import stat.mnSt

/**
 *          Main cranks.
 */
object mnCr: CrankModule() {

/**
 *      Commonly used concepts.
 */
object cmn: CrankGroup {

    // Request parent finish the current branch.
    val finishBranch_act = SpA(313_424_276).load(cmnSt.requestParentFinishBranch)

    // Raise the Branch.breakPoint flag
    val setBreakPoint_act = SpA(-1_426_110_123).load(cmnSt.setBreakPoint)

    val debugOn_act = SpA(-1_160_608_042).load(cmnSt.debugOn)
    val debugOff_act = SpA(1_216_614_327).load(cmnSt.debugOff)

    val log1_act = SpA_Cid(-937_858_466)
    val log2_act = SpA_Cid(-1_491_380_944)
    val log3_act = SpA_Cid(-936_769_357)

    val log1_actn = SpActionNeuron(-1_978_110_017).load(acts(log1_act))
    val log2_actn = SpActionNeuron(-848_757_907).load(acts(log2_act))
    val log3_actn = SpActionNeuron(-1_193_562_290).load(acts(log3_act))

    // Some numbers to work with in cranking
    val num0_numprim = SpNumPrim(2_059_457_726).load(0.0)
    val num1_numprim = SpNumPrim(57_701_987).load(1.0)
    val num2_numprim = SpNumPrim(-1_269_839_473).load(2.0)

    override fun crank() {}

}   //         -1_808_768_002 209_458_482 -1_380_871_710

/**
 *      Attention circle branch.
 */
object circle: CrankGroup {

    val seed = SpSeed(2_063_171_572)

    val waitForUlreadAndUlwriteToStart_andn = SpAndNeuron(1_732_167_551)

    // The own breed is formed and active at the start of the branch.
    val copyOwnBradToUserInputRequest_act = SpA_2Cid(-2_059_132_975)
        .load(cmnSt.copyPremise, hCr.hardCid.circle_breed, ulread.userInputRequest_bradprem)

    // Send ulread activated brad as both the request for user line and the address of requester. It is used only the first
    // time to pass to ulread branch our brad, after that we will just be reactivating the request.
    val transportUlreadInputRequest_act = SpA_2Cid(-2_089_689_065)
        .load(cmnSt.transportSingleConcept, ulread.breed, ulread.userInputRequest_bradprem)

    // Wait for the user line to become active, then process it
    val userInputValve_andn = SpAndNeuron(-1_384_487_145)

    // After processing the local instance of user line it is anactivated to make the valve wait for the next one
    val anactivateUserInputLine_act = SpA_Cid(-139_070_542)
        .load(cmnSt.anactivate, ulread.userInputLine_strprem)

    // Anactivate before splitting
    val anactivateUserChain_act = SpA_Cid(-1_031_129_456).load(cmnSt.anactivate, pulCr.splitUl.userChain_strqprem)

    // Prepare launching the store words branch
    val anactivateStoreWordsFinishedPeg_act = SpA_Cid(580_717_991).load(cmnSt.anactivate,
        pulCr.storeWordsFromUserChain.branchFinished_pegprem)

    // Before requesting to output line of text, place it to the request concept (together with the activation field)
    val copyUserInputLineToOutputRequest_act = SpA_2Cid(517_308_633)
        .load(cmnSt.copyPremise, ulread.userInputLine_strprem, ulwrite.outputLine_strprem)

    // Pass to ulwrite request for sending text to user. Text is inside the request.
    val sendUlwriteOutputRequest_act = SpA_2Cid(1_873_323_521)
        .load(cmnSt.transportSingleConcept, ulwrite.breed, ulwrite.outputLine_strprem)

    // Ask ulwrite to prompt user for next input
    val activateRemotelyPromptRequest_act = SpA_2Cid(-1_275_797_463)
        .load(cmnSt.activateRemotely, ulwrite.breed, ulwrite.promptRequest_pegprem)

    // Signal ulread to send next user line with the userInputLine_strprem premise. Second time and on we just reactivate the request.
    val activateRemotelyInputRequest_act = SpA_2Cid(794_381_089)
        .load(cmnSt.activateRemotely, ulread.breed, ulread.userInputRequest_bradprem)

    // Wait until result of splitting user line becomes available, then... what then?
    val parsingResultValve_andn = SpAndNeuron(2_091_624_554)

    // Print something on the screen and request next input.
    val giveUserFeedback_actn = SpActionNeuron(1_125_150_058)

    // Jump to getting new user line
    val storeWordsValve_andn = SpAndNeuron(-1_874_867_101)

    // For all brans in the project when branch is started it gets an activated breed with its own Brad. This is
    // true for the circle branch too. Besides, it gets the userThread_threadprem concept with user's thread reference (also
    // activated), even though it isn't present in the breed.ins.
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
            stem = waitForUlreadAndUlwriteToStart_andn
        )

        // Wait until the ulread and ulwrite branches are started then send ulread our brad (and it also means requesting
        // the first user input from it).
        waitForUlreadAndUlwriteToStart_andn.loadPrems(
            ulread.breed,       // ulread and
            ulwrite.breed       // ulwrite started?
        ).addEff(
            Float.POSITIVE_INFINITY,
            acts(
                transportUlreadInputRequest_act
            ),
            stem = userInputValve_andn
        )
        // preparations finished, request for the first user input sent

        // Wait for user input to come, then start parsing branch for that line
        userInputValve_andn.loadPrems(
            ulread.userInputLine_strprem    // user text line has come?
        ).addEff(
            Float.POSITIVE_INFINITY,
            acts(anactivateUserChain_act),
            brans = brans(pulCr.splitUl.breed),
            stem = parsingResultValve_andn
        )

        // Wait until user line is parsed then store the new words
        parsingResultValve_andn.loadPrems(
            pulCr.splitUl.userChain_strqprem
        ).addEff(
            Float.POSITIVE_INFINITY,
            acts(anactivateStoreWordsFinishedPeg_act),
            brans = brans(pulCr.storeWordsFromUserChain.breed),
            stem = giveUserFeedback_actn
        )

        // Print something on the screen then prompt user
        giveUserFeedback_actn.load(
            acts(
                copyUserInputLineToOutputRequest_act,
                sendUlwriteOutputRequest_act,
                activateRemotelyPromptRequest_act
            ),
            stem = storeWordsValve_andn
        )

        // Wait until ready for new user line then go get it
        storeWordsValve_andn.loadPrems(
            pulCr.storeWordsFromUserChain.branchFinished_pegprem        // wait until the chain is stored
        ).addEff(
            Float.POSITIVE_INFINITY,
            acts(
                anactivateUserInputLine_act,
                activateRemotelyInputRequest_act
            ),
            stem = userInputValve_andn
        )

    }
}   //    538_155_321 -1_945_366_218 1_675_242_166 1_592_949_000 345_223_608 445_101_230

/**
 *      Reading user input lines.
 */
object ulread: CrankGroup {

    val breed = SpBreed(-1_636_443_905)
    val seed = SpSeed(-2_063_171_572)

    // Send user own address, so enabling him to speak to our branch
    val ulreadSendsUserOwnBrad_act = SpA_Cid(432_419_405)
        .load(mnSt.branchSendsUserItsBrad, hCr.hardCid.userThread_threadprem)

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
                hCr.hardCid.userThread_threadprem      // let ulread know user's thread to be able to communicate with it
            )
        )

        seed.load(
            acts(
                ulreadSendsUserOwnBrad_act
            ),
            stem = userInputValve_andn
        )

        // Wait until lines from user are available and a request for user input comes. Then take one line from the user
        // input buffer and send it to the requester.
        userInputValve_andn.loadPrems(
            userInputRequest_bradprem,              // either injected or activated remotely by requester
            hCr.hardCid.userInputBuffer_strqprem    // filled and activated by the UserTellsCircleMsg user message
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

/**
 *      Writing to user's console.
 */
object ulwrite: CrankGroup {

    val breed = SpBreed(207_026_886)
    val seed = SpSeed(-1_918_726_653)

    // Branch, that requesting the output injects this premise.
    val outputLine_strprem = SpStringPrem(-1_186_670_642)

    // If client branch wants to prompt user for next line, it remotely activates this peg.
    val promptRequest_pegprem = SpPegPrem(342_661_687)

    // Checks the output request and prompt request premises and  and sends them to user. It's a pick neuron, it does
    // work related to one of the premises from a list.
    val requestValve_pickn = SpPickNeuron(-333_614_575)

    // Send text to user
    val sendTextToUser_act = SpA_2Cid(2_005_795_367)
        .load(mnSt.circleTellsUser, hCr.hardCid.userThread_threadprem, outputLine_strprem)

    // After text is sent to user, the request needs to be anactivated
    val anactivateRequestForOutputLine_act = SpA_Cid(913_222_153)
        .load(cmnSt.anactivate, outputLine_strprem)

    // Send user the prompt message
    val sendUserPrompt_act = SpA_Cid(-367_082_727)
        .load(mnSt.circlePromtsUser, hCr.hardCid.userThread_threadprem)

    // Anactivate prompt request after it is processed
    val anactivatePromptRequest_act = SpA_Cid(-1_419_169_404)
        .load(cmnSt.anactivate, promptRequest_pegprem)

    override fun crank() {

        breed.load(
            seed,
            ins(
                hCr.hardCid.userThread_threadprem      // let ulread know user's thread to be able to communicate with it
            )
        )

        seed.load(
            stem = requestValve_pickn
        )

        requestValve_pickn.add(
            premoid = outputLine_strprem,
            acts = acts(
                sendTextToUser_act,
                anactivateRequestForOutputLine_act
            )
        ).add(
            premoid = promptRequest_pegprem,
            acts = acts(
                sendUserPrompt_act,
                anactivatePromptRequest_act
            )
        )
    }
}   //  -1_027_420_163 -10_664_875 107_759_426 -1_852_026_187 -1_255_850_367 -48_028_321

}
