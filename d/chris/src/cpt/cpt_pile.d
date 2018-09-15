module cpt_pile;
import std.format;

import global, tools;
import cpt_abstract;
import interfaces;
import attn_circle_thread;

/**
            Static concept.
    Actually, it is immutable since all fields are immutable. Making the class or constructor immutable, however would introduce
    unneccessary complexity in the code, that uses this class.
*/
final class SpStaticConcept: SpiritConcept {

    immutable void* fp;                     /// function pointer to the static concept function
    immutable StatCallType _callType_;       /// call type of the static concept function

    /**
                Constructor
        Parameters:
            cid = Concept identifier. Must lay in the range of static concepts.
            fp = function pointer to the static concept function
            callType = call type of the static concept function
    */
    this(Cid cid, void* fp, StatCallType callType){
        super(cid);
        cast()flags |= SpCptFlags.STATIC;
        cast()this.fp = cast(immutable)fp;
        cast()_callType_ = callType;
    }

    /// Create live wrapper for the holy static concept.
    override StaticConcept live_factory() const {
        return new StaticConcept(cast(immutable)this);
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--
}

/// Live wrapper for the HolyConcept class
final class StaticConcept: Concept {

    /// Private constructor. Use HolyTidPrimitive.live_factory() instead.
    private this(immutable SpStaticConcept holyStaticConcept) { super(holyStaticConcept); }
}

/**
        Tid primitive.
    The field tid_ is in the live part.
*/
final class SpTidPrimitive: SpiritPrimitive {

    /// Constructor
    this(Cid cid) { super(cid); }

    /// Create live wrapper for the holy static concept.
    override TidPrimitive live_factory() const {
        return new TidPrimitive(cast(immutable)this);
    }
}

/// Live.
final class TidPrimitive: Primitive {
    import std.concurrency: Tid;

    /// The tid field
    Tid _tid_;

    /// Private constructor. Use spiritual live_factory() instead.
    private this(immutable SpTidPrimitive SpTidPrimitive) { super(SpTidPrimitive); }

}

/**
            Peg premise.
*/
final class SpPegPremise: SpiritPremise {

    /// Constructor
    this(Cid cid) { super(cid); }

    /// Create live wrapper for the holy static concept.
    override PegPremise live_factory() const {
        return new PegPremise(cast(immutable)this);
    }
}

/// Live.
final class PegPremise: Premise {

    /// Private constructor. Use spiritual live_factory() instead.
    private this(immutable SpPegPremise holyPegPremise) { super(holyPegPremise); }
}

/**
            String premise.
    The string field is in the live part.
*/
final class SpStringPremise: SpiritPremise {

    /**
                Constructor
        Parameters:
            cid = predefined concept identifier
    */
    this(Cid cid) { super(cid); }

    /// Create live wrapper for the holy static concept.
    override StringPremise live_factory() const {
        return new StringPremise(cast(immutable)this);
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--
}

/// Live.
final class StringPremise: Premise {

    /// The string field
    string _str_;

    /// Private constructor. Use spiritual live_factory() instead.
    private this(immutable SpStringPremise holyStringPremise) { super(holyStringPremise); }
}


//---***---***---***---***---***--- types ---***---***---***---***---***---***

//---***---***---***---***---***--- data ---***---***---***---***---***--

/**
        Constructor
*/
//this(){}

//---***---***---***---***---***--- functions ---***---***---***---***---***--

//~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
//
//                                 Protected
//
//~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
protected:
//---$$$---$$$---$$$---$$$---$$$--- data ---$$$---$$$---$$$---$$$---$$$--

//---$$$---$$$---$$$---$$$---$$$--- functions ---$$$---$$$---$$$---$$$---$$$---

//---$$$---$$$---$$$---$$$---$$$--- types ---$$$---$$$---$$$---$$$---$$$---

//===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
//
//                                  Private
//
//===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
private:
//---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
