/// Shared memory, global parameters.
module global;
import std.concurrency;
import std.traits;
import std.conv;

import tools;
import attn_dispatcher_thread;
import cpt_holy;

//---***---***---***---***---***--- types ---***---***---***---***---***---***

/// Concept identifier is 4 bytes long at the moment.
alias Cid = uint;

/// Static cid range is from 1 to MAX_STATIC_CID;
enum MAX_STATIC_CID = 1000000;

// modules with static concepts
import stat_pile;
import stat_substat_subpile;

/// Full list of modules, that contain static concept functions. This list is used at compile time to gather together all static
/// concepts and put them in the holy map and their names and cids in the name map.
enum StatConceptModules {
    pile = "stat_pile",
    subpile = "stat_substat_subpile"
}

// modules with dynamic concept names and cranks
import crank_pile;
import crank_subcrank_subpile;

/// Full list of modules, that contain dynamic concept names.This list is used at compile time to gather together all dynamic
///// concept names along with cids and put them in the name map.
enum DynConceptNameEnums {
    pile = "crank_pile",
    subpile = "crank_subcrank_subpile"
}

/// It is a two-way map of concept name/cid. The concepts are both static and dynamic. Here are gathered all of the concepts
/// which have names, that are known to the compiler, t.e. can be manipulated directly from the code.
synchronized shared pure nothrow class NameMap {
    import tools: CrossMap;

    /**
                Check the length of the map.
            Returns: number of pairs in the map.
    */
    auto length() {
        return (cast()cross).length;
    }

    /**
            Check if the cid present in the map. Analogous to the D "in" statement.
    */
    const(string*) cid_in(Cid cid) {
        return (cast()cross).first_in(cid);
    }

    /**
            Check if the name present in the map. Analogous to the D "in" statement.
        Parameters:
            name = name of the concept.
    */
    const(Cid*) name_in(string name) const {
        return (cast()cross).second_in(name);
    }

    /**
            Get the cid of the concept by name.
        Parameters:
            name = name of the concept.
        Returns: cid of the concept.
        Throws: RangeError exception if the name is not found.
    */
    const(Cid) cid(string name) const {
        return (cast()cross).first(name);
    }

    /**
            Get the name of the concept by cid.
        Parameters:
            cid = cid of the concept
        Returns: name of the concept.
        Throws: RangeError exception if the cid is not found.
    */
    const(string) name(Cid  cid) const {
        return (cast()cross).second(cid);
    }

    /*
            Get the range of the cids.
        Returns: range of cids.
    */
    auto cids() {
        return (cast()cross).firsts;
    }

    /*
            Get a range of the namess.
        Returns: range of names.
    */
    auto names() {
        return (cast()cross).seconds;
    }

    /**
            Add a pair cid/name.
        Parameters:
            cid = cid of the concept
            name = name of the concept
    */
    void add(Cid cid, string name) {
        assert(!name_in(name) && !cid_in(cid), "Keys are already in the map. We won't want to have assimetric maps.");     // if not, we risk having assimetric maps.
        (cast()cross).add(cid, name);
        assert(name_in(name) && cid_in(cid));
    }

    /**
            Remove pair cid/name. If there is no such a pair, nothing happens.
        Parameters:
            cid = cid of the concept
            name = name of the concept
    */
    void remove(Cid cid, string name) {
        assert((!name_in(name) && !cid_in(cid)) || (name_in(name) && cid_in(cid)));
        (cast()cross).remove(cid, name);
        assert(!name_in(name) && !cid_in(cid));
    }

    private:
    CrossMap!(Cid, string) cross;   // we use the cross map implementation, adding interface pass-through methods, to make it readable
}

///
unittest {
    shared NameMap nm = new shared NameMap;
    nm.add(1, "firstCpt");
    assert(nm.length == 1);
    assert(nm.name_in("firstCpt"));
    assert(nm.cid_in(1));

    import std.array: array;
    import std.algorithm.iteration: sum, joiner;
    //  cm.add(1, "secondCpt");       // this will produce an error, because 1 is in the cross already. We won't want to end up with assimetric maps.
    nm.add(2, "secondCpt");
    assert(nm.name(2) == "secondCpt");
    assert(nm.cid("secondCpt") == 2);
    assert(nm.cids.sum == 3);
    assert(nm.names.joiner.array.length == 17);
    //import std.stdio: writeln;
    //writeln(nm.names);     // will produce ["firstCpt", "secondCpt"]

                // Makes the program crash with code -4. A bug in DMD, obviously.
    // throws RangeError on non-existent key
    //import core.exception: RangeError;
    //try {
    //    int cid = nm.cid("three");
    //} catch(RangeError e) {
    //    assert(e.msg == "Range violation");
    //}

    nm.remove(1, "firstCpt");
    assert(nm.length == 1);
    nm.remove(1, "firstCpt");  // nothing happens
    assert(nm.length == 1);
}

/**
            Holy concepts map. It is a wrapper for actual associative array.
        Map of all static and dynamic shared storrable (holy) concepts. This map will be used concurrently by all caldrons,
    so it must be synchronized. At the moment, it is usual syncronization on the class object. In the future it can possibly
    be changed to atomic, because the concurrent asccess might be intensive. To that end acsses via the class methods would
    help, because this way we could get away with changes to only interface methods for the real map.
*/
synchronized shared pure @safe nothrow class HolyMap {

    //---***---***---***---***---***--- types ---***---***---***---***---***---***

    //---***---***---***---***---***--- data ---***---***---***---***---***--

    /**
        Constructor
    */
    this(){}

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    auto length() {
        assert(holyMap_, "The map must be allocated.");
        return holyMap_.length;
    }

    /**
                Assign/constract-assign new holy map entry.
        Parameters:
            cpt = shared concept to assign
            cid = key
    */
    shared(HolyConcept) opIndexAssign(shared HolyConcept cpt, Cid cid) {
        holyMap_[cid] = cpt;
        return cpt;
    }

    /**
                Get a holy map entry.
        Parameters:
            cid = key
        Returns: shared concept
    */
    shared(HolyConcept) opIndex(Cid cid) {
        return holyMap_[cid];
    }

    /**
                Overload for "in".
        Parameters:
            cid = cid of the concept.
    */
    shared(HolyConcept*) opBinaryRight(string op)(Cid cid) {
        return cid in holyMap_;
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

//      Key threads of the project. The console thead will be spawned, but we don't need to remember its Tid. The circle
// knows it, it's enough.
immutable Tid _mainTid_;         /// Tid of the main thread
immutable Tid _attnDispTid_;     /// Attention dispatcher thread Tid

// Key shared data structures
shared NameMap _nm_;        /// name/seed two-way map
shared CaldronMap _cm_;     ///     caldron/seed map
shared HolyMap _hm_;        /// The map of holy(stable and storrable and shared) concepts.

//---***---***---***---***---***--- functions ---***---***---***---***---***--

/**
    Spawn the key threads (console_thread, attention dispatcher), capture their Tids.
*/
shared static this() {

    // Capture Tid of the main thread.
    _mainTid_ = cast(immutable)thisTid;

    // Spawn the attention dispatcher thread.
    _attnDispTid_ = cast(immutable)spawn(&attention_dispatcher_thread_func);

    // Spawn the console thread thread.
    import console_thread: console_thread_func;
    spawn(&console_thread_func);

    // Create and initialize the key shared structures
    _nm_ = new shared NameMap;
    _hm_ = new shared HolyMap;
    fillInStaticConcepts_(_hm_, _nm_);
    _cm_ = new shared CaldronMap;


}

///
unittest {

}

//###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
//
//                               Private
//
//###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
private:
//---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

/// Manifest constant array of descriptors (cids, names, pointers, call types) of all the static concepts of the project.
enum statDescriptors_ = createStaticConceptDescriptors_;

/// Manifest constant array of gaps in the static cids sequense, used for the static concepts.
enum unusedStaticCids = unusedCids_;

/// Manifest constant array of descriptors (cids, names) of all the named dynamic concepts of the project. Remember, that most
/// of the dynamic concepts are supposed to be unnamed in the sence, that they are not visible directly to the code.
enum dynDescriptors_ = createDynConceptDescriptors_;

/// Manifest constant array of gaps in the named dynamic cids sequense, used for the named dynamic concepts.
enum unusedDynamicCids = unusedDynamicCids_;

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

/**
        Create array of names plus static concept descriptors, CTFE.
    Used to create the manifest constant array namedStaticDescriptors.
    Returns: array of static concept descriptors.
*/
TempCptDescriptor[] createStaticConceptDescriptors_() {

    // Declare named static descriptor array
    TempCptDescriptor[] sds;

    // Fill the named descriptors array
    TempCptDescriptor sd;
    static foreach (moduleName; [EnumMembers!StatConceptModules]) {
        static foreach (memberName; __traits(allMembers, mixin(moduleName))) {
            static if (__traits(isStaticFunction, __traits(getMember, mixin(moduleName), memberName))) {
                sd.cid = __traits(getAttributes, mixin(memberName))[0];
                sd.name = memberName;
                sd.fun_ptr = mixin("&" ~ memberName);
                sd.call_type = __traits(getAttributes, mixin(memberName))[1];

                sds ~= sd;
            }
        }
    }

    // Sort it
    import std.algorithm.sorting: sort;
    sds.sort;

    return sds;
}

/**
        Create array of unused cids, CTFE.
    Returns: array of free cids
*/
Cid[] unusedCids_() {
    Cid[] unusedCids;

    // find unused cids
    Cid lastCid = 0;
    foreach(sd; statDescriptors_) {
        assert(sd.cid > lastCid, "cid " ~ to!string(sd.cid) ~ ": cids cannot be used multiple times.");
        if
            (sd.cid > lastCid + 1)
        {
            for(Cid j = lastCid + 1; j < sd.cid; ++j ) unusedCids ~= j;
        }
        lastCid = sd.cid;
    }

    return unusedCids;
}

/**
            Fill in static concepts into the holy map.
    Parameters:
        hm = holy map to fill
*/
void fillInStaticConcepts_(shared HolyMap hm, shared NameMap nm)
out{
    assert(_hm_.length == statDescriptors_.length);
    assert(_nm_.length == statDescriptors_.length);
}
do {
    import std.stdio;

    foreach(sd; statDescriptors_) {
        assert(sd.cid !in _hm_, "Cid: " ~ to!string(sd.cid) ~ ". Cids cannot be reused.");
        _hm_[sd.cid] = new shared StaticConcept(sd.cid, sd.fun_ptr, sd.call_type);
        _nm_.add(sd.cid, sd.name);

    }

    // report static cids usage
    writefln("Unused cids: %s", unusedCids);
    writefln("Last used cid: %s", statDescriptors_[$-1].cid);
}

//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--

/// Info about static concept descriptor (it's all you need to call that function) and also this structure is used to
/// gather together name/cid pairs for dynamic concepts. Arrays of this structures will be stored as enums at compile
/// time for following processing them to fill in the holy and name maps.
struct TempCptDescriptor {
    Cid cid;                        /// cid of the concept
    string name;                    /// concept's name
    void* fun_ptr;                  /// pointer to the function
    StatCallType call_type;         /// call аgreement for the function

    /// Reload opCmp to make it sortable on cid (not nescessary, actually, since cid is the first field in the structure).
    int opCmp(ref const TempCptDescriptor s) const {
        if(cid < s.cid)
            return -1;
        else if(cid > s.cid)
            return 1;
        else
            return 0;
    }
}

///
unittest {
    import attn_circle_thread: Caldron;

    // Stat concept to make a test call
    @(1, StatCallType.rCid_p0Cal_p1Cidar_p2Obj) static Cid fun(Caldron spaceName, Cid[] cid, Object extra) {
        assert(spaceName is null && cid is null && extra is null);
        return 0;
    }

    // extract the descriptor, cid and name from concept's annotation and declaration
    TempCptDescriptor sd = TempCptDescriptor(__traits(getAttributes, fun)[0], "fun", &fun, __traits(getAttributes, fun)[1]);
    assert(sd.call_type == StatCallType.rCid_p0Cal_p1Cidar_p2Obj);

    // use the descriptor form the map to call the concept.
    auto fp = cast(Cid function(Caldron, Cid[], Object))sd.fun_ptr;
    fp(null, null, null);
}
