/// Shared memory, global parameters.
module global;
import std.concurrency;
import std.traits;

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

/**
            Fill in static concepts into the holy map.;
    Parameters:
        hm = holy map to fill
*/
void fillInStaticConcepts_(shared HolyMap hm) {
    import std.stdio;
    import std.typecons;

    alias NamedStaticConcept = Tuple!(string, "name", shared HolyConcept, "obj");    // static concept plus its name
    NamedStaticConcept namedCpt;
    NamedStaticConcept[] namedCpts;
    StatDescriptor sd;
    shared StaticConcept cpt;
    static foreach(moduleName; [EnumMembers!StatConceptModules])
        static foreach(memberName; __traits(allMembers, mixin(moduleName))) {
            static if (__traits(isStaticFunction, __traits(getMember, mixin(moduleName), memberName))) {
                sd.fun_ptr = mixin("&" ~ memberName);
                sd.call_type = __traits(getAttributes, mixin(memberName))[1];
                cpt = new shared StaticConcept(__traits(getAttributes, mixin(memberName))[0], sd);
                namedCpt.name = memberName;
                namedCpt.obj = cpt;
                namedCpts ~= namedCpt;
            }
        }

    foreach(nc; namedCpts){
        writeln(nc.name);
        writeln((cast(shared StaticConcept)nc.obj).sd.fun_ptr);
        writeln((cast(shared StaticConcept)nc.obj).sd.call_type);
    }
}

enum aaa = createNamedStaticConceptDescriptors();

auto createNamedStaticConceptDescriptors() {
    import std.typecons;
    struct NamedStatConcept

    int[] ar;
    ar ~= 1;
    ar ~= 2;
    ar ~= 3;

    return ar;
}

///     caldron/cid map, where "cid" is the cid of the seed neuron of the reasoning branch as an identifier of the branch
/// and caldron. We will need synchronization, because the map can be concurrently accessed by different caldrons.
shared Tid[Cid] _caldronMap_;


//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--


//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
