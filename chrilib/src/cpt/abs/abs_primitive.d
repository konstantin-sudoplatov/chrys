module cpt.abs.abs_primitive;
import std.typecons;

import proj_data, proj_funcs;

import chri_data;
import cpt.cpt_types, cpt.abs.abs_concept;

/**
            Base for all premitives. Primitives are mostly the spirit entities since they are mostly designed as a data
    storage facility, and they store their data in the spiritual part. Their live part is used to store the delta to the
    spiritual part contents, so that at the end of the life cycle of the concept or during some intervals the delta
    would be merged into the spiritual part and eventually stored in the database. BR
        Also, since there would be many different uses for primitive of one type, it has a type attribute. For example,
    since such different concepts as words, separators, digits and more can be represented by a single class pair
    SpStringPrimitive/StringPrimitive, we will need some means of qualification.
*/
abstract class SpiritPrimitive: SpiritDynamicConcept {

    /// Type of the concept. It is a cid of the SpiritMarkPrimitive, for example.
    Cid mark;

    this(Cid cid) { super(cid); }

    /// Serialize concept
    override Serial serialize() const {
        Serial res = super.serialize;

        res.stable.length = mark.sizeof;  // allocate
        *cast(Cid*)&res.stable[0] = mark;

        return res;
    }

    /// Equality test
    override bool opEquals(Object sc) const {

        if(!super.opEquals(sc)) return false;
        auto o = scast!(typeof(this))(sc);
        return mark == o.mark;
    }

    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //                                 Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$--- data ---$$$---$$$---$$$---$$$---$$$--

    //---$$$---$$$---$$$---$$$---$$$--- functions ---$$$---$$$---$$$---$$$---$$$---

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
        mark = *cast(Cid*)&stable[0];
        return tuple!(const byte[], "stable", const byte[], "transient")(stable[mark.sizeof..$], transient);
    }
}

/// Ditto
abstract class Primitive: DynamicConcept {

    /// Constructor. Is called from the live_factory() function of the spirit counterpart.
    this(immutable SpiritPrimitive spiritPrimitive) { super(spiritPrimitive); }
}
