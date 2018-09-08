module crank_subcrank_subpile;
import std.conv: asOriginalType;

import global, tools;
import cpt_concrete;
import crank_pile;

enum OtherConcepts {
    another_test_concept_name = cpt!(HolyUnconditionalNeuron, 258_455_509),
}

void other_concepts() {
    mixin(dequalify_enums!(CommonConcepts, OtherConcepts));
}