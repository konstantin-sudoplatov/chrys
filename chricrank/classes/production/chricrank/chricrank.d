module chricrank;
import std.conv;

import project_params, tools;

import chri_shared;
import stat.stat_registry;
import crank.crank_registry;
import cpt.cpt_actions, cpt.cpt_neurons, cpt.cpt_premises, cpt.cpt_stat;

void main()
{
    loadAndCrank_(_sm_, _nm_);
}

//###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
//
//                               Private
//
//###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

//---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

/**
        Fully prepare spirit and name maps based on the stat and crank modules.
    Parameters:
        sm = spirit map
        nm = name map
*/
private void loadAndCrank_(ref shared SpiritMap sm, ref shared string[Cid] nm) {
    // Create and initialize the key shared structures
    sm = new shared SpiritMap;
    // static concepts from the stat modules, dynamic concept names and uncrunk objects from the crank modules
    loadConceptMaps_(sm, nm);
    debug
        cast()_maps_filled_ = true;

    // Crank the system, i.e. setup manually programed dynamic concepts
    runCranks;
    import std.stdio: writefln;
    writefln("Some free dynamic cids: %s", sm.generate_some_cids(5));

    // Remove from the name map entries not related to the concepts.
    cleanupNotUsedNames;
    sm.rehash;
    nm.rehash;
    debug
        cast()_cranked_ = true;
}

/**
            Fill in gathered in statDescriptors_ and dynDescriptors_ info into the holy map and name map.
    Parameters:
        sm = spirit map
        nm = name map
*/
private void loadConceptMaps_(shared SpiritMap sm, shared string[Cid] nm)
out{
    assert(sm.length == statDescriptors.length + dynDescriptors.length);
    assert(nm.length == statDescriptors.length + dynDescriptors.length);
}
do {
    import std.stdio: writefln;

    // Accept static concepts and their names from the statDescriptors_ enum
    foreach(sd; statDescriptors) {
        assert(sd.cid !in sm, "Cid: " ~ to!string(sd.cid) ~ ". Cids cannot be reused.");
        sm.add(new shared SpStaticConcept(sd.cid, sd.fun_ptr, sd.call_type));
        nm[sd.cid] = sd.name;
    }

    // report static cids usage
    writefln("Unused static cids: %s", unusedStaticCids);
    writefln("Last used static cid: %s", statDescriptors[$-1].cid);

    // Accept dynamic concept names from the dynDescriptors_ enum
    foreach(dd; dynDescriptors) {
        assert(dd.cid !in nm);
        nm[dd.cid] = dd.name;
    }

    // Create dynamic concepts based on the dynDescriptors_ enum
    static foreach(dd; dynDescriptors) {
        sm[] = mixin("new shared " ~ dd.class_name ~ "(" ~ to!string(dd.cid) ~ ")");
    }
}
