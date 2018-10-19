module cpt.cpt_types;

import proj_shared, proj_tools;
import db.db_main, db.db_concepts_table;

import cpt.cpt_registry, cpt.abs.abs_concept;

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
