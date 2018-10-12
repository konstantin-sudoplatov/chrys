module cpt.cpt_types;

/// External runtime function, that creates a new object by its ClassInfo. No constructors are called. Very fast, much faster
/// than manually allocating the object on the heap as new buf[], as it is done in the emplace function. Used in the
/// SpiritConcept.clone() method.
private extern (C) Object _d_newclass (ClassInfo info);

/// Concept's attributes.
enum SpCptFlags: short {

    /// Static concept
    STATIC = 0x0001,

    /// Temporary dynamic concept. Heavily uses its live part, since it is thread local. Even its holy part is not designed
    /// to be stored in the DB, if only to collect the usage info.
    TEMP = 0x0002,

    /// Permanent dynamic concept. The holy part is stored in the DB and constitutes the knoledge base.
    PERM = 0x0004,
}

//---***---***---***---***---***--- functions ---***---***---***---***---***--

/// Take clid from annotation of the spirit concept class and make it a enum. Designed to be used in the class constructor.
template spClid(T: SpiritConcept)
    if(__traits(getAttributes, T).length == 1 && is(typeof(__traits(getAttributes, T)[0]) == int) &&
            __traits(getAttributes, T)[0] >= 0)
{
    enum :Clid {
        spClid = __traits(getAttributes, T)[0]
    }
}

//---***---***---***---***---***--- types ---***---***---***---***---***--

/**
        Concept version control struct. BR
    It contains a raw version field, which is the part of each concept. Zero value of that field is quite legal and it
    means that the concept is of the _min_ver_ version, the oldest valid version that cannot be removed yet.
*/
shared synchronized class ConceptVersion {

    /// The newest availabale version to use. This is the latest version commited by the tutor. If the _cur_ver_ rolled over the
    /// Cvr.max and became the lesser number than all other versions, it stil must not reach the _stale_ver_, or an assertion
    /// exception will be thrown.
    private static Cvr _cur_ver_;

    /// Minimal currently used version. If a concept has version 0 it means this version. All versions older than that
    /// they are stale and may be removed.
    private static Cvr _min_ver_;

    /// Minimum stale version. Stale versions are less than _min_ver_ and so should be removed.
    private static Cvr _stale_ver_;
}
