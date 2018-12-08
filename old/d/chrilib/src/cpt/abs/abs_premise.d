module cpt.abs.abs_premise;
import std.format, std.typecons;

import proj_data;

import cpt.abs.abs_concept;
import cpt.cpt_interfaces;

/**
            Base for all premises. Premises are mostly live entities since their main activity is in the caldron local
    level. The activation interface they implement works only in the live realm never penetrating the spirit level. They
    can ocasionally heep their data in the spirit part though, like the Breed concept keeps its corresponding Seed cid there.
*/
abstract class SpiritPremise: SpiritDynamicConcept {
    this(Cid cid) { super(cid); }
}

/// Ditto
abstract class Premise: DynamicConcept, BinActivationIfc {

    /// Constructor. Is called from the live_factory() function of the spirit counterpart.
    this(immutable SpiritPremise spiritPremise) { super(spiritPremise); }

    override string toString() const {
        string s = super.toString;
        s ~= format!"\n    _activation = %s"(_activation);
        return s;
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
            Copy meaningful fields of this concept to the destination. Both concepts must be exactly the same runtime type.
        Such fields as cid, ver or the usage statistics, for example, are left intact.
        Parameters:
            destination = concept to copy data to
        Returns: changed destination concept
    */
    void copy(ref Premise destination) {
        assert(typeid(this) is typeid(destination));
        destination._activation = _activation;
    }

    mixin BinActivationImpl!Premise;
}
