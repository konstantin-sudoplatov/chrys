module cpt.abs.abs_premise;
import std.format, std.typecons;

import proj_shared, proj_tools;

import cpt.abs.abs_concept;
import cpt.cpt_interfaces;

/**
            Base for all premises.
    All concrete descendants will have the "_pre" suffix.
*/
abstract class SpiritPremise: SpiritDynamicConcept {

    /// constructor
    this(Cid cid, Clid clid) { super(cid, clid); }

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
