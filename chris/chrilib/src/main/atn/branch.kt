package atn

import basemain.Cid
import chribase_thread.CuteThread
import cpt.A
import cpt.Breed
import cpt.SpBreed
import cpt.abs.Concept
import cpt.abs.Neuron
import libmain.BranchRequestsCreationChildMsg
import libmain._pp_
import libmain._sm_

/**
 *      All reasoning takes place in this class. All branches are packed in pods and those in the pod pool.
 *  Parent-child relationships of the branches has nothing to do with parent-child relationships of the threads(pods). Pods
 *  are children of the main branch, where they are started. Branches are born, live and terminate in their own logical
 *  hierarchy.
 *
 *  No sharing, no synchronization, all work takes place in one thread.
 *
 *  @param breedCid Cid of the breed concept for the branch.
 *  @param brid Brid object, that identifies its place in the pod and pod pool.
 */
open class Branch(
    breedCid: Cid,
    val brid: Brid      // own address
) {

    /**
     *      It is a heart of the system. In here we calculate activation of a current neuron (the stem) and take decision
     *  what actions should be done, which branches spawned, and which neuron will become our next stem or should we yield
     *  the flow control and wait until conditions change and the next call comes.
     */
    fun reasoning() {
        var stem = _stem

        // Main reasoning cycle
        while(true) {

            // Do the neuron's assessment and determine effect
            val eff = stem.calculateActivationAndSelectEffect(this)

            // Do actions, if any
            if(eff.actions != null)
                for(actCid in eff.actions)
                    (this[actCid] as A).run(this)

            // Spawn branches, if any
            if(eff.branches != null)
                for(breedCid in eff.branches)
                    _pp_.putInQueue(BranchRequestsCreationChildMsg(breedCid, brid))

            // Assign new stem or yield
            if(eff.stemCid != 0)
                stem = this[eff.stemCid] as Neuron
            else
                break
        }

        // Save stem and exit
        _stem = stem
    }

    /**
     *          Get live concept from local map. If not present, create it.
     */
    operator fun get(cid: Cid): Concept {

        var cpt = _liveMap[cid]
        if(cpt != null)
            return _liveMap[cid] as Concept
        else
        {
            cpt = _sm_[cid].liveFactory()
            _liveMap[cid] = cpt

            return cpt
        }
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%--- private data ---%%%---%%%---%%%---%%%---%%%---%%%

    /** Branch-local map of live concepts */
    private val _liveMap = hashMapOf<Cid, Concept>()

    /** The head neuron of the branch. Initially it's the seed from the breed concept. */
    private var _stem: Neuron = this[(this[breedCid].sp as SpBreed).seedCid] as Neuron

    /**
     *      Main constructor.
     */
    init {
        // Set up the breed
        @Suppress("LeakingThis")
        val breed = this[breedCid] as Breed
        breed.brid = brid
        breed.activate()
    }
}

/**
 *      Attention circle. It is the root branch for all the branch tree that communicates with userThread.
 *  @param breedCid Cid of the breed concept for the branch.
 *  @param brid Brid object, that identifies its place in the pod and pod pool.
 *  @param user User thread.
 */
class AttentionCircle(breedCid: Cid, brid: Brid, user: CuteThread): Branch(breedCid, brid) {

}