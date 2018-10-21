module chricrank;
import std.stdio;
import std.conv, std.format, std.exception;

import proj_data, proj_funcs;
import db.db_main, db.db_concepts_table;

import chri_types, chri_data;
import stat.stat_registry;
import cpt.cpt_registry, cpt.abs.abs_concept;
import crank.crank_registry;
import cpt.cpt_actions, cpt.cpt_neurons, cpt.cpt_premises, cpt.cpt_stat;

void main()
{
    SpiritManager spiritMan;
    spiritMan.openDatabase;
    scope(exit) spiritMan.closeDatabase;

    logUnusedClids;

    // Fill and crank main maps with static and hardcoded dynamic concepts and their names.
    loadAndCrank_(_sm_, _nm_);

    // Add or update spirit concepts in DB
    Cind added;
    Cind updated;
    foreach(cid; _sm_.keys) {
        const SpiritConcept smCpt = _sm_[cid];
        if      // is it a static concept?
                (smCpt.cid <= MAX_STATIC_CID)
            // no DB for it
            continue;

        const SpiritConcept dbCpt = spiritMan.retrieveConcept(cid, 0);
        if      // isn't the concept in DP?
                (!dbCpt)
        {   //no: add it
            spiritMan.insertConcept(smCpt);
            added++;
        }
        else if // is the concept in DP different from the newly cranked?
                (dbCpt != smCpt)
        {   //yes: update it
            enforce(cid in _nm_, format!("Cid %s is used by for a concept in DB, and it's not present in the name map." ~
                    " May be we are trying to reuse this cid?")(cid));
            spiritMan.updateConcept(smCpt);
            updated++;
        }
    }

    logit(format!"%s concepts loaded into the spirit map, %s names loaded into the name map"(_sm_.length, _nm_.length));
    logit(format!"%s concepts added to DB, %s concepts updated in DB."(added, updated));
}

//###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
//
//                               Private
//
//###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

//---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

/**
        Create and initialize the key shared structures, namely the spirit and name maps based on the stat and crank modules.
    The spirit map is synchronized store for all concepts. The name map holds names for the named concepts. Those are
    the concepts that code is aware of. The names are defined in the enums in the crank modules. Actually code works with
    them based on those enums, the map is more for debugging. It is immutable, so shared easily.

    Parameters:
        sm = spirit map
        nm = name map
*/
private void loadAndCrank_(ref shared SpiritMap sm, ref immutable string[Cid] nm) {

    // Create spirit map
    sm = new shared SpiritMap;

    // Load static concepts from the stat modules. Load dynamic named concept from the enums, located in the crank modules.
    loadConceptMaps_(sm, nm);
    debug
        cast()_maps_filled_ = true;     // raise the "filled" flag

    // Crank the system, i.e. setup manually programed dynamic concepts
    runCranks;
    import std.stdio: writefln;
    writefln("Some free dynamic cids: %s", sm.generate_some_cids(5));

    // Remove from the name map entries that have no corresponding the concepts.
    cleanupNotUsedNames;
    sm.rehash;
    (cast()nm).rehash;
    debug
        cast()_cranked_ = true;         // raise the "cranked" flag
}

/**
            Gather the static and dynamic concepts info from the stat and crank modules and put it to the spirit and name maps.
    Parameters:
        sm = spirit map
        nm = name map
*/
private void loadConceptMaps_(shared SpiritMap sm, ref immutable string[Cid] nm) {
    import std.stdio: writefln;

    // Load static concepts and their names to the sm and nm maps.
    auto statDescriptors = createStatDescriptors;
    foreach(sd; statDescriptors) {
        assert(sd.cid !in sm, "Cid: " ~ to!string(sd.cid) ~ ". Cids cannot be reused.");
        sm.add(new SpStaticConcept(sd.cid, sd.fp, sd.call_type));
        cast()nm[sd.cid] = sd.name;
    }

    // report static cids usage
    writefln("Unused static cids: %s", findUnusedStatCids);
    writefln("Last used static cid: %s", statDescriptors[$-1].cid);

    // Accept dynamic concepts and their names,
    enum dynDescriptors = createDynDescriptors;
    foreach(dd; dynDescriptors) {
        assert(dd.cid !in nm);
        cast()nm[dd.cid] = dd.name;
    }

    // Create dynamic concepts based on the dynDescriptors_ enum
    static foreach(dd; dynDescriptors) {
        sm.add(mixin("new " ~ dd.class_name ~ "(" ~ to!string(dd.cid) ~ ")"));
    }
}
