module chricrank;
import std.stdio;
import std.conv, std.format, std.exception, std.algorithm, std.typecons;

import proj_data, proj_funcs;
import db.db_main, db.db_concepts_table;

import chri_types, chri_data;
import cpt.cpt_registry, cpt.abs.abs_concept, cpt.abs.abs_neuron;
import cpt.cpt_actions, cpt.cpt_neurons, cpt.cpt_premises, cpt.cpt_primitives, cpt.cpt_stat;

import stat.stat_registry;
import crank.crank_registry;
import crank.crank_types;

void main()
{
    SpiritManager spiritMan;
    spiritMan.openDatabase;
    scope(exit) spiritMan.closeDatabase;

    logUnusedClids;

    // Fill and crank main maps with static and hardcoded dynamic concepts and their names.
    loadAndCrank_(_sm_, _nm_);

    // Additional logical verification of the cranked concepts.
    checkCranking_;

    // Add or update spirit concepts in DB
    Cind added;
    Cind updated;
    foreach(cid; _sm_.keys) {
        const SpiritConcept smCpt = _sm_[cid];
        if      // is it a static concept?
                (smCpt.cid <= MAX_STATIC_CID)
            // no DB for it
            continue;

        SpiritConcept dbCpt;
        try {
            dbCpt = spiritMan.retrieveConcept(cid, 0);
        } catch(Exception e) {
            goto UPDATE;
        }
        if      // isn't the concept in DP?
                (!dbCpt)
        {   //no: add it
            spiritMan.insertConcept(smCpt);
            added++;
        }
        else if // is the concept in DP different from the newly cranked?
                (dbCpt != smCpt)
        {   //yes: update it
        UPDATE:
            enforce(cid in _nm_, format!("Cid %s is used by for a concept in DB, and it's not present " ~
                    "in the name map. Are we trying to reuse this cid?")(cid));
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
    import std.stdio: writeln, writef;
    writef("Some free dynamic cids: %,?s", '_', sm.generate_some_cids(1)[0]);
    foreach(cid; sm.generate_some_cids(7))
        writef(", %,?s", '_', cid);
    writeln;

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

/**
        Check logical consistency of concepts. It'll the checks that might be cumbersome in the asserts and contracts
        of the main code.
*/
private void checkCranking_() {
    foreach(cid, cpt; _sm_.spiritMap)
        if(auto c = cast(SpiritNeuron)cpt)
            checkNeuron(cid, c);
}

private void checkNeuron(Cid cid, SpiritNeuron cpt) {

    // There can be no more then one next head
    foreach(effect; cpt.effects) {
        Cind cnt = effect.branches.fold!((c, cid){ return (cast(SpiritNeuron)_sm_[cid])? ++c: c; })(0);
        assert(cnt <= 1, "There are %s next heads in the %s(%,?s), but there can only be one or zero.".format(cnt,
                cptName(cid), '_', cid));
    }

    // Currently only AndNeurons are allowed to breed
    if(!cast(SpAndNeuron)cpt)
        foreach(effect; cpt.effects) {
            bool hasBreed = effect.branches.canFind!(cid => cast(SpBreed)_sm_[cid] !is null);
            assert(!hasBreed, "%s(%,?s) of type %s has breeds, but currently only SpAndNeuron is allowed to have them.".
                    format(cptName(cid), '_', cid, '_', typeid(_sm_[cid])));
        }

    // When the AndReurons are going to breed, there must be a check that the branch is not breeded already.
    if(auto an = cast(SpAndNeuron)cpt){
        foreach(effect; cpt.effects) {
            auto effBreeds = effect.branches.filter!(cid => cast(SpBreed)_sm_[cid]);   // range of breeds in the effects array
            foreach(breed; effBreeds) {
                auto premBreeds = an.premises.filter!(prem => cast(SpBreed)_sm_[prem.cid] && // range of negated breeds in premises
                        prem.negation).map!(prem => prem.cid);
                assert(premBreeds.canFind(breed),
                        "In %s(%,?s) breed %s(%,?s) has no corresponding check for inactivity in premises %s".
                        format(cptName(cid), '_', cid, '_', cptName(breed), '_', breed, '_', an.premises));
            }
        }
    }
}

// Enums for unittests
debug enum {
    nrn = cd!(SpAndNeuron, 3_462_176_707),
    headNrn1 = cd!(SpAndNeuron, 494_288_204),
    headNrn2 = cd!(SpAndNeuron, 2_698_686_338),
    graft1 = cd!(SpGraft, 2_421_951_107),
    breed1 = cd!(SpBreed, 2_258_569_046),
    peg1 = cd!(SpPegPrem, 1_985_996_641),
    peg2 = cd!(SpPegPrem, 17_020_653),
}

// Test neuron
//, , , , , , , 3_356_847_540
unittest {
    createTestEnvironment;

    cp!nrn.addEffs(
        float.infinity,
        null,
        [
            headNrn1,
            headNrn2,
            graft1,
        ]
    );
    //checkNeuron(nrn.cid, cast(SpiritNeuron)_sm_[nrn.cid]);    // error - 2 new heads
    _sm_.remove(nrn.cid); _sm_[] = new SpAndNeuron(nrn.cid);    // reset the neuron

    cp!nrn.addPrem(peg1);
    cp!nrn.addPrem(peg2);
    cp!nrn.addEffs(
        float.infinity,
        null,
        [
            headNrn1,
            breed1,
        ]
    );
    //checkNeuron(nrn.cid, cast(SpiritNeuron)_sm_[nrn.cid]);       // error - no check in premises for a breed
    cp!nrn.addPrem(Yes.negate, breed1);
    checkNeuron(nrn.cid, cast(SpiritNeuron)_sm_[nrn.cid]);       // ok

    clearTestEnvironment;
}

debug void createTestEnvironment() {
    import proj_memoryerror: registerMemoryErrorHandler;
    registerMemoryErrorHandler;
    _sm_ = new shared SpiritMap;
    _sm_[] = new SpAndNeuron(nrn.cid);  cast()_nm_[nrn.cid] = nrn.stringof;
    _sm_[] = new SpAndNeuron(headNrn1.cid);  cast()_nm_[headNrn1.cid] = headNrn1.stringof;
    _sm_[] = new SpAndNeuron(headNrn2.cid);  cast()_nm_[headNrn2.cid] = headNrn2.stringof;
    _sm_[] = new SpGraft(graft1.cid);  cast()_nm_[graft1.cid] = graft1.stringof;
    _sm_[] = new SpBreed(breed1.cid);  cast()_nm_[breed1.cid] = breed1.stringof;
    _sm_[] = new SpPegPrem(peg1.cid);  cast()_nm_[peg1.cid] = peg1.stringof;
    _sm_[] = new SpPegPrem(peg2.cid);  cast()_nm_[peg2.cid] = peg2.stringof;
}

debug void clearTestEnvironment() {
    import proj_memoryerror: deregisterMemoryErrorHandler;
    _sm_ = null;
    cast()_nm_ = null;
    deregisterMemoryErrorHandler;
}








