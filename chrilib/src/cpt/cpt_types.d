module cpt.cpt_types;

import project_params, tools;
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

/// Database management for concepts
struct DbConceptHandler {
    import derelict.pq.pq: PGconn;

    @disable this();

    alias con_ this;

    /// Implicit constructor.
    this(int dummy) {
        openDatabase_;
    }

    /// Destructor.
    ~this() {
        closeDatabase_;
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
            Factory for creating concepts based on the serialization from the database.
        Parameters:
            cid = cid
            ver = version
        Returns: newly constructed object or null if it was not found in the DB.
    */
    SpiritConcept retreiveConcept(Cid cid, Cvr ver) const {
        const cptDat = cptTbl_.getConcept(cid, ver);
        if      // is the concept present in the DB?
                (cptDat)
        {   // yes: create and return it
            // TODO not finished
            //SpiritConcept dbCpt = cast(SpiritConcept)_d_newclass(spiritRegistry[cptDat.clid]);
            //size_t size = dbCpt.classinfo.initializer.length;
            //(cast(byte*)dbCpt)[8..size] = cptDat.stable[0..size-8];
            return null;
        }
        else
            return null;
    }

    /**
            Wrapper for the ConceptsTable.insertConcept.
        Parameters:
            cpt - concept to insert
        Throws: enforce() for errors, for a dupilcate key, for example
    */
    void insertConcept(const SpiritConcept cpt) const {
        cptTbl_.insertConcept(
            cpt.cid,
            cpt.ver,
            cpt.clid,
            cpt.shallowBlit,
            cpt.deepBlit
        );
    }

    /**
            Wrapper for the ConceptsTable.updateConcept.
        Parameters:
            cpt - concept to update
        Throws: enforce, if there is an error, no record to update, for example.
    */
    void updateConcept(const SpiritConcept cpt) const {
        cptTbl_.updateConcept(
            cpt.cid,
            cpt.ver,
            cpt.clid,
            cpt.shallowBlit,
            cpt.deepBlit
        );
    }

    //---***---***---***---***---***--- private ---***---***---***---***---***--

    /// Pointer to connection.
    private PGconn* con_;

    /// Pointer to the concepts table control structure
    private ConceptsTable* cptTbl_;

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /// Connect to the database.
    private void openDatabase_() {
        assert(!con_, "Db must be closed.");
        con_ = connectToDb;
        cptTbl_ = new ConceptsTable(con_);
    }

    /// Diskonnect from the database.
    private void closeDatabase_() {
        assert(con_, "DB must be open.");
        disconnectFromDb(con_);
        con_ = null;
        cptTbl_ = null;
    }
}