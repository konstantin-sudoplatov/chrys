module crank_subcrank_subpile;
import std.conv;
import crank_pile;

enum DCN2 {
    another_test_concept_name = DCN1.max.asOriginalType,
    max         // The first not used cid. Must be the last in the enum.
}
