module chri_types;
import std.stdio;
import std.conv, std.format, std.string, core.exception, core.thread;

import proj_data, proj_funcs, proj_types;
import db.db_main, db.db_concepts_table;

import chri_data;
import cpt.cpt_types, cpt.abs.abs_concept, cpt.cpt_stat;
import atn.atn_caldron;

//---***---***---***---***---***--- functions ---***---***---***---***---***--

/// Get name of a concept, if exists.
string cptName(Cid cid) {
    return cid in _nm_? _nm_[cid]: "noname";
}

/// Adapter.
string cptName(DcpDsc dc) {
    return dc.cid in _nm_? _nm_[dc.cid]: "noname";
}

/**
            Check up availability and type of a concept by its cid.
    Parameters:
        T = type to check against
        cid = cid of a concept, that is checked
*/
void checkCid(T: SpiritConcept)(Cid cid) {
    debug if(_maps_filled_) {
        assert(cid in _sm_, "Cid: %s(%s) do not exist in the holy map.".format(cid, cid in _nm_? _nm_[cid]: "noname"));
        assert(cast(T)_sm_[cid],
                "Cid: %s, must be of type %s and it is of type %s.".format(cid, T.stringof, typeid(_sm_[cid])));
    }
}

///         Adapter for live concepts.
void checkCid(T)(Caldron caldron, Cid cid)
    if(is(T: Concept) || is(T == interface))
{
    debug if(_cranked_)
        assert((cast(T)caldron[cid]),
                format!"Cid: %s, must be of type %s and it is of type %s."
                        (cid, T.stringof, typeid(caldron[cid])));
}

/**
        Remove from the name map all entries that don't have related entry in the spirit map.
*/
void cleanupNotUsedNames() {
    import std.typecons: Tuple;

    // Find all orphan entries in the name map.
    alias Entry = Tuple!(Cid, "cid", string, "name");
    Entry[] orphans;
    Entry orphan;
    foreach(cid; (cast()_nm_).byKey)
        if      //is not cid present in the holy map?
                (cid !in _sm_)
        {
            orphan.cid = cid;
            orphan.name =_nm_[cid];

            orphans ~= orphan;
        }

    // Remove orphans
    foreach(orph; orphans) {
        logit(format!"Removing from _nm_ name: %s, cid %s is not in the _sm_"(orph.name, orph.cid), TermColor.red);
        (cast()_nm_).remove(orph.cid);
    }
}

//---***---***---***---***---***--- types ---***---***---***---***---***---***

/**
            Holy concepts map. It is a wrapper for actual associative array.
        Map of all static and dynamic shared storrable (holy) concepts. This map will be used concurrently by all caldrons,
    so it must be synchronized. At the moment, it is usual syncronization on the class object. In the future it can possibly
    be changed to atomic, because the concurrent asccess might be intensive. To that end acsses via the class methods would
    help, because this way we could get away with changes to only interface methods for the real map.
*/
import std.random;
synchronized final pure nothrow class SpiritMap {

    // Forward
    alias spiritMan_ this;

    /**
        Constructor
    */
    this(){
        // Initialize random generator
        rnd_ = Random(unpredictableSeed);
        spiritMan_.openDatabase;
    }

    /// Destructor.
    ~this(){
        spiritMan_.closeDatabase;
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
                Length of the map.
        Returns: the length of the map as in an AA.
    */
    auto length() {
        return spiritMap_.length;
    }

    /**
                Assign/construct-assign new spirit map entry. If cid had not been assigned to the cpt yet, it is generated.
        Parameters:
            cpt = shared concept to assign
    */
    SpiritConcept add(SpiritConcept cpt)
    in {
        assert(cpt.cid !in spiritMap_, "Cid " ~ to!string(cpt.cid) ~ " - this cid already exists in the spirit map.");
        if      // dynamic?
                (cast(shared SpiritDynamicConcept)cpt)
            if      // with preset cid?
                    (cpt.cid != 0)
                assert(cpt.cid >= MIN_DYNAMIC_CID && cpt.cid <= MAX_DINAMIC_CID,
                        "Cid: " ~ to!string(cpt.cid) ~ ", cids for dynamic concepts must lie in the range of " ~
                        to!string(MIN_DYNAMIC_CID) ~ ".." ~ to!string(MAX_DINAMIC_CID));
            else {} //no: dynamic concepts without cid are allowed, cid will be generated
        else if // static?
                (cast(shared SpStaticConcept)cpt)
        {
            assert(cpt.cid != 0, "Static concepts can't have zero cid. Their cids are initialized at construction.");
            assert(cpt.cid >= MIN_STATIC_CID && cpt.cid <= MAX_STATIC_CID,
                    "Cid: " ~ to!string(cpt.cid) ~ ", cids for static concepts must lie in the range of " ~
                    to!string(MIN_STATIC_CID) ~ ".." ~ to!string(MAX_STATIC_CID));
        }
        else    // neither dynamic and nor static?
            assert(false, to!string(cpt) ~ " - not expected type here.");
    }
    do {
        // generate cid and use it
        if      // is not cid set yet?
                (cpt.cid == 0)
            //no: generate and set it
            cast()cpt.cid = generateDynamicCid_;

        // put the pair in the map
        spiritMap_[cpt.cid] = cast(shared)cpt;

        return cpt;
    }

    /**
            Remove key from map. Analogously to the AAs.
        Parameters:
            cid = key
        Returns: true if existed, else false
    */
    bool remove(Cid cid) {
        return spiritMap_.remove(cid);
    }

    /**
                Get concept by cid, an overload for the index operation.
        Parameters:
            cid = key
        Returns: concept
    */
    SpiritConcept opIndex(Cid cid) {
        if      //is cid in the map?
                (auto p = cid in spiritMap_)
            return cast()*p;
        else {
            SpiritConcept cpt = spiritMan_.retrieveConcept(cid, 0);     // is ver=0 ok? it is for now.
            if(cpt)
                return cpt;
            else
                throw new RangeError("There is no concept cid = %s(\"%s\") in DB.".format(cid,
                        cid in _nm_? _nm_[cid]: "noname"));
        }
    }

    /// Adapter
    SpiritConcept opIndex(DcpDsc dc) {
        return this[dc.cid];
    }

    /**
                Overload for "in". It checks for existence of the concept not only in the map, but in DB too.
        Parameters:
            cid = cid of the concept.
        Returns: pointer to the concept or null
    */
    SpiritConcept* opBinaryRight(string op)(Cid cid) {
        if      // is the concept in the map?
                (auto p = cid in spiritMap_)
            return cast(SpiritConcept*)p;
        else
            if      // is it in the DB?
                    (auto cpt = spiritMan_.retrieveConcept(cid, 0))
                return cast(SpiritConcept*)cpt;
            else
                return null;
    }

    /**
            Returns (I hope) a cast of keys of the map on the moment of the call. Synchronization would obviously be no
        good with ranges.
    */
    Cid[] keys() {
        return spiritMap_.keys;
    }

    /**
                Rebuild associative array to make it more efficient.
    */
    void rehash() {
        spiritMap_.rehash;
    }

    /**
                Generate a namber of dynamic cids.
        Parameters:
            howMany = how many cids you need
        Returns: array of fresh cidsj
    */
    Cid[] generate_some_cids(int howMany) {

        Cid[] sids;
        foreach(i; 0..howMany)
            sids ~= generateDynamicCid_;

        return sids;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%
    private SpiritConcept[Cid] spiritMap_;       /// map concept/cid

    private immutable SpiritManager spiritMan_ = SpiritManager();

    /// rnd generator. Initialized from constructor.
    private static typeof(Random(unpredictableSeed())) rnd_;

    //---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

    /**
                Generate new unique throughout the system cid.
        Returns: cid
    */
    private Cid generateDynamicCid_() {
        Cid cid;
        do {
            cid = rnd_.uniform!Cid;
        } while(cid in this);        // do until not repeated in the map and DB

        return cid;
    }
}

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
immutable struct SpiritManager {
    import derelict.pq.pq: PGconn;

    alias cptTbl_ this;

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
            Factory for creating concepts based on the serialization from the database.
        Parameters:
            cid = cid
            ver = version
        Returns: newly constructed object or null if it was not found in the DB.
    */
    SpiritConcept retrieveConcept(Cid cid, Cvr ver) const {
        const cpDat = cptTbl_.getConcept(cid, ver);
        if      // is the concept present in the DB?
                (cpDat)
        {   // yes: create and return it
            return SpiritConcept.deserialize(cid, ver, cpDat.clid, cpDat.stable, cpDat.transient);
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
        Serial ser = cpt.serialize;
        cptTbl_.insertConcept(
            ser.cid,
            ser.ver,
            ser.clid,
            ser.stable,
            ser.transient
        );
    }

    /**
            Wrapper for the ConceptsTable.updateConcept.
        Parameters:
            cpt - concept to update
        Throws: enforce, if there is an error, no record to update, for example.
    */
    void updateConcept(const SpiritConcept cpt) const {
        Serial ser = cpt.serialize;
        cptTbl_.updateConcept(
            ser.cid,
            ser.ver,
            ser.clid,
            ser.stable,
            ser.transient
        );
    }

    /// Connect to the database.
    void openDatabase() {
        assert(!con_, "Db must be closed.");
        cast()con_ = cast(immutable)connectToDb;
        cast()cptTbl_ = cast(immutable)new ConceptsTable(cast(PGconn*)con_);
    }

    /// Diskonnect from the database.
    void closeDatabase() {
        assert(con_, "DB must be open.");
        disconnectFromDb(cast(PGconn*)con_);
        cast()con_ = null;
        cast()cptTbl_ = null;
    }

    //---***---***---***---***---***--- private ---***---***---***---***---***--

    /// Pointer to connection.
    private PGconn* con_;

    /// Pointer to the concepts table control structure
    private ConceptsTable* cptTbl_;

    //---***---***---***---***---***--- functions ---***---***---***---***---***--
}

/// Dynamic concept descriptor - structure of the crank enums.
struct DcpDsc {
    string className;      // named concept's class
    Cid cid;                // named concept's cid
}

/// Enum template for declaring named dynamic concepts. Used in the crank modules.
enum cd(T : SpiritDynamicConcept, Cid cid)  = DcpDsc(T.stringof, cid);





















