package atn

import basemain.DEFAULT_MAX_THREAD_QUEUE
import basemain.DEFAULT_THREAD_QUEUE_TIMEOUT
import chribase_thread.CuteThread
import libmain.POD_POOL_SIZE
import java.util.*
import kotlin.Comparator

/**
 *      Full address of branch in the pod pool
 *  @param pod pod object
 *  @param podInd podIndex of the branch in the pod
 */
data class Brid(val pod: Pod, val podInd: Int)

/**
 *      This is a thread, that contains a number of branches.
 *  @param podName Alias for threadName
 *  @param podIndex Index of the pod in the pool's array of pods
 */
class Pod(podName: String, val podIndex: Int): CuteThread(DEFAULT_THREAD_QUEUE_TIMEOUT, DEFAULT_MAX_THREAD_QUEUE, podName)
{
    /** Alias for threadName */
    val podName: String
        inline get() = threadName

    /** Number of branches currently assigned to the pod. */
    internal var numOfBranches = 0

    override fun toString(): String {
        var s= super.toString()
        s += "\n    numOfBranches = ${numOfBranches}"
        s += "\n    podIndex = ${podIndex}"
        return s
    }
}

/**
 *      Comparator for pods is needed for building sorted set of pods (TreeSet<Pod>).
 */
class PodComparator: Comparator<Pod>
{
    override fun compare(o1: Pod?, o2: Pod?): Int {
        assert(value = o1 != null && o2 != null)

        if
                (o1!!.numOfBranches != o2!!.numOfBranches)
            return if(o1.numOfBranches < o2.numOfBranches) -1 else 1
        else
            if
                    (o1.podIndex != o2.podIndex)
                return if(o1.podIndex < o2.podIndex) -1 else 1
            else
                return 0
    }
}

/**
 *      Pool of pods. It is of fixed size and populated with running pods (they are started on the pool construction).
 *  @param size number of pods in the pool
 */
class PodPool(val size: Int = POD_POOL_SIZE)
{
    // Create and start all pods
    internal var podArray: Array<Pod> = Array<Pod>(size, {Pod("pod_$it", it).also {it.start()}})

    /** Sorted set of pods. Pods are sorted by their usage number, plus a unique id to avoid repetition. */
    internal val podSet = TreeSet<Pod>(PodComparator())

    init {
        // Register all pods in the tree set
        for(pod in podArray) podSet.add(pod)
    }
}
