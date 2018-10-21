module cpt.abs.abs_premise;
import std.format, std.typecons;

import proj_data;

import cpt.abs.abs_concept;
import cpt.cpt_interfaces;

/**
            Base for all premises.
    All concrete descendants will have the "_pre" suffix.
*/
abstract class SpiritPremise: SpiritDynamicConcept {

    /// constructor
    this(Cid cid) { super(cid); }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--
}

/// Ditto
abstract class Premise: DynamicConcept, BinActivationIfc {
    this(immutable SpiritPremise holyPremise) { super(holyPremise); }

    override string toString() const {
        string s = super.toString;
        s ~= format!"\n    _activation = %s"(_activation);
        return s;
    }

    mixin BinActivationImpl!Premise;
}
