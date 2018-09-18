/// Dynamic concept names
module crank_pile;

import global, tools;
import cpt_pile, cpt_neurons, cpt_premises, cpt_actions;
import stat_pile;


/// Dynamic concept names.
enum CommonConcepts: CptDescriptor {

    // Service concepts
    logCpt_0_unact = cd!(SpUnaryAction, 246_390_338),
    logCpt_1_unact = cd!(SpUnaryAction, 1_005_527_366),
    logCpt_2_unact = cd!(SpUnaryAction, 122_016_958),

    /// call current Caldron._requestStopAndWait_(), can be used by all caldrons
    stopAndWait_act = cd!(SpAction, 580_052_493),

    /// It is a very special and narrow case of concept. We have in it Tid of the thread, that maintains dialog with user.
    /// Not uline branch thread, not even a caldron. It is the thread that controls the console or http connection.
    /// The Tid is put in it on start of the chat caldron, and the primitive is valid only for chat branch,
    /// since the Tid field is stored in the live part of the concept.
    userThread_tidprem = cd!(SpTidPremise, 217_397_612),
}

void _commonConcepts_() {
    mixin(dequalify_enums!(CommonConcepts));

    // Setup the stop and wait action
    cpt!logCpt_0_unact.statAction = statCid!logConcept;
    cpt!logCpt_1_unact.statAction = statCid!logConcept;
    cpt!logCpt_2_unact.statAction = statCid!logConcept;

    // Setup the stop and wait action
    cpt!stopAndWait_act.statAction = statCid!_stopAndWait_;
}

/// Chat branch enums
/// , , 3426667410, 3926428957, 805124526, 2996929904
enum Chat: CptDescriptor {

    /// This is the root branch of the attention circle, is set up in the attention circle constructor.
    chat_breed = cd!(SpBreed, 1_719_007_030),

    /// This is the root branch of the attention circle. It heads the handshaker and spawns the uline
    chat_seed = cd!(SpSeed, 2_500_739_441),

    /// Setting up the uline branch (see actions below)
    /// After chat starts the uline branch, it sends user its own breed. Also it sends uline Tid of the user thread
    /// (console or http), so that uline could talk to user.
    chatShakesHandsWithUline_actnrn = cd!(SpActionNeuron, 3_996_466_002),

    /// The action for the handshaker. After chat starts the uline branch, it sends user its own breed.
    chatSendsUlineItsOwnBreed_binact = cd!(SpBinaryAction, 553_436_801),

    /// The action for the handshaker. It sends uline Tid of the user thread (console or http), so that uline could be able
    /// to talk to the user.
    chatSendsUlineUserTid_binact = cd!(SpBinaryAction, 3_408_832_589),

    /// A valve for waiting for a line of text from uline, which it gets from user.
    ulineValve_andnrn = cd!(SpAndNeuron, 497_144_117),

}

/// Setup the chat branch.
void _chatBranch_() {
    mixin(dequalify_enums!(CommonConcepts, Chat, Uline));    // anonymizes the concept enums, so we don't need use their full names.

    // Setup the breed and seed
    cpt!chat_breed.seed = chat_seed;
    cpt!chat_seed.addEffects(null, [chatShakesHandsWithUline_actnrn, uline_breed]);

    // Handshake with uline
    cpt!chatShakesHandsWithUline_actnrn.addEffects(
        [   // acts
            chatSendsUlineItsOwnBreed_binact,       // give uline own breed
            chatSendsUlineUserTid_binact            // give uline user's Tid
        ],
        ulineValve_andnrn
    );

    // actions
    cpt!chatSendsUlineItsOwnBreed_binact.load(statCid!sendConceptToBranch, uline_breed, chat_breed);
    cpt!chatSendsUlineUserTid_binact.load(statCid!sendConceptToBranch, uline_breed, userThread_tidprem);

    // Wait on the user input
    cpt!ulineValve_andnrn.addEffects(float.infinity, stopAndWait_act, null);
}

// User line branch enums
/// , , 1439958318, 1079824511, 4278173576
enum Uline {

    /// uline branch identifier
    uline_breed = cd!(SpBreed, 4_021_308_401),

    /// uline branch seed
    uline_seed = cd!(SpSeed, 1_771_384_341),

    /// wait until chat sends its breed and user's tid.
    ulineShakesHandsWithChat_anrn = cd!(SpAndNeuron, 226_154_664),

    /// After the handshaking with chat uline has user tid and can send back its own
    ulineSendsUserItsTid_binact = cd!(SpBinaryAction, 2_277_726_710),

    /// line of text from the user, string premise.
    userInput_strprem = cd!(SpStringPremise, 3_622_010_989),

    /// wait neuron. Wait on it for the next line of text from user.
    userInputValve_anrn = cd!(SpAndNeuron, 732_066_873),

    /// activate user input string premise
    activateUserInputPrem_unact = cd!(SpUnaryAction, 2_228_223_070),

    /// anactivate user input string premise
    anactivateUserInputPrem_unact = cd!(SpUnaryAction, 1_733_678_366),

}

/// Setup the uline branch.
void _ulineBranch_() {
    mixin(dequalify_enums!(CommonConcepts, Uline, Chat));

    // Load actions
    cpt!activateUserInputPrem_unact.load(statCid!activateStat, userInput_strprem);
    cpt!anactivateUserInputPrem_unact.load(statCid!anactivateStat, userInput_strprem);

    // Mate uline seed and breed.
    cpt!uline_breed.seed = uline_seed;

    // Setup the uline_seed
    cpt!uline_seed.addEffects(
        anactivateUserInputPrem_unact,      // act
        ulineShakesHandsWithChat_anrn       // branch
    );

    // Handshaker. The chat breed and the user thread tid will be sent by the chat branch, wait for them.
    // The uline breed will be set up in the chat name space.
    cpt!ulineShakesHandsWithChat_anrn.addPremise([
        chat_breed,
        userThread_tidprem
    ]);
    cpt!ulineShakesHandsWithChat_anrn.addEffects(
        0,
        stopAndWait_act,
        null
    );
    cpt!ulineShakesHandsWithChat_anrn.addEffects(
        float.infinity,
        [   // acts
            ulineSendsUserItsTid_binact,
        ],
        userInputValve_anrn
    );
    cpt!ulineSendsUserItsTid_binact.load(statCid!sendTidToUser, userThread_tidprem, uline_breed);

    // User input valve. The handshake is over. Now, wait for user input and send it to chat, in cycle.
    cpt!userInputValve_anrn.addEffects(
        0,
        null,
        null
    );
}

















