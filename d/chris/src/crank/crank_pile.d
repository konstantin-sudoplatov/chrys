/// Dynamic concept names
module crank_pile;

import global, tools;
import cpt_concrete;
import stat_pile;


/// Dynamic concept names.
enum CommonConcepts: CptDescriptor {
    stopAndWait_act = cd!(HolyAction, 580_052_493),
}

void _commonConcepts_() {
    mixin(dequalify_enums!(CommonConcepts));

    /// Setup the stop and wait action
    cpt!stopAndWait_act._statActionCid_ = _statCid_!_stopAndWait_;
}

/// Chat branch enums
/// 3996466002, 3408832589, 217397612, 1005527366, 246390338, 2996929904
enum Chat: CptDescriptor {
    chat_breed = cd!(HolyBreed, 1_719_007_030),             // this is the root branch of the attention circle, is set up at the circle start
    chat_seed = cd!(HolySeed, 2_500_739_441),               // this is the root branch of the attention circle
    uline_valve_anrn = cd!(HolyAndNeuron, 497_144_117),     // wait on this neuron for the next line from the user line branch
}

/// Setup the chat branch.
void _startChatBranch_() {
    mixin(dequalify_enums!(CommonConcepts, Chat, Uline));    // anonymizes the concept enums, so we don't need use their full names.

    // Setup the chat_seed
    cpt!chat_breed._seed_ = chat_seed;
    cpt!chat_seed.add_effects(null, [uline_valve_anrn, uline_breed]);
    cpt!uline_valve_anrn.add_effects(float.infinity, stopAndWait_act, null);
}

// User line branch enums
/// 226154664, 2277726710, 1439958318, 1079824511, 4278173576
enum Uline {
    uline_breed = cd!(HolyBreed, 4_021_308_401),            // uline branch identifier
    uline_seed = cd!(HolySeed, 1_771_384_341),              // uline branch seed
    userInput_valve_anrn = cd!(HolyAndNeuron, 732_066_873), // wait neuron. Wait on it for the next line of text from user.
    userInput_strprem = cd!(HolyStringPremise, 3_622_010_989),  // line of text from the user, string premise.
    //activate_userInputPrem_act = cd!(HolyAction, 2_228_223_070),    // activate user input string premise
    //anactivate_userInputPrem_act = cd!(HolyAction, 1_733_678_366),  // anactivate user input string premise
}

/// Setup the uline branch.
void _ulineBranch_() {
    mixin(dequalify_enums!(CommonConcepts, Uline, Chat));

    // Mate uline seed and breed. Make the parent chat breed known to the uline breed. In the process of creating uline
    // caldron the live instance of chat breed will be setup for it, so uline will be able to send messages to chat.
    // And vice versa, the uline breed will be set up in the chat name space.
    cpt!uline_breed._seed_ = uline_seed;
    cpt!uline_breed._parentBreed_ = chat_breed;

    // Setup the uline_seed
    cpt!uline_seed.add_effects(null, userInput_valve_anrn);
}

















