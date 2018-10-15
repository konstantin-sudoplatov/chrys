module cpt.abs.abs_premise;
import std.format, std.typecons;

import project_params;

import cpt.abs.abs_concept;
import cpt.cpt_interfaces;

/**
            Base for all premises.
    All concrete descendants will have the "_pre" suffix.
*/
abstract class SpiritPremise: SpiritDynamicConcept {

    /// constructor
    this(Cid cid, Clid clid) { super(cid, clid); }

    /// Serialize concept
    override Serial serialize() const {
        assert(false, "Stab");
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
            Initialize concept from its serialized form.
        Parameters:
            stable = stable part of data
            transient = unstable part of data
        Returns: unconsumed slices of the stable and transient byte arrays.
    */
    protected override Tuple!(const byte[], "stable", const byte[], "transient") _deserialize(const byte[] stable,
            const byte[] transient)
    {
        assert(false, "Stab");
    }
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
