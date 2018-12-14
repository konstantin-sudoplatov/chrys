package atn

import chribase_thread.CuteThread

/**
 *      Full address of branch.
 *  @param pod pod object
 *  @param branchInd index of the branch in the pod
 */
data class Brid(val pod: Pod, val branchInd: Int)

/**
 *      This is a thread, that contains a number of branches.
 */
class Pod(): CuteThread() {

}
