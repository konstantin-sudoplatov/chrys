package atn

import chribase.DEFAULT_MAX_THREAD_QUEUE
import chribase.DEFAULT_THREAD_QUEUE_TIMEOUT
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
class Pod(podName: String): CuteThread(DEFAULT_THREAD_QUEUE_TIMEOUT, DEFAULT_MAX_THREAD_QUEUE, podName) {
    
    /** Alias for thrName */
    val podName: String
        inline get() = threadName
}
