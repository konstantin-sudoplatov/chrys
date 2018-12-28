package atn

import basemain.Cid
import chribase_thread.CuteThread
import cpt.A
import cpt.Breed
import cpt.CuteThreadPrem
import cpt.SpBreed
import cpt.abs.Concept
import cpt.abs.DynamicConcept
import cpt.abs.Neuron
import libmain.BranchRequestsPodpoolCreateChildMsg
import libmain._pp_
import libmain._sm_
import libmain.hardCrank

/**
 *      All reasoning takes place in this class. All branches are packed in pods and those in the pod pool.
 *  Parent-child relationships of the branches has nothing to do with parent-child relationships of the threads(pods). Pods
 *  are children of the main branch, where they are started. Branches are born, live and terminate in their own logical
 *  hierarchy.
 *
 *  No sharing, no synchronization, all work takes place in one thread.
 *
 *  @param breedCid Cid of the breed concept for the branch.
 *  @param ownBrid Brid object, that identifies its place in the pod and pod pool.
 *  @param parentBrid parent's ownBrid. Can be null if it's root.
 */
open class Branch(
    breedCid: Cid,
    val ownBrid: Brid,         // own address
    val parentBrid: Brid?    // parent's address
) {

    /**
     *      It is a heart of the system. In here we calculate activation of a current neuron (the stem) and take decision
     *  what actions should be done, which branches spawned, and which neuron will become our next stem or should we yield
     *  the flow control and wait until conditions change and the next call comes.
     */
    fun reasoning() {
        var stem = stem_

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
                for(destBreedCid in eff.branches) {

                    // Form an array of cloned from the current branch ins of the destination breed
                    val insCids = (_sm_[destBreedCid] as SpBreed).ins
                    val clonedIns = if(insCids != null) Array<DynamicConcept>(insCids.size)
                        { this[insCids[it]].clone() as DynamicConcept} else null

                    _pp_.putInQueue(BranchRequestsPodpoolCreateChildMsg(destBreedCid, destIns = clonedIns, parentBrid = ownBrid))
                }

            // Assign new stem or yield
            if(eff.stemCid != 0)
                stem = this[eff.stemCid] as Neuron
            else
                break
        }

        // Save stem and exit
        stem_ = stem
    }

    /**
     *      Add concept to live map bypassing the spirit map. Used for concept injections.
     *  @param cpt live dynamic concept
     */
    fun add(cpt: DynamicConcept) {
        liveMap_[cpt.cid] = cpt
    }

    /**
     *          Get live concept from local map. If not present, create it.
     */
    operator fun get(cid: Cid): Concept {

        var cpt = liveMap_[cid]
        if(cpt != null)
            return liveMap_[cid] as Concept
        else
        {
            cpt = _sm_[cid].liveFactory()
            liveMap_[cid] = cpt

            return cpt
        }
    }

    /**
     *      Add a child branch to the list of children.
     *  @param childBrid
     */
    fun addChild(childBrid: Brid) {
        children.add(childBrid)
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%--- private data ---%%%---%%%---%%%---%%%---%%%---%%%

    /** Branch-local map of live concepts */
    private val liveMap_ = hashMapOf<Cid, Concept>()

    /** The head neuron of the branch. Initially it's the seed from the breed concept. */
    private var stem_: Neuron = this[(this[breedCid].sp as SpBreed).seedCid] as Neuron

    /** List of child branches. Used to send them the termination message. */
    private val children = ArrayList<Brid>()

    /**
     *      Main constructor.
     */
    init {
        // Set up the breed
        @Suppress("LeakingThis")
        val breed = this[breedCid] as Breed
        breed.brid = ownBrid
        breed.activate()
    }
}

/**
 *      Attention circle. It is the root branch for all the branch tree that communicates with userThread.
 *  @param breedCid Cid of the breed concept for the branch.
 *  @param brid Brid object, that identifies its place in the pod and pod pool.
 *  @param userThread User thread.
 */
class AttentionCircle(breedCid: Cid, brid: Brid, userThread: CuteThread): Branch(breedCid, brid, null) {
    init {

        // Inject the userThread_prem hard cid premise
        val userThreadPrem = this[hardCrank.hardCids.userThread_prem.cid] as CuteThreadPrem
        userThreadPrem.thread = userThread
        userThreadPrem.activate()
    }
}