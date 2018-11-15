/// Dynamic concept names
module crank.crank_main;
import std.typecons;

import proj_data;

import chri_types, chri_data;
import crank.crank_types, crank.crank_registry;
import cpt.cpt_actions, cpt.cpt_neurons, cpt.cpt_premises, cpt.cpt_primitives;

import stat.stat_types, stat.stat_main;

/// Dynamic concept names and cids.
// , , , , 1_278_962_165, 49_787_120
enum CommonConcepts: DcpDsc {

    // Service concepts
    checkUp_act = cd!(SpA, 3_525_361_282),              // raise the CheckPt_ flag of the caldron
    logCpt0_cact = cd!(SpA_Cid, 246_390_338),          // log a concept, using concept.toString()
    logCpt1_cact = cd!(SpA_Cid, 1_005_527_366),        // ditto
    logCpt2_cact = cd!(SpA_Cid, 122_016_958),          // ditto
    zond0_anrn = cd!(SpActionNeuron, 2_279_163_875),       // test action neuron to inject into different points of workflow
    zond1_anrn = cd!(SpActionNeuron, 2_025_623_255),       // ditto
    zond2_anrn = cd!(SpActionNeuron, 1_321_617_741),       // ditto
    circus1_anrn = cd!(SpActionNeuron, 33_533_622),        // temporary while name not yet invented
    circus1_andnrn = cd!(SpAndNeuron, 2_142_584_142),      // ditto
    circus2_anrn = cd!(SpActionNeuron, 3_372_907_570),     // ditto
    circus2_andnrn = cd!(SpAndNeuron, 2_800_603_496),      // ditto
    circus3_anrn = cd!(SpActionNeuron, 3_786_801_661),     // ditto
    circus3_andnrn = cd!(SpAndNeuron, 4_220_759_348),      // ditto

    /// Controlling the debug level inside the caldron
    setDebugLevel_0_act = cd!(SpA, 3_426_667_410),
    setDebugLevel_1_act = cd!(SpA, 805_124_526),
    setDebugLevel_2_act = cd!(SpA, 2_996_929_904),

    /// call current Caldron._requestStopAndWait_(), can be used by all caldrons
    wait_act = cd!(SpA, 580_052_493),
    stop_act = cd!(SpA, 3_520_033_260),


}

/// Setup common concepts.
void commonConcepts() {
    mixin(dequalify_enums!(CommonConcepts));

    // Setup the checkup
    cp!checkUp_act.load(st!checkUp_stat);

    // Setup the log actions
    cp!logCpt0_cact.load(st!logConcept_stat);
    cp!logCpt1_cact.load(st!logConcept_stat);
    cp!logCpt2_cact.load(st!logConcept_stat);

    // Setup controlling the debug level
    cp!setDebugLevel_0_act.load(st!setDebugLevel_0_stat);
    cp!setDebugLevel_1_act.load(st!setDebugLevel_1_stat);
    cp!setDebugLevel_2_act.load(st!setDebugLevel_2_stat);

    // Setup the stop and wait actions
    cp!wait_act.load(st!wait_stat);
    cp!stop_act.load(st!stop_stat);
}

/// Chat branch enums
// 3_567_444_531, 2_650_964_728, 2_888_019_240, 2_383_825_757, 3_505_369_639
enum Chat: DcpDsc {

    /// This is the root branch of the attention circle. It heads the handshaker and spawns the uline
    chat_seed = cd!(SpActionNeuron, 2_500_739_441),

    /// Setting up the uline branch (see actions below)
    /// After chat starts the uline branch, it sends user its own breed. Also it sends uline Tid of the user thread
    /// (console or http), so that uline could talk to user.
    shakeHandsWithUline_andnrn_chat = cd!(SpAndNeuron, 3_996_466_002),

    /// The action for the handshaker. After chat starts the uline branch, it sends user its own breed.
    sendUlineChatBreed_c2act_chat = cd!(SpA_2Cid, 553_436_801),

    /// The action for the handshaker. It sends uline Tid of the user thread (console or http), so that uline could be able
    /// to talk to the user.
    sendUlineUserTid_c2act_chat = cd!(SpA_2Cid, 3_408_832_589),

    /// Prepare inPars for the putUserLine branch
    copyUserInputToOutput_c2act_uline = cd!(SpA_2Cid, 1_099_498_783),
}

/// Setup the chat branch.
void chat() {
    mixin(dequalify_enums!(HardCids, CommonConcepts, Chat, Uline, GetUserline, PutUserLine));    // anonymizes the concept enums, so we don't need use their full names.

    // Setup the breed and seed
    cp!chat_breed_hcid.load(chat_seed, null, null);
    cp!chat_seed.addEffs(
        null,
        [   // brans
            shakeHandsWithUline_andnrn_chat,    // handshake with uline
            uline_breed                         // start uline branch
        ]
    );

    // Handshake with uline
    //prems: wait for tid from user. no point in waiting uline breed, it is activated in the same iteration with spawnig
    cp!shakeHandsWithUline_andnrn_chat.addPrem(userTid_tidprem_hcid);
    cp!shakeHandsWithUline_andnrn_chat.addEffs(
        float.infinity,
        [   // acts
            sendUlineChatBreed_c2act_chat,       // give uline own breed
            sendUlineUserTid_c2act_chat,         // give uline user's Tid
        ],
        [
            circus1_anrn,
        ]
    );
    // acts
    cp!sendUlineChatBreed_c2act_chat.load(st!sendConceptToBranch_stat, uline_breed, chat_breed_hcid);
    cp!sendUlineUserTid_c2act_chat.load(st!sendConceptToBranch_stat, uline_breed, userTid_tidprem_hcid);

    cp!circus1_anrn.addEffs(
        cast(DcpDsc[])[],
        [
            getUserInput_breed_getuln,
            circus1_andnrn,
        ]
    );

    cp!circus1_andnrn.addPrem(userInput_strprem_uline);
    cp!circus1_andnrn.addEffs(
        cast(DcpDsc[])[
            copyUserInputToOutput_c2act_uline,
            anactivateUserInput_cact_uline
        ],
        [
            putUserOutput_breed_putuln,
            circus1_anrn,
        ]
    );
    cp!copyUserInputToOutput_c2act_uline.load(st!copyPremise, userInput_strprem_uline, userOutput_strprem_uline);
}

/// User line branch enums
// 2_882_879_983, 2_631_252_357
enum Uline {
    /// uline branch identifier
    uline_breed = cd!(SpBreed, 4_021_308_401),

    /// uline branch seed
    uline_seed = cd!(SpActionNeuron, 1_771_384_341),

    /// wait until chat sends its breed and user's tid.
    shakeHandsWithChat_andnrn_uline = cd!(SpAndNeuron, 226_154_664),

    /// After the handshaking with chat uline has user tid and can send back its own
    sendUserUlineTid_c2act_uline = cd!(SpA_2Cid, 2_277_726_710),

    /// line of text from the user, string premise.
    userInput_strprem_uline = cd!(SpStringPrem, 3_622_010_989),
    anactivateUserInput_cact_uline = cd!(SpA_Cid, 1_733_678_366),

    /// Wait on this neuror for the next line of text from user.
    userInputValve_andnrn_uline = cd!(SpAndNeuron, 732_066_873),

    /// askUserline branch is ready to receive next user line.
    requestForUserInput_peg_uline = cd!(SpPegPrem, 1_906_470_662),
    anactivateRequestForUserInput_cact_uline = cd!(SpA_Cid, 409_329_855),

    /// Call stat action of moving line from buffer to string peg.
    popLineFromUserInuputBuffer_c2act_uline = cd!(SpA_2Cid, 2_949_480_003),

    /// Send user line to whatever branch the request came from (with set activation)
    sendUserInputToCaller_c2act_uline = cd!(SpA_2Cid, 3_447_310_214),

    /// Send user a prompt for the next input
    requestUserForNextInput_cact_uline = cd!(SpA_Cid, 1_439_958_318),

    /// Watever branch wants to send user a line of text sets up this concept along with activation.
    userOutput_strprem_uline = cd!(SpStringPrem, 3_186_686_771),
    anactivateUserOutput_cact_uline = cd!(SpA_Cid, 3_758_390_978),
}

/// Setup the uline branch.
void uline() {
    mixin(dequalify_enums!(HardCids, CommonConcepts, Chat, OutputUserLine, Uline));

    // Mate uline seed and breed and choose the start type.
    cp!uline_breed.load(uline_seed, null, null);

    // Setup the uline_seed
    cp!uline_seed.addEffs(
        cast(DcpDsc[])[
        ],
        shakeHandsWithChat_andnrn_uline       // branch
    );

    // Handshaker. The chat breed and the user thread tid will be sent by the chat branch, wait for them.
    // The uline breed will be set up in the chat name space.
    cp!shakeHandsWithChat_andnrn_uline.addPrem(chat_breed_hcid);
    cp!shakeHandsWithChat_andnrn_uline.addPrem(userTid_tidprem_hcid);
    cp!shakeHandsWithChat_andnrn_uline.addEffs(
        float.infinity,
        [   // acts
            sendUserUlineTid_c2act_uline,   // give user the means to communicate to this branch
        ],
        [
            outputUserLine_graft_oputuln,   // endless graft, forwarding outputs from outside to user
            userInputValve_andnrn_uline,    // the successor neuron
        ]
    );
    cp!sendUserUlineTid_c2act_uline.load(st!sendTidToUser_stat, userTid_tidprem_hcid, uline_breed);

        // User input valve. The handshake is over. Now, wait for user input and send it to the askUserLine branch in a cycle.
    // Premises
    cp!userInputValve_andnrn_uline.addPrem(userInputBuffer_strqprem_hcid);
    cp!userInputValve_andnrn_uline.addPrem(requestForUserInput_peg_uline);
    // Effects
    cp!userInputValve_andnrn_uline.addEffs(
        float.infinity,
        [   // acts
            popLineFromUserInuputBuffer_c2act_uline,
            sendUserInputToCaller_c2act_uline,
            anactivateRequestForUserInput_cact_uline,
            anactivateUserInput_cact_uline,
//            requestUserForNextInput_cact_uline,
        ],
        null
    );
    // acts
    cp!popLineFromUserInuputBuffer_c2act_uline.load(st!popUserInputLineFromBuffer_stat,
            userInputBuffer_strqprem_hcid, userInput_strprem_uline);
    cp!sendUserInputToCaller_c2act_uline.load(st!sendConceptToBranch_stat, callerTid_tidprem_hcid,
    userInput_strprem_uline);
    cp!anactivateRequestForUserInput_cact_uline.load(st!anactivate_stat, requestForUserInput_peg_uline);
    cp!anactivateUserInput_cact_uline.load(st!anactivate_stat, userInput_strprem_uline);
    cp!requestUserForNextInput_cact_uline.load(st!requestUserInput, userTid_tidprem_hcid);

    // Other
    cp!anactivateUserOutput_cact_uline.load(st!anactivate_stat, userOutput_strprem_uline);
}

/// Endless uline's graft, used in uline to forward lines of text from other branches to user. Works only in the uline
/// branch since it uses its concepts, e.g. local copy of the userTid_tidprem_hcid.
//1_086_012_647, 1_922_712_729, 2_281_377_307,
enum OutputUserLine {
    outputUserLine_graft_oputuln = cd!(SpGraft, 2_243_064_562),

    /// Waits for the userOutput_strprem_uline from an external branch and sends it to user
    valve_andnrn_oputuln = cd!(SpAndNeuron, 3_020_567_833),

    /// Send content of the userOutput_strprem_uline to user
    outputUserLine_c2act_oputuln = cd!(SpA_2Cid, 2_535_811_603),
}

void outputUserLine() {
    mixin(dequalify_enums!(HardCids, CommonConcepts, Uline, OutputUserLine));

    // Graft
    cp!outputUserLine_graft_oputuln.load(valve_andnrn_oputuln);

    // Seed
    cp!valve_andnrn_oputuln.addPrem(userOutput_strprem_uline);
    cp!valve_andnrn_oputuln.addEffs(
        float.infinity,
        [
            outputUserLine_c2act_oputuln,
            anactivateUserOutput_cact_uline
        ],
        null
    );
    cp!outputUserLine_c2act_oputuln.load(st!sendOutputToUser, userTid_tidprem_hcid, userOutput_strprem_uline);
}

// 580_962_659, 3_399_694_389, 2_877_036_599
enum GetUserline {
    getUserInput_breed_getuln = cd!(SpBreed, 188_095_368),
    seed_anrn_getuln = cd!(SpActionNeuron, 2_594_815_860),
    getUserInput_c2act_getuln = cd!(SpA_2Cid, 4_122_865_703),
    waitUlineResponse_andnrn_getuln = cd!(SpAndNeuron, 2_337_467_201),
}

void getUserline() {
    mixin(dequalify_enums!(HardCids, CommonConcepts, Uline, GetUserline));

    // Breed
    cp!getUserInput_breed_getuln.load(
        seed_anrn_getuln,                   // the seed to branch
        [uline_breed],                      // in params, will be injected into the branch by parent
        [userInput_strprem_uline]       // out params, will be injected back to parent's branch on finishing
    );

    // Seed - asks uline to give new user line
    cp!getUserInput_c2act_getuln.load(st!activateRemotely_stat, uline_breed, requestForUserInput_peg_uline);
    cp!seed_anrn_getuln.addEffs(
        cast(DcpDsc[])[
            getUserInput_c2act_getuln,
        ],
        waitUlineResponse_andnrn_getuln
    );

    // Wait for user line and finish the branch
    cp!waitUlineResponse_andnrn_getuln.addPrem(userInput_strprem_uline);
    cp!waitUlineResponse_andnrn_getuln.addEffs(
        float.infinity,
        cast(DcpDsc[])[
            stop_act,
        ],
        null,
    );
}

// 1_921_957_812, 2_266_507_232, 3_663_063_770, 3_011_995_072, 2_565_596_668
enum PutUserLine {
    putUserOutput_breed_putuln = cd!(SpBreed, 2_558_694_764),
    seed_anrn_putuln = cd!(SpActionNeuron, 2_076_317_570),
    putUserOutput_c2act_putuln = cd!(SpA_2Cid, 1_226_307_907),
}

void putUserLine() {
    mixin(dequalify_enums!(HardCids, CommonConcepts, PutUserLine, Uline));

    // breed
    cp!putUserOutput_breed_putuln.load(
        seed_anrn_putuln,
        [uline_breed, userOutput_strprem_uline],
        null
    );

    // seed
    cp!seed_anrn_putuln.addEffs(
        cast(DcpDsc[])[
            putUserOutput_c2act_putuln,
            stop_act
        ],
        null
    );
    cp!putUserOutput_c2act_putuln.load(st!sendConceptToBranch_stat, uline_breed, userOutput_strprem_uline);
}










