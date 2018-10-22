module cpt.cpt_primitives;
import std.typecons;

import proj_data, proj_funcs;

import chri_data;
import cpt.cpt_types, cpt.abs.abs_primitive;

/**
        Mark primitive. It contains no data, only its cid carries some meaning. So, its live part may even naver be instantiated.
    It is used in the SpiritPrimitive to mark its type.
*/
@(16) final class SpMarkPrim: SpiritPrimitive {

    this(Cid cid) { super(cid); }

    /// Create live wrapper for the spirit static concept.
    override MarkPrim live_factory() const {
        return new MarkPrim(cast(immutable)this);
    }
}

/// Live.
final class MarkPrim: Primitive {

    /// Private constructor. Use spiritual live_factory() instead.
    private this(immutable SpMarkPrim spMarkPrimitive) { super(spMarkPrimitive); }
}


/**
            String primitive. Contains a line of text.
*/
@(17)final class SpStringPrim: SpiritPrimitive {

    /// The string
    string text;

    this(Cid cid) { super(cid); }

    /// Create live wrapper for the spirit static concept.
    override StringPrim live_factory() const {
        return new StringPrim(cast(immutable)this);
    }

    /// Serialize concept
    override Serial serialize() const {
        Serial res = super.serialize;
        res.stable ~= serializeArray(text);

        return res;
    }

    /// Equality test
    override bool opEquals(Object sc) const {

        if(!super.opEquals(sc)) return false;
        auto o = scast!(typeof(this))(sc);
        return text == o.text;
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
        auto rest = super._deserialize(stable, transient);
        auto dser = deserializeArray!(char[])(rest.stable);
        text = cast(immutable char[])dser.array;
        return tuple!(const byte[], "stable", const byte[], "transient")(dser.restOfBuffer, rest.transient);
    }

}

unittest {
    import std.stdio;
    auto a = new SpStringPrim(42);
    a.ver = 5;
    a.mark = 43;
    a.text = "Здравствуй, мир!";
    Serial ser = a.serialize;

    auto b = cast(SpStringPrim)a.deserialize(ser.cid, ser.ver, ser.clid, ser.stable, ser.transient);
    assert(b.cid == 42 && b.ver == 5 && b.mark == 43 && typeid(b) == typeid(SpStringPrim) &&
            b.text == "Здравствуй, мир!");

    assert(a == b);
}

/// Live.
final class StringPrim: Primitive {

    /// Private constructor. Use spiritual live_factory() instead.
    private this(immutable SpStringPrim spStringPrimitive) { super(spStringPrimitive); }
}


//---***---***---***---***---***--- data ---***---***---***---***---***--

//---***---***---***---***---***--- functions ---***---***---***---***---***--

//---***---***---***---***---***--- types ---***---***---***---***---***---***
