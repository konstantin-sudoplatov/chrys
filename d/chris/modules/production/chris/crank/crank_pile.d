/// Dynamic concept names
module crank_pile;

import global, tools;
import cpt_pile, cpt_neurons, cpt_premises, cpt_actions;
import stat_pile;


/// Dynamic concept names.
enum CommonConcepts: CptDescriptor {

    logCpt_unact =
            cd!(SpUnaryAction, 246_390_338),

    /// call current Caldron._requestStopAndWait_(), can be used by all caldrons
    stopAndWait_act =
            cd!(SpAction, 580_052_493),

    /// It is a very special and narrow case of concept. We have in it Tid of the thread, that maintains dialog with user.
    /// Not uline branch thread, not even a caldron. It is the thread that controls the console or http connection.
    /// The Tid is put in it on start of the chat caldron, and the primitive is valid only for chat branch,
    /// since the Tid field is stored in the live part of the concept.
    //TODO: continue from here - fill in the breed and make a binop action to pass it to the stat concept.
    userThread_tidprim =
            cd!(SpTidPrimitive, 217_397_612),
    //
    //// It stores Tid of the current caldron and is filled on the start of the caldron. You can always get your Tid using
    //// std.concurrency.thisTid(), but, if you need to send your Tid to another branch on the conceptual level use this.
    //myTid_tidprim =
    //        cd!(SpTidPrimitive, 1_005_527_366),
}

void _commonConcepts_() {
    mixin(dequalify_enums!(CommonConcepts));

    // Setup the stop and wait action
    cpt!logCpt_unact.statAction = _statCid_!logConcept;

    // Setup the stop and wait action
    cpt!stopAndWait_act.statAction = _statCid_!_stopAndWait_;
}

/// Chat branch enums
/// 553436801, 122016958, 3426667410, 3926428957, 805124526, 2996929904
enum Chat: CptDescriptor {
    chat_breed =                    /// this is the root branch of the attention circle, is set up at the circle start
            cd!(SpBreed, 1_719_007_030),
    chat_seed =                     /// this is the root branch of the attention circle
            cd!(SpSeed, 2_500_739_441),
    ulineHandShaker_actnrn =        /// after the seed starts uline branch, it sends user its Tid, so user could talk to it
            cd!(SpActionNeuron, 3_996_466_002),
    chatSendsUserUlineTid_unact =    /// it is the action for the previous neuron
            cd!(SpUnaryAction, 3_408_832_589),
    ulineValve_andnrn =             /// wait on this neuron for the next line from the user line branch
            cd!(SpAndNeuron, 497_144_117),
}

/// Setup the chat branch.
void _chatBranch_() {
    mixin(dequalify_enums!(CommonConcepts, Chat, Uline));    // anonymizes the concept enums, so we don't need use their full names.

    // Setup the breed and seed
    cpt!chat_breed.seed = chat_seed;
cpt!logCpt_unact._operand_ = uline_breed;
    cpt!chat_seed.add_effects(logCpt_unact, [ulineHandShaker_actnrn, uline_breed]);

    // Handshake with user
    cpt!ulineHandShaker_actnrn.add_effects(chatSendsUserUlineTid_unact, ulineValve_andnrn);
    cpt!chatSendsUserUlineTid_unact.statAction = _statCid_!sendConceptToBranch;
    cpt!chatSendsUserUlineTid_unact._operand_ = uline_breed;

    // Wait on the user input
    cpt!ulineValve_andnrn.add_effects(float.infinity, stopAndWait_act, null);
}

// User line branch enums
/// 226154664, 2277726710, 1439958318, 1079824511, 4278173576
enum Uline {
    uline_breed =                   /// uline branch identifier
            cd!(SpBreed, 4_021_308_401),
    uline_seed =                    /// uline branch seed
            cd!(SpSeed, 1_771_384_341),
    userInput_valve_anrn =          /// wait neuron. Wait on it for the next line of text from user.
            cd!(SpAndNeuron, 732_066_873),
    userInput_strprem =             /// line of text from the user, string premise.
            cd!(SpStringPremise, 3_622_010_989),
    activate_userInputPrem_unact =   /// activate user input string premise
            cd!(SpUnaryAction, 2_228_223_070),
    anactivate_userInputPrem_unact = /// anactivate user input string premise
            cd!(SpUnaryAction, 1_733_678_366),
}

/// Setup the uline branch.
void _ulineBranch_() {
    mixin(dequalify_enums!(CommonConcepts, Uline, Chat));

    // Setup actions
    cpt!activate_userInputPrem_unact.statAction = _statCid_!activateStat;
    cpt!activate_userInputPrem_unact._operand_ = userInput_strprem;
    cpt!anactivate_userInputPrem_unact.statAction = _statCid_!anactivateStat;
    cpt!anactivate_userInputPrem_unact._operand_ = userInput_strprem;

    // Mate uline seed and breed. Make the parent chat breed known to the uline breed. In the process of creating uline
    // caldron the live instance of chat breed will be setup for it, so uline will be able to send messages to chat.
    // And vice versa, the uline breed will be set up in the chat name space.
    cpt!uline_breed.seed = uline_seed;

    // Setup the uline_seed
    cpt!uline_seed.add_effects(anactivate_userInputPrem_unact, userInput_valve_anrn);
}

















