package atn

/**
 *      All reasoning takes place in this class. All branches are packed in pods and them in their turn in the pod pool.
 *  Parent-child relationships of the branches has nothing to do with parent-child relationships of the threads(pods). Pods
 *  are children of the libmain branch, where they are started. Branches are born, live and terminate in their own logical
 *  hierarchy.
 */
open class Branch {

}

class AttentionCircle: Branch() {

}