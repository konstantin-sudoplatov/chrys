package atn

import basemain.Cid
import cpt.Breed
import cpt.SpBreed

/**
 *      All reasoning takes place in this class. All branches are packed in pods and them in their turn in the pod pool.
 *  Parent-child relationships of the branches has nothing to do with parent-child relationships of the threads(pods). Pods
 *  are children of the libmain branch, where they are started. Branches are born, live and terminate in their own logical
 *  hierarchy.
 *
 *  @param breedCid Cid of the breed concept for the branch.
 *  @param brid Brid object, that identifies its place in the pod and pod pool.
 */
open class Branch(breedCid: Cid, brid: Brid) {

    fun reasoning() {

    }
}

/**
 *      Attention circle. It is the root branch for all the branch tree that communicates with user.
 *  @param breed Breed concept for the branch with fully set up brid and the seed concept.
 *  @param brid Brid object, that identifies its place in the pod and pod pool.
 */
class AttentionCircle(breedCid: Cid, brid: Brid): Branch(breedCid, brid) {

}