module crank.subcrank.subcrank_subpile;

import proj_shared, proj_tools;

import crank.crank_types;
import crank.crank_main;
import cpt.cpt_neurons;
import chri_types;


enum OtherConcepts {
    another_test_concept_name = cd!(SpActionNeuron, 258_455_509),
}

void other_concepts() {
    mixin(dequalify_enums!(CommonConcepts, OtherConcepts));
}