module cpt.cpt_stat;
import std.format;

import project_params, tools;

import stat.stat_types;
import cpt.cpt_types, cpt.abs.abs_concept;
import atn.atn_circle_thread;

/**
            Static concept.
    Actually, it is immutable since all fields are immutable. Making the class or constructor immutable, however would introduce
    unneccessary complexity in the code, that uses this class.
*/
@(0) final class SpStaticConcept: SpiritConcept {

    immutable void* fp;                    /// function pointer to the static concept function
    immutable StatCallType callType;       /// call type of the static concept function

    /**
                Constructor
        Parameters:
            cid = Concept identifier. Must lay in the range of static concepts.
            fp = function pointer to the static concept function
            callType = call type of the static concept function
    */
    this(Cid cid, void* fp, StatCallType callType){
        super(cid, spClid!(typeof(this)));

        cast()flags |= SpCptFlags.STATIC;
        cast()this.fp = cast(immutable)fp;
        cast()this.callType = callType;
    }

    /// Serialize concept
    override Serial serialize() const {
        assert(false, "Stab");
    }

    /// Disabled equality test
    override bool opEquals(Object sc) const {
        assert(false,
                "No need for this test since static concepts are not stored in DB and so not subject to versioning.");
    }

    /// Create live wrapper for the holy static concept.
    override StaticConcept live_factory() const {
        return new StaticConcept(cast(immutable)this);
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
            Initialize concept from its serialized form.
        Parameters:
            cid = cid
            ver = concept version
            clid = classinfo identifier
            stable = stable part of data
            transient = unstable part of data
        Returns: newly constructed object of this class
    */
    protected override void _deserialize(Cid cid, Cvr ver, Clid clid, const byte[] stable, const byte[] transient) {
        assert(false, "Stab");
    }
}

/// Live wrapper for the HolyConcept class
final class StaticConcept: Concept {

    /// Private constructor. Use HolyTidPrimitive.live_factory() instead.
    private this(immutable SpStaticConcept holyStaticConcept) { super(holyStaticConcept); }
}
