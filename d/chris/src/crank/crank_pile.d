/// Dynamic concept names
module crank_pile;

import global, tools;
import cpt_concrete;


/// Dynamic concept names.
enum CommonConcepts: CptDescriptor {
    chat_seed = cd!(HolySeed, 2_500_739_441),                  // this is the root branch of the chat
    do_not_know_what_it_is = cd!(HolySeed, 580_052_493),
}

void start() {
    mixin(dequalify_enums!(CommonConcepts, Chat));    // anonymizes the concept enums, so we don't need use their full names.

    // Setup the chat_seed
    shared HolySeed seed = cpt!chat_seed;
    seed.add_effects(null, [chat_seed, console_breed]);
}

/// free [, 497144117, 1719007030, 732066873, 3622010989, 2228223070, 1733678366, 2996929904]
enum Chat: CptDescriptor {
    console_breed = cd!(HolyBreed, 4_021_308_401),
    console_seed = cd!(HolySeed, 1_771_384_341),
}

void chat_branch() {
    mixin(dequalify_enums!(CommonConcepts, Chat));

    // mate console seed and breed
    cpt!console_breed.seed_cid = console_seed.cid;
}

















