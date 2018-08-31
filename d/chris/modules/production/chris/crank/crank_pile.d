/// Dynamic concept names
module crank_pile;
import std.conv;

import global, tools;
import cpt_holy, cpt_holy_abstract;

/// Dynamic concept names. If we use automatic enumeration, then the "max" element must be the last in the enum and the
/// first line in the next enum must be started with the "max.asOriginalType" expression.
/// If we use manual enumeration, which supposedly will be our route, then the "max" elements and the references to them
/// should be ommited, since the "max" element won't make it to the name map.
enum CommonConcepts: Cid {
    chat_seed = 580052493,                  // this is the root branch of the chat
    do_not_know_what_it_is,
    max         // The first not used cid. Must be the last in the enum.
}

void common_concepts() {
    mixin(dequalify_enums!CommonConcepts);
    auto cpt= new shared HolySeed;
    (cast()cpt.cid) = chat_seed;
    _hm_.add(cpt);
shared HolyConcept c = _hm_[chat_seed];
mixin("c".w);
}

enum Chat: Cid {
    test_concept_name = CommonConcepts.max.asOriginalType,
    max         // The first not used cid. Must be the last in the enum.
}

void chat_branch() {
    mixin(dequalify_enums!(CommonConcepts, Chat));
}



















