module crank_subcrank_subpile;
import std.conv: asOriginalType;

import common_tools;

import global_data, crank_registry;
import cpt_neurons;
import crank_main;
import attn_circle_thread;

enum OtherConcepts {
    another_test_concept_name = cd!(SpActionNeuron, 258_455_509),
}

void other_concepts() {
    mixin(dequalify_enums!(CommonConcepts, OtherConcepts));
}