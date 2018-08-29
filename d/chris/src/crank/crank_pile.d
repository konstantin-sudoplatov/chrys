/// Dynamic concept names
module crank_pile;
import std.conv;

import global;

/// Dynamic concept names
enum DCN {
    chat_seed = MAX_STATIC_CID + 1,                  // this is the root branch of the chat
    max         // The first not used cid. Must be the last in the enum.
}

enum DCN1 {
    test_concept_name = DCN.max.asOriginalType,
    max         // The first not used cid. Must be the last in the enum.
}
