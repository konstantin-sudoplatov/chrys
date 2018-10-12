module chri_shared;
import std.conv, std.format;

import project_params, tools;

import cpt.cpt_registry, cpt.cpt_abstract, cpt.cpt_stat;
import attn.attn_circle_thread;

//---***---***---***---***---***--- data ---***---***---***---***---***--

// Key shared data structures
shared SpiritMap _sm_;        /// The map of holy(stable and storrable and shared) concepts.
immutable string[Cid] _nm_;   /// name/seed map
immutable TypeInfo_Class[] _sp_cl_reg_;  /// Spirit concept classes registry (classinfo by clid).
debug {
    // set to true after the maps are filled in with names,cids and references to the concept objects
    immutable bool _maps_filled_;

    // set to true after the cranking is finished and the maps rehashed
    immutable bool _cranked_;
}

shared static this() {
    _sp_cl_reg_ = cast(immutable)createSpiritClassesRegistry;
}

//---***---***---***---***---***--- functions ---***---***---***---***---***--

/**
            Check up availability and type of a concept by its cid.
    Parameters:
        T = type to check against
        cid = cid of a concept, that is checked
*/
void checkCid(T: SpiritConcept)(Cid cid) {
    debug if(_maps_filled_) {
        assert(cid in _sm_, format!"Cid: %s(%s) do not exist in the holy map."(cid, _nm_[cid]));
        assert(cast(T)_sm_[cid],
                format!"Cid: %s, must be of type %s and it is of type %s."(cid, T.stringof, typeid(_sm_[cid])));
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

    //---***---***---***---***---***--- types ---***---***---***---***---***---***

    //---***---***---***---***---***--- data ---***---***---***---***---***--

    /**
        Constructor
    */
    this(){
        // Initialize random generator
        rnd_ = Random(unpredictableSeed);
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
                Assign/construct-assign new holy map entry. If cid had not been assigned to the cpt yet, it is generated.
        Parameters:
            cpt = shared concept to assign
    */
    SpiritConcept add(SpiritConcept cpt)
    in {
        assert(cpt !in this, "Cid " ~ to!string(cpt.cid) ~ " - this cid already exists in the holy map.");
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
        return cast()spiritMap_[cid];
    }

    /**
                Overload for "in".
        Parameters:
            cid = cid of the concept.
        Returns: pointer to the concept or null
    */
    SpiritConcept* opBinaryRight(string op)(Cid cid) {
        return cast(SpiritConcept*)(cid in spiritMap_);
    }

    /// Ditto.
    SpiritConcept* opBinaryRight(string op)(SpiritConcept cpt) {
        return cast(SpiritConcept*)(cpt.cid in spiritMap_);
    }

    /**
                Pass through for byKey.
        Returns: range of cids
    */
    auto byKey() {
        return (cast()spiritMap_).byKey;      // need to cast off the shared attribute to avoid a compiler error
    }

    /**
                Pass through for byValue.
        Returns: range of concepts
    */
    auto byValue() {
        return (cast()spiritMap_).byValue;      // need to cast off the shared attribute to avoid a compiler error
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
        } while(cid in spiritMap_);        // do until not repeated in the map

        return cid;
    }
}
