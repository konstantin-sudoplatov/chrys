module stat.stat_registry;
import std.traits, std.conv;

import proj_shared, proj_tools;

import stat.stat_types;
import atn.atn_circle_thread;
import chri_types: StatCallType;

/// Full list of modules, that contain static concept functions. This list is used at compile time to gather all static
/// concepts and put them in the spirit map and their names and cids in the name map.
enum StatConceptModules {
    pile = "stat.stat_main",
    subpile = "stat.substat.substat_subpile"
}

//---***---***---***---***---***--- functions ---***---***---***---***---***--

/**
        Create array of static concept descriptors, CTFE. Used to create the manifest constant array statDescriptors.
    Returns: array of static concept descriptors.
*/
StatDescriptor[] createStatDescriptors() {

    // Declare named static descriptor array
    StatDescriptor[] sds;

    // Fill the named descriptors array
    StatDescriptor sd;
    static foreach(moduleName; [EnumMembers!StatConceptModules]) {
        mixin("import " ~ moduleName ~ ";");
        static foreach(memberName; __traits(allMembers, mixin(moduleName))) {
            static if(__traits(isStaticFunction, __traits(getMember, mixin(moduleName), memberName))) {
                sd.cid = __traits(getAttributes, mixin(memberName))[0];
                sd.name = memberName;
                sd.fp = mixin("&" ~ memberName);
                static assert(is(typeof(mixin("&"~memberName))== type!(__traits(getAttributes, mixin(memberName))[1])),
                        memberName ~ ": annotated type " ~ __traits(getAttributes, mixin(memberName))[1] ~
                        " doesn't match with real type " ~ typeof(mixin("&" ~ memberName)).stringof);
                sd.call_type = __traits(getAttributes, mixin(memberName))[1];
                sds ~= sd;
            }
        }
    }

    // Sort it
    import std.algorithm.sorting: sort;
    sds.sort;

    // Check if cids in the array are unique.
    Cid lastCid = 0;
    foreach(st; sds) {
        assert(st.cid > lastCid, "cid: "~ to!string(st.cid) ~ ", name: " ~ st.name ~
                " - cids cannot be used multiple times.");
        lastCid = st.cid;
    }

    return sds;
}

/**
        Create array of unused cids.
    Parameters:
        descArray = either statDecsriptors_ or DynDescriptors_
    Returns: array of free cids
*/
Cid[] findUnusedStatCids() {
    Cid[] unusedCids;

    // find unused cids
    Cid lastCid = 0;
    foreach(sd; createStatDescriptors) {
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

//===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
//
//                                  Private
//
//===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@

//---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

/**
        Convert name of type represented as a string to real type. Don't forget, the type you a trying to instantiate must
    exist, i.e. be imported or be a basic type.
    Parameters:
        typeName = name of type
*/
private template type(string typeName) {
    mixin("alias type = " ~ typeName ~ ";");
}

//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--

/// Info about static concept descriptor (it's all you need to call that function). This structure is used to
/// gather together name/cid pairs for dynamic concepts. Arrays of this structures will be stored as enums at compile
/// time for following processing them to fill in the holy and name maps.
private struct StatDescriptor {
    Cid cid;                        /// cid of the concept
    string name;                    /// concept's name
    void* fp;                       /// pointer to the function
    StatCallType call_type;         /// call аgreement for the function

    /// Reload opCmp to make it sortable on cid (not nescessary, actually, since cid is the first field in the structure).
    int opCmp(ref const StatDescriptor s) const {
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
    import atn.atn_circle_thread: Caldron;

    // Stat concept to make a test call
    @(1, StatCallType.p0Calp1Cid) static void fun(Caldron spaceName, Cid cid) {
        assert(spaceName is null && cid ==0);
    }

    // extract the descriptor, cid and name from concept's annotation and declaration
    const StatDescriptor sd =
            StatDescriptor(__traits(getAttributes, fun)[0], "fun", &fun, __traits(getAttributes, fun)[1]);
    assert(sd.call_type == StatCallType.p0Calp1Cid);

    // use the descriptor form the map to call the concept.
    auto fp = cast(void function(Caldron, Cid))sd.fp;
    fp(null, 0);
}
