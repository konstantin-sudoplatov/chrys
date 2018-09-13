/// Dynamic concept names
module crank_pile;

import global, tools;
import cpt_concrete;


/// Dynamic concept names.
enum CommonConcepts: CptDescriptor {
    do_not_know_what_it_is = cd!(HolySeed, 580_052_493),
}

/// free [, , , 732066873, 3622010989, 2228223070, 1733678366, 2996929904]
enum Chat: CptDescriptor {
    chat_breed = cd!(HolyBreed, 1_719_007_030),             // this is the root branch of the attention circle, is set up at the circle start
    chat_seed = cd!(HolySeed, 2_500_739_441),               // this is the root branch of the attention circle
    uline_valve_anrn = cd!(HolyAndNeuron, 497_144_117),     // wait on this neuron for the next line from user
}

void start_chat_branch() {
    mixin(dequalify_enums!(Chat, Uline));    // anonymizes the concept enums, so we don't need use their full names.

    // Setup the chat_seed
    cpt!chat_breed._seed_ = chat_seed;
    cpt!chat_seed.add_effects(null, [uline_valve_anrn, uline_breed]);
}

enum Uline {
    uline_breed = cd!(HolyBreed, 4_021_308_401),
    uline_seed = cd!(HolySeed, 1_771_384_341),
}

void uline_branch() {
    mixin(dequalify_enums!(Uline, Chat));

    // Mate user line seed and breed. Make parent chat breed known to the uline breed. In the process of creating uline
    // caldron the live instance of chat breed will be setup for it, so uline will be able to send messages to chat.
    // And vice versa, the uline breed will be set up in the chat name space.
    cpt!uline_breed._seed_ = uline_seed;
    cpt!uline_breed._parentBreed_ = chat_breed;
}

















