module common_types;
import std.stdio;

import global, tools;
import attn.attn_circle_thread;
import cpt.holy;


//---***---***---***---***---***--- types ---***---***---***---***---***---***

/// Call types of the static concept (static concept is function).
enum StatCallType {
    rCid_p0Cal_p1Cidar_p2Obj,           // Cid function(Caldron nameSpace, Cid[] paramCids, Object extra)
    rCidar_p0Cal_p1Cidar_p2Obj,         // Cid[] function(Caldron nameSpace, Cid[] paramCids, Object extra)
}

/// Static concept descriptor. It's all you need to call that function. Serves as a value in the StatCptMap, where cid is key.
struct StatDescriptor {
    void* fun_ptr;                  // cid of concept
    StatCallType call_type;          // call аgreement for the concept function
}

/// Get cid by static concept (it' a function, remember!) name.
template stat_cid(alias cptName)
        if      // annotation consists of two elements and their types are int and StatCallType?
                (__traits(getAttributes, cptName).length == 2 &&
                is(typeof(__traits(getAttributes, cptName)[0]) == int) &&
                is(typeof(__traits(getAttributes, cptName)[1]) == StatCallType))
{   // extract the first element of annotation, which is cid
    enum int stat_cid = __traits(getAttributes, cptName)[0];
}


///
unittest {

    // Stat concept to make a test call
    @(1, StatCallType.rCid_p0Cal_p1Cidar_p2Obj) static Cid fun(Caldron spaceName, Cid[] cid, Object extra) {
        assert(spaceName is null && cid is null && extra is null);
        return 0;
    }

    // extract the descriptor and cid from concept's annotation
    StatDescriptor sd = StatDescriptor(&fun, __traits(getAttributes, fun)[1]);    // its value
    Cid cid = __traits(getAttributes, fun)[0];      // its cid
    assert(stat_cid!fun == cid);    // check cid
    assert(sd.call_type == StatCallType.rCid_p0Cal_p1Cidar_p2Obj);

    // use the descriptor form the map to call the concept.
    auto fp = cast(Cid function(Caldron, Cid[], Object))sd.fun_ptr;
    fp(null, null, null);
}

/**
            Holy concepts map. It is a wrapper for actual associative array.
        Map of all static and dynamic shared storrable (holy) concepts. This map will be used concurrently by all caldrons,
    so it must be synchronized. At the moment, it is usual syncronization on the class object. In the future it can possibly
    be changed to atomic, because the concurrent asccess might be intensive. To that end acsses via the class methods would
    help, because this way we could get away with changes to only interface methods for the real map.
*/
shared pure @safe nothrow class HolyMap {

    //---***---***---***---***---***--- types ---***---***---***---***---***---***

    //---***---***---***---***---***--- data ---***---***---***---***---***--

    /**
        Constructor
    */
    this(){}

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
                Assign/constract-assign new holy map entry.
        Parameters:
            cpt = shared concept to assign
            cid = key
    */
    synchronized shared(HolyConcept) opIndexAssign(shared HolyConcept cpt, Cid cid) {
        holyMap_[cid] = cpt;
        return cpt;
    }

    /**
                Get a holy map entry.
        Parameters:
            cid = key
        Returns: shared concept
    */
    synchronized shared(HolyConcept) opIndex(Cid cid) {
        return holyMap_[cid];
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    private:
    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%
    HolyConcept[Cid] holyMap_;       /// map caldron[seed]

    //---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
}

///
unittest {
    shared HolyMap hm = new shared HolyMap;
    shared UnconditionalNeuron_hnr hnr = new shared UnconditionalNeuron_hnr;
    Cid cid = 1;
    hm[cid] = hnr;
    assert(hm[cid] is hnr);
}

/**
            It is a wrapper for caldron/seed map.
        Here "seed" is the cid of the seed neuron of the reasoning branch as an identifier of the branch and caldron.
    We will need synchronization, because this map will be concurrently accessed by different caldrons, so it is a class,
    just not to introduce a separate mutex object.
*/
shared pure @safe nothrow class CaldronMap {
    import std.concurrency: Tid;

    //---***---***---***---***---***--- types ---***---***---***---***---***---***

    //---***---***---***---***---***--- data ---***---***---***---***---***--

    /**
        Constructor
    */
    //this(){}

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    private:
    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%
    Tid[Cid] caldronMap_;       /// map caldron[seed]

    //---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
}

//---***---***---***---***---***--- data ---***---***---***---***---***--

/**
        Constructor
*/
//this(){}

//---***---***---***---***---***--- functions ---***---***---***---***---***--

//###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
//
//                               Private
//
//###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
private:
//---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
