module chri_types;
import std.conv, std.format;

import project_params;

import chri_data;
import cpt.cpt_abstract, cpt.cpt_stat;
import attn.attn_circle_thread;

//---***---***---***---***---***--- data ---***---***---***---***---***--

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

//---***---***---***---***---***--- types ---***---***---***---***---***---***

/**
            Holy concepts map. It is a wrapper for actual associative array.
        Map of all static and dynamic shared storrable (holy) concepts. This map will be used concurrently by all caldrons,
    so it must be synchronized. At the moment, it is usual syncronization on the class object. In the future it can possibly
    be changed to atomic, because the concurrent asccess might be intensive. To that end acsses via the class methods would
    help, because this way we could get away with changes to only interface methods for the real map.
*/
import std.random;
shared synchronized final pure nothrow class SpiritMap {

    //---***---***---***---***---***--- types ---***---***---***---***---***---***

    //---***---***---***---***---***--- data ---***---***---***---***---***--

    /**
        Constructor
    */
    this(){}

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
                Length of the map.
        Returns: the length of the map as in an AA.
    */
    auto length() {
        return holyMap_.length;
    }

    /**
                Assign/construct-assign new holy map entry. If cid had not been assigned to the cpt yet, it is generated.
        Parameters:
            cpt = shared concept to assign
    */
    shared(SpiritConcept) add(shared SpiritConcept cpt)
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
        holyMap_[cpt.cid] = cpt;

        return cpt;
    }

    /**
            Remove key from map. Analogously to the AAs.
        Parameters:
            cid = key
        Returns: true if existed, else false
    */
    bool remove(Cid cid) {
        return holyMap_.remove(cid);
    }

    /**
                Get concept by cid, an overload for the index operation.
        Parameters:
            cid = key
        Returns: shared concept
    */
    shared(SpiritConcept) opIndex(Cid cid) {
        return holyMap_[cid];
    }

    /**
                Add concept to the map, an overload for the index assignment operation.
            Example of usage:
        ---
            _hm_[] = cpt;
        ---
        Parameters:
            cpt = concept to add
        Returns: assigned concept
    */
    shared(SpiritConcept) opIndexAssign(shared SpiritConcept cpt) {
        return add(cpt);
    }

    /**
                Overload for "in".
        Parameters:
            cid = cid of the concept.
        Returns: pointer to the concept or null
    */
    shared(SpiritConcept*) opBinaryRight(string op)(Cid cid) {
        return cid in holyMap_;
    }

    /// Ditto.
    shared(SpiritConcept*) opBinaryRight(string op)(shared SpiritConcept cpt) {
        return cpt.cid in holyMap_;
    }

    /**
                Pass through for byKey.
        Returns: range of cids
    */
    auto byKey() {
        return (cast()holyMap_).byKey;      // need to cast off the shared attribute to avoid a compiler error
    }

    /**
                Pass through for byValue.
        Returns: range of concepts
    */
    auto byValue() {
        return (cast()holyMap_).byValue;      // need to cast off the shared attribute to avoid a compiler error
    }

    /**
                Rebuild associative array to make it more efficient.
    */
    void rehash() {
        holyMap_.rehash;
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
    private SpiritConcept[Cid] holyMap_;       /// map concept/cid

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
        } while(cid in holyMap_);        // do until not repeated in the map

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
