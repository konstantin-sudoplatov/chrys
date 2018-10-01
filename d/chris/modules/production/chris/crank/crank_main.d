/// Dynamic concept names
module crank_main;

import global_data, crank_registry;
import cpt_stat, cpt_neurons, cpt_premises, cpt_actions;
import stat_main;


/// Dynamic concept names.
enum CommonConcepts: CptDescriptor {

    // Service concepts
    logCpt_0_unact = cd!(SpUnaryAction, 246_390_338),       // log a concept, using concept.toString()
    logCpt_1_unact = cd!(SpUnaryAction, 1_005_527_366),     // ditto
    logCpt_2_unact = cd!(SpUnaryAction, 122_016_958),       // ditto
    zond_0_actnrn = cd!(SpActionNeuron, 2_279_163_875),     // test action neuron to inject into different points of workflow
    zond_1_actnrn = cd!(SpActionNeuron, 2_025_623_255),     // ditto
    zond_2_actnrn = cd!(SpActionNeuron, 1_321_617_741),     // ditto


    /// Controlling the debug level inside the caldron
    setDebugLevel_0_act = cd!(SpAction, 3_426_667_410),
    setDebugLevel_1_act = cd!(SpAction, 805_124_526),
    setDebugLevel_2_act = cd!(SpAction, 2_996_929_904),

    /// call current Caldron._requestStopAndWait_(), can be used by all caldrons
    stopAndWait_act = cd!(SpAction, 580_052_493),

    /// It is a very special and narrow case of concept. We have in it Tid of the thread, that maintains dialog with user.
    /// Not uline branch thread, not even a caldron. It is the thread that controls the console or http connection.
    /// The Tid is put in it on start of the chat caldron, and the primitive is valid only for chat branch,
    /// since the Tid field is stored in the live part of the concept.
    userThread_tidprem = cd!(SpTidPremise, 217_397_612),

    /// line of text from the user, string premise.
    userInputLine_strprem = cd!(SpStringPremise, 3_622_010_989),

    /// anactivate user input string premise
    anactivateUserInputLine_unact = cd!(SA_Cid, 1_733_678_366),
}

/// Setup common concepts.
void commonConcepts() {
    mixin(dequalify_enums!(CommonConcepts));

    // Setup the stop and wait action
    cpt!logCpt_0_unact.statAction = statCid!logConcept_stat;
    cpt!logCpt_1_unact.statAction = statCid!logConcept_stat;
    cpt!logCpt_2_unact.statAction = statCid!logConcept_stat;

    // Setup controlling the debug level
    cpt!setDebugLevel_0_act.load(statCid!setDebugLevel_0_stat);
    cpt!setDebugLevel_1_act.load(statCid!setDebugLevel_1_stat);
    cpt!setDebugLevel_2_act.load(statCid!setDebugLevel_2_stat);

    // Setup the stop and wait action
    cpt!stopAndWait_act.statAction = statCid!stopAndWait_stat;

    // Setup activation of the user input premise
    cpt!anactivateUserInputLine_unact.load(statCid!anactivate_stat, userInputLine_strprem);
}

/// Chat branch enums
/// , 3525361282, 3520033260
enum Chat: CptDescriptor {

    /// This is the root branch of the attention circle, is set up in the attention circle constructor.
    chat_breed = cd!(SpBreed, 1_719_007_030),

    /// This is the root branch of the attention circle. It heads the handshaker and spawns the uline
    chat_seed = cd!(SpSeed, 2_500_739_441),

    /// Setting up the uline branch (see actions below)
    /// After chat starts the uline branch, it sends user its own breed. Also it sends uline Tid of the user thread
    /// (console or http), so that uline could talk to user.
    shakeHandsWithUline_chat_actnrn = cd!(SpActionNeuron, 3_996_466_002),

    /// The action for the handshaker. After chat starts the uline branch, it sends user its own breed.
    sendUlineChatBreed_chat_binact = cd!(SpBinaryAction, 553_436_801),

    /// The action for the handshaker. It sends uline Tid of the user thread (console or http), so that uline could be able
    /// to talk to the user.
    sendUlineUserTid_chat_binact = cd!(SpBinaryAction, 3_408_832_589),

    /// A valve for waiting for a line of text from uline, which it gets from user with tools
    valveOnUlineInput_chat_andnrn = cd!(SpAndNeuron, 497_144_117),
    activateRemotely_readyForUlineInput_chat_binact = cd!(SpBinaryAction, 3_702_223_557),

}

/// Setup the chat branch.
void chatBranch() {
    mixin(dequalify_enums!(CommonConcepts, Chat, Uline));    // anonymizes the concept enums, so we don't need use their full names.

    // Setup the breed and seed
    cpt!chat_breed.load(chat_seed);
    cpt!chat_seed.addEffects(
        //[   // acts
        //],
        null,
        [   // brans
            shakeHandsWithUline_chat_actnrn,    // handshake with uline
            uline_breed                         // start uline branch
        ]
    );

    // Handshake with uline
    cpt!sendUlineChatBreed_chat_binact.load(statCid!sendConceptToBranch_stat, uline_breed, chat_breed);
    cpt!sendUlineUserTid_chat_binact.load(statCid!sendConceptToBranch_stat, uline_breed, userThread_tidprem);
    cpt!shakeHandsWithUline_chat_actnrn.addEffects(
        [   // acts
            sendUlineChatBreed_chat_binact,       // give uline own breed
            sendUlineUserTid_chat_binact,           // give uline user's Tid
            activateRemotely_readyForUlineInput_chat_binact,    // tell to uline send the next line
        ],
        valveOnUlineInput_chat_andnrn
    );

    // Wait on the user input
    cpt!activateRemotely_readyForUlineInput_chat_binact.
            load(statCid!activateRemotely_stat, uline_breed, chatReadyForUlineInputPeg_uline_pegprem);
    cpt!valveOnUlineInput_chat_andnrn.addPremises(userInputLine_strprem);
    cpt!valveOnUlineInput_chat_andnrn.addEffects(
        float.infinity,
        [
            anactivateUserInputLine_unact,
            activateRemotely_readyForUlineInput_chat_binact
        ],
        null
    );
}

// User line branch enums
/// , 643414724, 2821656862, 3589523171, 145413872 , 4278173576
enum Uline {

    /// in this buffer the attention circle thread puts user lines of text, where they wait to get processed
    userInputBuffer_uline_strqprem = cd!(SpStringQueuePremise, 1_079_824_511),

    /// uline branch identifier
    uline_breed = cd!(SpBreed, 4_021_308_401),

    /// uline branch seed
    uline_seed = cd!(SpSeed, 1_771_384_341),

    /// wait until chat sends its breed and user's tid.
    shakeHandsWithChat_uline_anrn = cd!(SpAndNeuron, 226_154_664),

    /// After the handshaking with chat uline has user tid and can send back its own
    sendUserUlineTid_uline_binact = cd!(SpBinaryAction, 2_277_726_710),

    /// wait neuron. Wait on it for the next line of text from user.
    userInputValve_uline_andnrn = cd!(SpAndNeuron, 732_066_873),

    /// Flag for uline to feed the next input from user to chat and anactivation action for it
    chatReadyForUlineInputPeg_uline_pegprem = cd!(SpPegPremise, 1_456_194_005),
    anactivateChatReadyForUlineInputPeg_uline_unact = cd!(SpUnaryAction, 409_329_855),

    /// Call stat action of moving line from buffer to string peg.
    moveLineFromUserInuputBufferToUserInputLine_uline_binact = cd!(SpBinaryAction, 2_949_480_003),

    /// Send user line premise to chat (together with the activation value)
    sendUserInputLineToChat_uline_binact = cd!(SpBinaryAction, 3_447_310_214),

    /// Send user a prompt for the next input
    sendUserRequestForNextLine_uline_unact = cd!(SpUnaryAction, 1_439_958_318),
}

/// Setup the uline branch.
void ulineBranch() {
    mixin(dequalify_enums!(CommonConcepts, Uline, Chat));


    // Mate uline seed and breed.
    cpt!uline_breed.load(uline_seed);

    // Setup the uline_seed
    cpt!uline_seed.addEffects(
        null,
        shakeHandsWithChat_uline_anrn       // branch
    );

    // Handshaker. The chat breed and the user thread tid will be sent by the chat branch, wait for them.
    // The uline breed will be set up in the chat name space.
    cpt!sendUserUlineTid_uline_binact.load(statCid!sendTidToUser_stat, userThread_tidprem, uline_breed);
    cpt!shakeHandsWithChat_uline_anrn.addPremises([
        chat_breed,
        userThread_tidprem
    ]);
    cpt!shakeHandsWithChat_uline_anrn.addEffects(
        float.infinity,
        [   // acts
            sendUserUlineTid_uline_binact,
        ],
        userInputValve_uline_andnrn
    );

    // User input valve. The handshake is over. Now, wait for user input and send it to chat, in a cycle.
    cpt!moveLineFromUserInuputBufferToUserInputLine_uline_binact.load(statCid!getUserInputLineFromBuffer_stat,
            userInputBuffer_uline_strqprem, userInputLine_strprem);
    cpt!sendUserInputLineToChat_uline_binact.load(statCid!sendConceptToBranch_stat, chat_breed, userInputLine_strprem);
    cpt!anactivateChatReadyForUlineInputPeg_uline_unact.load(statCid!anactivate_stat, chatReadyForUlineInputPeg_uline_pegprem);
    cpt!sendUserRequestForNextLine_uline_unact.load(statCid!requestUserInput, userThread_tidprem);
    cpt!userInputValve_uline_andnrn.addPremises([
        userInputBuffer_uline_strqprem,
        chatReadyForUlineInputPeg_uline_pegprem
    ]);
    cpt!userInputValve_uline_andnrn.addEffects(
        float.infinity,
        [
            moveLineFromUserInuputBufferToUserInputLine_uline_binact,
            sendUserInputLineToChat_uline_binact,
            anactivateChatReadyForUlineInputPeg_uline_unact,
            anactivateUserInputLine_unact,
            sendUserRequestForNextLine_uline_unact,
        ],
        null
    );
}

















