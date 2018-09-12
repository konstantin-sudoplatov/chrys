/// Dynamic concept names
module crank_pile;

import global, tools;
import cpt_concrete;


/// Dynamic concept names.
enum CommonConcepts: CptDescriptor {
    do_not_know_what_it_is = cd!(HolySeed, 580_052_493),
}

/// free [, , 1719007030, 732066873, 3622010989, 2228223070, 1733678366, 2996929904]
enum Chat: CptDescriptor {
    chat_seed = cd!(HolySeed, 2_500_739_441),               // this is the root branch of the chat
    uline_valve_anrn = cd!(HolyAndNeuron, 497_144_117),     // wait on this neuron for the next line from user
}

void start_chat_branch() {
    mixin(dequalify_enums!(Chat, Uline));    // anonymizes the concept enums, so we don't need use their full names.

    // Setup the chat_seed
    cpt!chat_seed.add_effects(null, [uline_valve_anrn, uline_breed]);
}

enum Uline {
    uline_breed = cd!(HolyBreed, 4_021_308_401),
    uline_seed = cd!(HolySeed, 1_771_384_341),
}

void uline_branch() {
    mixin(dequalify_enums!(Uline));

    // mate console seed and breed
    cpt!uline_breed.seed = uline_seed;
}

















