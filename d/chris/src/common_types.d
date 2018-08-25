module common_types;
import std.stdio;

import global_data, tools;

//---***---***---***---***---***--- types ---***---***---***---***---***---***

/// Concept identifier is 4 bytes long at the moment.
alias Cid = uint;

/// Call types of the static concept functions
enum CallType {
    type1,      // Cid function(Caldron nameSpace, Cid[] paramCids, Object extra)
    type2,      // Cid[] function(Caldron nameSpace, Cid[] paramCids, Object extra)
}

/// Annotation for the static concept functions
struct AnnoStat{
    Cid cid;            // cid of concept
    CallType type;      // call аgreement for the concept function
}

struct StatDir {

    void* opIndexAssign(void* value, size_t index) {
        return funMap[cast(Cid)index] = value;
    }

    void* opIndex(size_t index) {
        return funMap[cast(Cid)index];
    }

    private:
    void*[Cid] funMap;      /// Map of static concept function pointers by cid
}

unittest {
    StatDir sd;
    import attn.attn_circle_thread;
    static Cid fun(Caldron spaceName, Cid[] cid, Object extra) {
        assert(spaceName is null && cid is null && extra is null);
writeln("here");
        return 0;
    }

    sd[1] = &fun;
    auto fp = cast(Cid function(Caldron, Cid[], Object))sd[1];
    fp(null, null, null);
}

/**
            It is a wrapper for caldron/seed map.
    Here "seed" is the cid of the seed neuron of the reasoning branch as an identifier of the branch and caldron.
    We will need synchronization, because this map will be concurrently accessed by different caldrons, so it is a class,
    just not to introduce a separate mutex object.
*/
pure @safe nothrow class CaldronMap {
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
