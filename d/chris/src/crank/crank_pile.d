/// Dynamic concept names
module crank_pile;

import global, tools;
import cpt_pile, cpt_actions;
import stat_pile;


/// Dynamic concept names.
enum CommonConcepts: CptDescriptor {
    stopAndWait_act =               /// call current Caldron._requestStopAndWait_()
            cd!(HolyAction, 580_052_493),

    /// It is a very special case of the beed concept. We have in it Tid of the thread, that maintains dialog with user.
    /// Not uline branch thread, not even a caldron. It is the thread that controls the console or http connection.
    /// The Tid put put in the breed concept on start of the chat caldron, and the breed is valid only for chat branch,
    /// since the Tid field is in the live part of the concept.
    //TODO: continue from here - fill in the breed and make a binop action to pass it to the stat concept.
    chatUserThread_breed =
            cd!(HolyBreed, 217_397_612),
}

void _commonConcepts_() {
    mixin(dequalify_enums!(CommonConcepts));

    /// Setup the stop and wait action
    cpt!stopAndWait_act._statActionCid_ = _statCid_!_stopAndWait_;
}

/// Chat branch enums
/// , , , 1005527366, 246390338, 2996929904
enum Chat: CptDescriptor {
        chat_breed =                /// this is the root branch of the attention circle, is set up at the circle start
            cd!(HolyBreed, 1_719_007_030),
    chat_seed =                     /// this is the root branch of the attention circle
            cd!(HolySeed, 2_500_739_441),
    ulineHandShaker_actnrn =        /// after the seed starts uline branch, it sends user its Tid, so user could talk to it
            cd!(HolyActionNeuron, 3_996_466_002),
    chatSendsUserUlineTid_unop =    /// it is the action for the previous neuron
            cd!(HolyUnaryOperation, 3_408_832_589),
    ulineValve_andnrn =             /// wait on this neuron for the next line from the user line branch
            cd!(HolyAndNeuron, 497_144_117),
}

/// Setup the chat branch.
void _chatBranch_() {
    mixin(dequalify_enums!(CommonConcepts, Chat, Uline));    // anonymizes the concept enums, so we don't need use their full names.

    // Setup the breed and seed
    cpt!chat_breed._seed_ = chat_seed;
    cpt!chat_seed.add_effects(null, [ulineHandShaker_actnrn, uline_breed]);

    // Handshake with user
    cpt!ulineHandShaker_actnrn.add_effects(chatSendsUserUlineTid_unop, ulineValve_andnrn);
    cpt!chatSendsUserUlineTid_unop._statActionCid_ = _statCid_!send_tid_to_user;
    cpt!chatSendsUserUlineTid_unop._operandCid_ = uline_breed;

    // Wait on the user input
    cpt!ulineValve_andnrn.add_effects(float.infinity, stopAndWait_act, null);
}

// User line branch enums
/// 226154664, 2277726710, 1439958318, 1079824511, 4278173576
enum Uline {
    uline_breed =                   /// uline branch identifier
            cd!(HolyBreed, 4_021_308_401),
    uline_seed =                    /// uline branch seed
            cd!(HolySeed, 1_771_384_341),
    userInput_valve_anrn =          /// wait neuron. Wait on it for the next line of text from user.
            cd!(HolyAndNeuron, 732_066_873),
    userInput_strprem =             /// line of text from the user, string premise.
            cd!(HolyStringPremise, 3_622_010_989),
    activate_userInputPrem_unop =   /// activate user input string premise
            cd!(HolyUnaryOperation, 2_228_223_070),
    anactivate_userInputPrem_unop = /// anactivate user input string premise
            cd!(HolyUnaryOperation, 1_733_678_366),
}

/// Setup the uline branch.
void _ulineBranch_() {
    mixin(dequalify_enums!(CommonConcepts, Uline, Chat));

    // Setup actions
    cpt!activate_userInputPrem_unop._statActionCid_ = _statCid_!activate_stat;
    cpt!activate_userInputPrem_unop._operandCid_ = userInput_strprem;
    cpt!anactivate_userInputPrem_unop._statActionCid_ = _statCid_!anactivate_stat;
    cpt!anactivate_userInputPrem_unop._operandCid_ = userInput_strprem;

    // Mate uline seed and breed. Make the parent chat breed known to the uline breed. In the process of creating uline
    // caldron the live instance of chat breed will be setup for it, so uline will be able to send messages to chat.
    // And vice versa, the uline breed will be set up in the chat name space.
    cpt!uline_breed._seed_ = uline_seed;
    cpt!uline_breed._parentBreed_ = chat_breed;

    // Setup the uline_seed
    cpt!uline_seed.add_effects(anactivate_userInputPrem_unop, userInput_valve_anrn);
}

















