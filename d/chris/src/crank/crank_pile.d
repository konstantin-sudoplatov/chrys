/// Dynamic concept names
module crank_pile;
import std.conv;

import global;

/// Dynamic concept names. If we use automatic enumeration, then the "max" element must be the last in the enum and the
/// first line in the next enum must be started with the "max.asOriginalType" expression.
/// If we use manual enumeration, which supposedly will be our route, then the "max" elements and the references to them
/// should be ommited, since the "max" element won't make it to the name map.
enum DCN {
    chat_seed = MAX_STATIC_CID + 1,                  // this is the root branch of the chat
    do_not_know_what_it_is,
    max         // The first not used cid. Must be the last in the enum.
}

enum DCN1 {
    test_concept_name = DCN.max.asOriginalType,
    max         // The first not used cid. Must be the last in the enum.
}
