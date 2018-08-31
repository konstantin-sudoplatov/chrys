module crank_subcrank_subpile;
import std.conv: asOriginalType;

import global, tools;
import crank_pile;

enum OtherConcepts {
    another_test_concept_name = Chat.max.asOriginalType,
    max         // The first not used cid. Must be the last in the enum.
}

void other_concepts() {
    mixin(dequalify_enums!(CommonConcepts, OtherConcepts));
}