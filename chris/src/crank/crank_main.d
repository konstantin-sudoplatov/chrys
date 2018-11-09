/// Dynamic concept names
module crank.crank_main;

import proj_data;

import chri_types, chri_data;
import crank.crank_types, crank.crank_registry;
import cpt.cpt_actions, cpt.cpt_neurons, cpt.cpt_premises, cpt.cpt_primitives;

import stat.stat_types, stat.stat_main;

/// Dynamic concept names and cids.
// 3_372_907_570, 2_800_603_496, 3_786_801_661, 4_220_759_348, 1_278_962_165, 49_787_120
enum CommonConcepts: DcpDsc {

    // Service concepts
    checkUp_act = cd!(SpA, 3_525_361_282),              // raise the CheckPt_ flag of the caldron
    logCpt_0_cact = cd!(SpA_Cid, 246_390_338),          // log a concept, using concept.toString()
    logCpt_1_cact = cd!(SpA_Cid, 1_005_527_366),        // ditto
    logCpt_2_cact = cd!(SpA_Cid, 122_016_958),          // ditto
    zond_0_anrn = cd!(SpActionNeuron, 2_279_163_875),       // test action neuron to inject into different points of workflow
    zond_1_anrn = cd!(SpActionNeuron, 2_025_623_255),       // ditto
    zond_2_anrn = cd!(SpActionNeuron, 1_321_617_741),       // ditto
    circus_anrn = cd!(SpActionNeuron, 33_533_622),          // an endless waiting
    circus_andnrn = cd!(SpAndNeuron, 2_142_584_142),        // ditto

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
    cp!checkUp_act.load(statCid!checkUp_stat);

    // Setup the log actions
    cp!logCpt_0_cact.load(statCid!logConcept_stat);
    cp!logCpt_1_cact.load(statCid!logConcept_stat);
    cp!logCpt_2_cact.load(statCid!logConcept_stat);

    // Setup controlling the debug level
    cp!setDebugLevel_0_act.load(statCid!setDebugLevel_0_stat);
    cp!setDebugLevel_1_act.load(statCid!setDebugLevel_1_stat);
    cp!setDebugLevel_2_act.load(statCid!setDebugLevel_2_stat);

    // Setup the stop and wait actions
    cp!wait_act.load(statCid!wait_stat);
    cp!stop_act.load(statCid!stop_stat);
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
}

/// Setup the chat branch.
void chat() {
    mixin(dequalify_enums!(HardCid, CommonConcepts, Chat, Uline, GetUserline, PutUserLine));    // anonymizes the concept enums, so we don't need use their full names.

    // Setup the breed and seed
    cp!chat_breed_hcid.load(threadStartType_mark_hcid, chat_seed, null, null);
    cp!chat_seed.addEffs(
        null,
        [   // brans
            shakeHandsWithUline_andnrn_chat,    // handshake with uline
            uline_breed                         // start uline branch
        ]
    );

    // Handshake with uline
    cp!shakeHandsWithUline_andnrn_chat.addEffs(
        float.infinity,
        [   // acts
            sendUlineChatBreed_c2act_chat,       // give uline own breed
            sendUlineUserTid_c2act_chat,         // give uline user's Tid
        ],
        [
            getUserLine_breed_getuln,
            circus_andnrn,
        ]
    );
    //prems
    cp!shakeHandsWithUline_andnrn_chat.addPrem(userTid_tidprem_hcid);
    // acts
    cp!sendUlineChatBreed_c2act_chat.load(statCid!sendConceptToBranch_stat, uline_breed, chat_breed_hcid);
    cp!sendUlineUserTid_c2act_chat.load(statCid!sendConceptToBranch_stat, uline_breed, userTid_tidprem_hcid);

    cp!circus_andnrn.addPrem(userInputLine_strprem_uline);
    cp!circus_andnrn.addEffs(
        float.infinity,
        cast(DcpDsc[])[
        ],
        putUserLine_breed_putuln
    );
}

/// User line branch enums
// 3_186_686_771, 1_099_498_783, 3_758_390_978
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
    userInputLine_strprem_uline = cd!(SpStringPrem, 3_622_010_989),
    anactivateUserInputLine_cact_uline = cd!(SpA_Cid, 1_733_678_366),

    /// Wait on this neuror for the next line of text from user.
    userInputValve_andnrn_uline = cd!(SpAndNeuron, 732_066_873),

    /// askUserline branch is ready to receive next user line.
    requestForUserLine_peg_uline = cd!(SpPegPrem, 1_906_470_662),
    anactivateRequestForUserLine_cact_uline = cd!(SpA_Cid, 409_329_855),

    /// Call stat action of moving line from buffer to string peg.
    popLineFromUserInuputBufferToUserInputLine_c2act_uline = cd!(SpA_2Cid, 2_949_480_003),

    /// Send user line premise to chat (together with the activation value)
    sendUserInputLineToChat_c2act_uline = cd!(SpA_2Cid, 3_447_310_214),

    /// Send user a prompt for the next input
    sendUserRequestForNextLine_cact_uline = cd!(SpA_Cid, 1_439_958_318),
}

/// Setup the uline branch.
void uline() {
    mixin(dequalify_enums!(HardCid, CommonConcepts, Uline, Chat));

    // Mate uline seed and breed and choose the start type.
    cp!uline_breed.load(threadStartType_mark_hcid, uline_seed, null, null);

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
            userInputValve_andnrn_uline,    // the successor neuron
        ]
    );
    cp!sendUserUlineTid_c2act_uline.load(statCid!sendTidToUser_stat, userTid_tidprem_hcid, uline_breed);

        // User input valve. The handshake is over. Now, wait for user input and send it to the askUserLine branch in a cycle.
    // Premises
    cp!userInputValve_andnrn_uline.addPrem(userInputBuffer_strqprem_hcid);
    cp!userInputValve_andnrn_uline.addPrem(requestForUserLine_peg_uline);
    // Effects
    cp!userInputValve_andnrn_uline.addEffs(
        float.infinity,
        [   // acts
            popLineFromUserInuputBufferToUserInputLine_c2act_uline,
            sendUserInputLineToChat_c2act_uline,
            anactivateRequestForUserLine_cact_uline,
            anactivateUserInputLine_cact_uline,
            sendUserRequestForNextLine_cact_uline,
        ],
        null
    );
    // acts
    cp!popLineFromUserInuputBufferToUserInputLine_c2act_uline.load(statCid!popUserInputLineFromBuffer_stat,
            userInputBuffer_strqprem_hcid, userInputLine_strprem_uline);
    cp!sendUserInputLineToChat_c2act_uline.load(statCid!sendConceptToBranch_stat, callerTid_tidprem_hcid,
    userInputLine_strprem_uline);
    cp!anactivateRequestForUserLine_cact_uline.load(statCid!anactivate_stat, requestForUserLine_peg_uline);
    cp!anactivateUserInputLine_cact_uline.load(statCid!anactivate_stat, userInputLine_strprem_uline);
    cp!sendUserRequestForNextLine_cact_uline.load(statCid!requestUserInput, userTid_tidprem_hcid);
}

// 580_962_659, 3_399_694_389, 2_877_036_599
enum GetUserline {
    getUserLine_breed_getuln = cd!(SpBreed, 188_095_368),
    seed_anrn_getuln = cd!(SpActionNeuron, 2_594_815_860),
    getUserline_c2act_getuln = cd!(SpA_2Cid, 4_122_865_703),
    waitUlineResponse_andnrn_getuln = cd!(SpAndNeuron, 2_337_467_201),
}

void getUserline() {
    mixin(dequalify_enums!(HardCid, CommonConcepts, Uline, GetUserline));

    // Breed
    cp!getUserLine_breed_getuln.load(
        threadStartType_mark_hcid,          // branch by thread or fiber?
        seed_anrn_getuln,                   // the seed to branch
        [uline_breed],                      // in params, will be injected into the branch by parent
        [userInputLine_strprem_uline]       // out params, will be injected back to parent's branch on finishing
    );

    // Seed - asks uline to give new user line
    cp!getUserline_c2act_getuln.load(statCid!activateRemotely_stat, uline_breed, requestForUserLine_peg_uline);
    cp!seed_anrn_getuln.addEffs(
        cast(DcpDsc[])[
            getUserline_c2act_getuln,
        ],
        waitUlineResponse_andnrn_getuln
    );

    // Wait for user line and finish the branch
    cp!waitUlineResponse_andnrn_getuln.addPrem(userInputLine_strprem_uline);
    cp!waitUlineResponse_andnrn_getuln.addEffs(
        float.infinity,
        cast(DcpDsc[])[
            stop_act,
        ],
        null,
    );
}

//, , , 1_921_957_812, 2_266_507_232, 3_663_063_770, 3_011_995_072, 2_565_596_668
enum PutUserLine {
    putUserLine_breed_putuln = cd!(SpBreed, 2_558_694_764),
    seed_anrn_putuln = cd!(SpActionNeuron, 2_076_317_570),
    putUserLine_c2act_putuln = cd!(SpA_2Cid, 1_226_307_907),
}

void putUserLine() {
    mixin(dequalify_enums!(HardCid, CommonConcepts, PutUserLine, Uline));

    // breed
    cp!putUserLine_breed_putuln.load(
        threadStartType_mark_hcid,
        seed_anrn_putuln,
        [userInputLine_strprem_uline],
        null
    );

    // seed
    cp!seed_anrn_putuln.addEffs(
        cast(DcpDsc[])[
        ],
        null
    );
}








