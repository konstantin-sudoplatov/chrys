/// Dynamic concept names
module crank.crank_main;

import proj_data;

import chri_types, chri_data;
import crank.crank_types, crank.crank_registry;
import cpt.cpt_actions, cpt.cpt_neurons, cpt.cpt_premises;

import stat.stat_types, stat.stat_main;

/// Dynamic concept names and cids.
// 2_295_052_561, 2_421_473_041, 3_679_431_450, 33_533_622, 2_142_584_142, 3_372_907_570, 2_800_603_496, 3_786_801_661
enum CommonConcepts: DcpDescriptor {

    // Service concepts
    checkUp_act = cd!(SpA, 3_525_361_282),              // raise the CheckPt_ flag of the caldron
    logCpt_0_cact = cd!(SpA_Cid, 246_390_338),          // log a concept, using concept.toString()
    logCpt_1_cact = cd!(SpA_Cid, 1_005_527_366),        // ditto
    logCpt_2_cact = cd!(SpA_Cid, 122_016_958),          // ditto
    zond_0_anrn = cd!(SpActionNeuron, 2_279_163_875),       // test action neuron to inject into different points of workflow
    zond_1_anrn = cd!(SpActionNeuron, 2_025_623_255),       // ditto
    zond_2_anrn = cd!(SpActionNeuron, 1_321_617_741),       // ditto


    /// Controlling the debug level inside the caldron
    setDebugLevel_0_act = cd!(SpA, 3_426_667_410),
    setDebugLevel_1_act = cd!(SpA, 805_124_526),
    setDebugLevel_2_act = cd!(SpA, 2_996_929_904),

    /// call current Caldron._requestStopAndWait_(), can be used by all caldrons
    wait_act = cd!(SpA, 580_052_493),
    stop_act = cd!(SpA, 3_520_033_260),

    /// line of text from the user, string premise.
    userInputLine_strprem = cd!(SpStringPrem, 3_622_010_989),

    /// anactivate user input string premise
    anactivateUserInputLine_cact = cd!(SpA_Cid, 1_733_678_366),
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

    // Setup activation of the user input premise
    cp!anactivateUserInputLine_cact.load(statCid!anactivate_stat, userInputLine_strprem);
}

/// Chat branch enums
// 3_567_444_531, 2_650_964_728, 2_888_019_240, 2_383_825_757, 3_505_369_639, 4_220_759_348, 1_278_962_165, 49_787_120
enum Chat: DcpDescriptor {

    /// This is the root branch of the attention circle. It heads the handshaker and spawns the uline
    chat_seed = cd!(SpSeed, 2_500_739_441),

    /// Setting up the uline branch (see actions below)
    /// After chat starts the uline branch, it sends user its own breed. Also it sends uline Tid of the user thread
    /// (console or http), so that uline could talk to user.
    shakeHandsWithUline_andnrn_chat = cd!(SpAndNeuron, 3_996_466_002),

    /// The action for the handshaker. After chat starts the uline branch, it sends user its own breed.
    sendUlineChatBreed_c2act_chat = cd!(SpA_CidCid, 553_436_801),

    /// The action for the handshaker. It sends uline Tid of the user thread (console or http), so that uline could be able
    /// to talk to the user.
    sendUlineUserTid_c2act_chat = cd!(SpA_CidCid, 3_408_832_589),

    /// A valve for waiting for a line of text from uline, which it gets from user with tools
    valveOnUlineInput_andnrn_chat = cd!(SpAndNeuron, 497_144_117),
    activateRemotely_readyForUlineInput_c2act_chat = cd!(SpA_CidCid, 3_702_223_557),

}

/// Setup the chat branch.
void chatBranch() {
    mixin(dequalify_enums!(HardCid, CommonConcepts, Chat, Uline));    // anonymizes the concept enums, so we don't need use their full names.

    // Setup the breed and seed
    cp!chatBreed_breed_hcid.load(chat_seed);
    cp!chat_seed.addEffects(
        //[   // acts
        //],
        null,
        [   // brans
            shakeHandsWithUline_andnrn_chat,    // handshake with uline
            uline_breed                         // start uline branch
        ]
    );

    // Handshake with uline
        //prems
    cp!shakeHandsWithUline_andnrn_chat.addPremises(userTid_tidprem_hcid);
        // acts
    cp!sendUlineChatBreed_c2act_chat.load(statCid!sendConceptToBranch_stat, uline_breed, chatBreed_breed_hcid);
    cp!sendUlineUserTid_c2act_chat.load(statCid!sendConceptToBranch_stat, uline_breed, userTid_tidprem_hcid);
    cp!activateRemotely_readyForUlineInput_c2act_chat.load(statCid!activateRemotely_stat, uline_breed,
    chatReadyForUlineInputPeg_pegprem_uline);
        // nrn
    cp!shakeHandsWithUline_andnrn_chat.addEffects(
        float.infinity,
        [   // acts
            sendUlineChatBreed_c2act_chat,       // give uline own breed
            sendUlineUserTid_c2act_chat,         // give uline user's Tid
            activateRemotely_readyForUlineInput_c2act_chat,    // tell uline to send next line
        ],
        valveOnUlineInput_andnrn_chat
    );

    // Wait on the user input
    cp!valveOnUlineInput_andnrn_chat.addPremises(userInputLine_strprem);
    cp!valveOnUlineInput_andnrn_chat.addEffects(
        float.infinity,
        [
            anactivateUserInputLine_cact,
            activateRemotely_readyForUlineInput_c2act_chat
        ],
        null
    );
}

// User line branch enums
// 4_122_865_703, 2_594_815_860, 188_095_368, 254_056_846, 1_906_470_662, 3_186_686_771, 1_099_498_783, 3_758_390_978
enum Uline {
    /// uline branch identifier
    uline_breed = cd!(SpBreed, 4_021_308_401),

    /// uline branch seed
    uline_seed = cd!(SpSeed, 1_771_384_341),

    /// wait until chat sends its breed and user's tid.
    shakeHandsWithChat_andnrn_uline = cd!(SpAndNeuron, 226_154_664),

    /// After the handshaking with chat uline has user tid and can send back its own
    sendUserUlineTid_c2act_uline = cd!(SpA_CidCid, 2_277_726_710),

    /// wait neuron. Wait on it for the next line of text from user.
    userInputValve_andnrn_uline = cd!(SpAndNeuron, 732_066_873),

    /// Flag for uline to feed the next input from user to chat and anactivation action for it
    chatReadyForUlineInputPeg_pegprem_uline = cd!(SpPegPrem, 1_456_194_005),
    anactivateChatReadyForUlineInputPeg_cact_uline = cd!(SpA_Cid, 409_329_855),

    /// Call stat action of moving line from buffer to string peg.
    moveLineFromUserInuputBufferToUserInputLine_c2act_uline = cd!(SpA_CidCid, 2_949_480_003),

    /// Send user line premise to chat (together with the activation value)
    sendUserInputLineToChat_c2act_uline = cd!(SpA_CidCid, 3_447_310_214),

    /// Send user a prompt for the next input
    sendUserRequestForNextLine_cact_uline = cd!(SpA_Cid, 1_439_958_318),
}

/// Setup the uline branch.
void ulineBranch() {
    mixin(dequalify_enums!(HardCid, CommonConcepts, Uline, Chat));


    // Mate uline seed and breed.
    cp!uline_breed.load(uline_seed);

    // Setup the uline_seed
    cp!uline_seed.addEffects(
        null,
        shakeHandsWithChat_andnrn_uline       // branch
    );

    // Handshaker. The chat breed and the user thread tid will be sent by the chat branch, wait for them.
    // The uline breed will be set up in the chat name space.
    cp!sendUserUlineTid_c2act_uline.load(statCid!sendTidToUser_stat, userTid_tidprem_hcid, uline_breed);
    cp!shakeHandsWithChat_andnrn_uline.addPremises([
        chatBreed_breed_hcid,
        userTid_tidprem_hcid
    ]);
    cp!shakeHandsWithChat_andnrn_uline.addEffects(
        float.infinity,
        [   // acts
            sendUserUlineTid_c2act_uline,
        ],
        userInputValve_andnrn_uline
    );


    // User input valve. The handshake is over. Now, wait for user input and send it to chat, in a cycle.
        // Premises
    cp!userInputValve_andnrn_uline.addPremises([
        userInputBuffer_strqprem_hcid,
        chatReadyForUlineInputPeg_pegprem_uline
    ]);
        // Actions
    cp!moveLineFromUserInuputBufferToUserInputLine_c2act_uline.load(statCid!getUserInputLineFromBuffer_stat,
            userInputBuffer_strqprem_hcid, userInputLine_strprem);
    cp!sendUserInputLineToChat_c2act_uline.load(statCid!sendConceptToBranch_stat, chatBreed_breed_hcid, userInputLine_strprem);
    cp!anactivateChatReadyForUlineInputPeg_cact_uline.load(statCid!anactivate_stat, chatReadyForUlineInputPeg_pegprem_uline);
    cp!sendUserRequestForNextLine_cact_uline.load(statCid!requestUserInput, userTid_tidprem_hcid);
        // Effects
    cp!userInputValve_andnrn_uline.addEffects(
        float.infinity,
        [
            moveLineFromUserInuputBufferToUserInputLine_c2act_uline,
            sendUserInputLineToChat_c2act_uline,
            anactivateChatReadyForUlineInputPeg_cact_uline,
            anactivateUserInputLine_cact,
            sendUserRequestForNextLine_cact_uline,
        ],
        null
    );
}

















