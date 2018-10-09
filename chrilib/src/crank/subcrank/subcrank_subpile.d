module crank.subcrank.subcrank_subpile;
import std.conv: asOriginalType;

import crank.crank_types;
import cpt.cpt_neurons;


enum OtherConcepts {
    another_test_concept_name = cd!(SpActionNeuron, 258_455_509),
}

void other_concepts() {
    mixin(dequalify_enums!(CommonConcepts, OtherConcepts));
}