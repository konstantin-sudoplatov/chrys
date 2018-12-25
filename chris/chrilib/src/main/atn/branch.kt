package atn

import basemain.Cid
import chribase_thread.CuteThread
import cpt.Breed
import cpt.SpBreed
import cpt.abs.Concept
import cpt.abs.Neuron
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
open class Branch(breedCid: Cid, brid: Brid) {

    fun reasoning() {
        var stem = _stem_

        _stem_ = stem
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

    /** The head neuron of the branch. */
    private var _stem_: Neuron = this[(this[breedCid].sp as SpBreed).seedCid] as Neuron

    /**
     *      Main constructor.
     */
    init {
        // Set up the breed
        @Suppress("LeakingThis")
        val breed = this[breedCid] as Breed
        breed.brid = brid
    }
}

/**
 *      Attention circle. It is the root branch for all the branch tree that communicates with user.
 *  @param breedCid Cid of the breed concept for the branch.
 *  @param brid Brid object, that identifies its place in the pod and pod pool.
 *  @param user User thread.
 */
class AttentionCircle(breedCid: Cid, brid: Brid, user: CuteThread): Branch(breedCid, brid) {

}