module chricrank;
import std.stdio;
import std.conv, std.format;

import derelict.pq.pq;
import project_params, tools;
import db.db_main, db.db_concepts_table;

import chri_shared;
import cpt.cpt_types, cpt.cpt_registry, cpt.abs.abs_concept;
import stat.stat_registry;
import crank.crank_registry;
import cpt.cpt_actions, cpt.cpt_neurons, cpt.cpt_premises, cpt.cpt_stat;

void main()
{
    const dbCptHnd = DbConceptHandler(0);
    logUnusedClids;

    // Fill and crank main maps with static and hardcoded dynamic concepts and their names.
    loadAndCrank_(_sm_, _nm_);

    // Add or update spirit concepts in DB
    Cind added;
    Cind updated;
    foreach(cid; _sm_.byKey) {
        const SpiritConcept smCpt = _sm_[cid];
        if      // is it a static concept?
                (smCpt.cid <= MAX_STATIC_CID)
            // no DB for it
            continue;

        const SpiritConcept dbCpt = dbCptHnd.retreiveConcept(cid, 0);
        if      // isn't the concept in DP?
                (!dbCpt)
        {   //no: add it
            dbCptHnd.insertConcept(smCpt);
            added++;
        }
        else if // is the concept in DP different from the newly cranked?
                (dbCpt != smCpt)
        {   //yes: update it
            dbCptHnd.updateConcept(smCpt);
            updated++;
        }
    }

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
        Fully prepare spirit and name maps based on the stat and crank modules.
    Parameters:
        sm = spirit map
        nm = name map
*/
private void loadAndCrank_(ref shared SpiritMap sm, ref immutable string[Cid] nm) {
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
    (cast()nm).rehash;
    debug
        cast()_cranked_ = true;
}

/**
            Fill in gathered in statDescriptors_ and dynDescriptors_ info into the holy map and name map.
    Parameters:
        sm = spirit map
        nm = name map
*/
private void loadConceptMaps_(ref shared SpiritMap sm, ref immutable string[Cid] nm) {
    import std.stdio: writefln;

    // Accept static concepts and their names from the statDescriptors_ enum
    auto statDescriptors = createStatDescriptors;
    foreach(sd; statDescriptors) {
        assert(sd.cid !in sm, "Cid: " ~ to!string(sd.cid) ~ ". Cids cannot be reused.");
        sm.add(new SpStaticConcept(sd.cid, sd.fun_ptr, sd.call_type));
        cast()nm[sd.cid] = sd.name;
    }

    // report static cids usage
    writefln("Unused static cids: %s", findUnusedStatCids);
    writefln("Last used static cid: %s", statDescriptors[$-1].cid);

    // Accept dynamic concept names from the dynDescriptors_ enum
    foreach(dd; createDynDescriptors) {
        assert(dd.cid !in nm);
        cast()nm[dd.cid] = dd.name;
    }

    // Create dynamic concepts based on the dynDescriptors_ enum
    static foreach(dd; createDynDescriptors) {
        sm.add(mixin("new " ~ dd.class_name ~ "(" ~ to!string(dd.cid) ~ ")"));
    }
}
