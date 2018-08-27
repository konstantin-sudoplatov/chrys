/// Shared memory, global parameters.
module global;
import std.concurrency;
import std.traits;
import std.conv;

import tools;
import common_types;
import attn.attn_dispatcher_thread;
import cpt.holy;

// modules with static concepts
import stat.pile;
import stat.substat.subpile;

//---***---***---***---***---***--- types ---***---***---***---***---***---***

/// Concept identifier is 4 bytes long at the moment.
alias Cid = uint;

///
enum StatConceptModules {
    pile = "stat.pile",
    subpile = "stat.substat.subpile"
}

//---***---***---***---***---***--- data ---***---***---***---***---***--

//      Key threads of the project. The console thead will be spawned, but we don't need to remember its Tid. The circle
// knows it, it's enough.
immutable Tid _mainTid_;         /// Tid of the main thread
immutable Tid _attnDispTid_;     /// Attention dispatcher thread Tid

/// The map of holy(stable and storrable and shared) concepts.
shared HolyMap _hm_;

///     caldron/seed map
shared CaldronMap _cm_;

//---***---***---***---***---***--- functions ---***---***---***---***---***--

/**
    Spawn the key threads (console_thread, attention dispatcher), capture their Tids.
*/
shared static this() {

    // Capture Tid of the main thread.
    _mainTid_ = cast(immutable)thisTid;

    // Spawn the attention dispatcher thread.
    _attnDispTid_ = cast(immutable)spawn(&attention_dispatcher_thread);

    // Spawn the console thread thread.
    import console_thread: console_thread;
    spawn(&console_thread);

    // Create and initialize the holy map
    _hm_ = new shared HolyMap;
    _hm_.fillInStaticConcepts_;
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

/// Manifest constant array of descriptors of all static concepts of the project.
enum statDescriptors = createStaticConceptDescriptors_;

enum unusedCids = unusedCids_;

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

/**
        Create array of names plus static concept descriptors, CTFE.
    Used to create the manifest constant array namedStaticDescriptors.
    Returns: array of static concept descriptors.
*/
StatDescriptor[] createStaticConceptDescriptors_() {

    // Declare named static descriptor array
    StatDescriptor[] sds;

    // Fill the named descriptors array
    StatDescriptor sd;
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
    foreach(sd; statDescriptors) {
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
void fillInStaticConcepts_(shared HolyMap hm) {
    import std.stdio;

    foreach(sd; statDescriptors) {
        writefln("%s, %s, %s, %s", sd.cid, sd.name, sd.fun_ptr, sd.call_type);
    }

    // report static cids usage
    writefln("Unused cids: %s", unusedCids);
    writefln("Last used cid: %s", statDescriptors[$-1].cid);

    // fill the holy map
    //foreach(sd; statDescriptors)
    //    _hm_
}

//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
