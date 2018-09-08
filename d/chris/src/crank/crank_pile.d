/// Dynamic concept names
module crank_pile;

import global, tools;
import cpt_concrete;


/// Dynamic concept names.
enum CommonConcepts: Cid {
    chat_seed = cpt!(HolySeed, 2_500_739_441),                  // this is the root branch of the chat
    do_not_know_what_it_is = cpt!(HolySeed, 580_052_493),
}

void start() {
    mixin(dequalify_enums!(CommonConcepts, Chat));    // anonymizes the concept enums, so we don't need use their full names.

    // Create the chat_seed concept. It will be used in initialization of the attention circles.
    auto chatSeed = new shared HolySeed(chat_seed);
    _hm_.add(new shared HolySeed(chat_seed));                                  // add to the holy map.
    chatSeed.add_effects(null, [chat_seed, console_breed]);
}

/// free [, 497144117, 1719007030, 732066873, 3622010989, 2228223070, 1733678366, 2996929904]
enum Chat: Cid {
    console_breed = cpt!(HolyBreed, 4_021_308_401),
    console_seed = cpt!(HolySeed, 1_771_384_341),
}

void chat_branch() {
    mixin(dequalify_enums!(CommonConcepts, Chat));

    // Create console seed and breed and add it to the chat seed
    auto consoleBreed = new shared HolyBreed(console_breed);
    consoleBreed.seed_cid = console_seed;
    _hm_[] = consoleBreed;
}

















