package atn

import basemain.*
import chribase_thread.CuteThread
import chribase_thread.MessageMsg
import chribase_thread.TerminationRequestMsg
import chribase_thread.TimeoutMsg
import cpt.Breed
import libmain.*
import java.util.*
import kotlin.Comparator
import kotlin.random.Random

/**
 *      Full address of branch in the pod pool
 *  @param pod pod object
 *  @param cellid Cell identifier - branch identifier in pod. It is an integer key in the branch map the pod object.
 */
data class Brid(val pod: Pod, val cellid: Int): Cloneable {

    public override fun clone(): Brid {
        return super.clone() as Brid
    }

    override fun toString(): String {
        var s = this::class.qualifiedName as String
        s += "\npod = $pod".replace("\n", "\n    ")
        s += "\n    sellidcellid = $cellid"

        return s
    }
}

/**
 *      This is a thread, that contains a number of branches.
 *  @param podName Alias for threadName
 *  @param pid Pod identifier. It is the index of the pod in the pool's array of pods
 */
class Pod(podName: String, pid: Int): CuteThread(POD_THREAD_QUEUE_TIMEOUT, MAX_POD_THREAD_QUEUE, podName)
{
    /** Index of the pod in the podArray of the pod pool. */
    val pid = pid

    /** Alias for threadName */
    val podName: String
        inline get() = threadName

    /** Number of branches currently assigned to the pod. */
    internal var numOfBranches = 0

    override fun toString(): String {
        var s= super.toString()
        s += "\n    numOfBranches = $numOfBranches"
        s += "\n    pid = $pid"
        return s
    }

    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //                                  Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$ protected data ---$$$---$$$---$$$---$$$---$$$--

    //---$$$---$$$---$$$---$$$---$$$--- protected methods ---$$$---$$$---$$$---$$$---$$$---

    protected override fun _messageProc_(msg: MessageMsg): Boolean {

        when(msg) {

            is BranchRequestsPodpoolCreateChildMsg -> {
                val cellid = generateSockid()
                val destBrid = Brid(this, cellid)
                val branch = Branch(msg.destBreedCid, destBrid, msg.parentBrid)

                // May be inject ins
                if(msg.destIns != null)
                    for(cpt in msg.destIns)
                        branch.add(cpt)

                branchMap_[cellid] = branch
                numOfBranches++
                _pp_.putInQueue(BranchReportsPodpoolAndParentItsCreationMsg(parentBrid = msg.parentBrid,
                    ownBrid = destBrid, ownBreedCid = msg.destBreedCid
                ))

                branch.reasoning()      // kick off

                return true
            }

            // Finish creating branch
            is BranchReportsPodpoolAndParentItsCreationMsg -> {
                val branch = branchMap_[msg.parentBrid.cellid]
                branch!!.addChild(msg.ownBrid)

                // Activate and fill in child's breed
                val childBreed = branch[msg.ownBreedCid] as Breed
                childBreed.brid = msg.ownBrid
                childBreed.activate()

                branch.reasoning()

                return true
            }

            is UserTellsCircleIbr -> {
                // todo: give it to circle
                return true
            }

            // Create new branch
            is UserRequestsDispatcherCreateAttentionCircleMsg -> {
                val breedCid = hardCrank.hardCids.circle_breed.cid
                val cellid = generateSockid()
                val circle = AttentionCircle(breedCid, Brid(this, cellid), msg.userThread)
                branchMap_[cellid] = circle
                numOfBranches++
                _pp_.putInQueue(AttentionCircleReportsPodpoolDispatcherUserItsCreationMsg(msg.userThread, Brid(this, cellid)))

                circle.reasoning()

                return true
            }

            is TerminationRequestMsg -> {
                return true
            }

            is TimeoutMsg ->
                if      //is this pod idle?
                        (numOfBranches == 0)
                    //yes: it's ok, silence the timeout
                    return true
                else {
                    // todo: give it to circle
                    return false
                }
        }
        return false
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%--- private data ---%%%---%%%---%%%---%%%---%%%---%%%

    /** Map Branch/ownBrid. */
    private val branchMap_ = hashMapOf<Int, Branch>()

    //---%%%---%%%---%%%---%%%--- private funcs ---%%%---%%%---%%%---%%%---%%%---%%%

    /**
     *      Generate socket identifier of a branch in the pod, that is guaranteed in no use.
     */
    private fun generateSockid(): Int {

        var brid: Int
        do {
            brid = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE)
        } while(brid in branchMap_)

        return brid
    }
}

/**
 *      Comparator for pods is needed for building sorted set of pods (TreeSet<Pod>).
 */
class PodComparator: Comparator<Pod>
{
    override fun compare(o1: Pod?, o2: Pod?): Int {
        assert(value = o1 != null && o2 != null)

        return when {
            o1!!.numOfBranches != o2!!.numOfBranches -> if(o1.numOfBranches < o2.numOfBranches) -1 else 1
            o1.pid != o2.pid -> if(o1.pid < o2.pid) -1 else 1
            else -> 0
        }
    }
}

/**
 *      Pool of pods. It is of fixed size and populated with running pods (they are started on the pool construction).
 *  @param size number of pods in the pool
 */
class Podpool(val size: Int = POD_POOL_SIZE): CuteThread(0, 0, "pod_pool")
{
    protected override fun _messageProc_(msg: MessageMsg?): Boolean {
        when(msg) {

            is BranchRequestsPodpoolCreateChildMsg,
            is UserRequestsDispatcherCreateAttentionCircleMsg -> {

                // Take from podSet the pod with smallest usage, so preventing it from dispatching before it will be
                // loaded with new branch. It will be returned back on getting report on starting the branch in the
                // correspondent handler.
                if      //are all of the pods busy with dispatching new branches?
                        (podSet.isEmpty())
                {   //yes: do spin-blocking - sleep for a short wile then redispatch the message
                    assert(borrowedPods == podArray.size)
                    if (!podpoolOverflowReported) {
                        logit("no free pods to create a branch")    // log the overflow without flooding
                        podpoolOverflowReported = true
                    }
                    Thread.sleep(1)
                    putInQueue(msg)

                    return true
                }
                else
                {   //no: take out a pod from the pod set and request pod to create new attention circle or branch
                    val pod = podSet.pollFirst()
                    borrowedPods++
                    pod.putInQueue(msg)     // forward message to pod

                    return true
                }
            }

            is BranchReportsPodpoolAndParentItsCreationMsg -> {
                podSet.add(msg.ownBrid.pod)
                podpoolOverflowReported = false
                borrowedPods--
                msg.parentBrid.pod.putInQueue(msg)      // forward this message to the parent's pod

                return true
            }

            is AttentionCircleReportsPodpoolDispatcherUserItsCreationMsg -> {

                podSet.add(msg.ownBrid.pod)
                podpoolOverflowReported = false
                borrowedPods--
                _atnDispatcher_.putInQueue(msg)

                return true
            }

            is TerminationRequestMsg -> {

                // Terminate pods
                for(pod in podArray)
                    pod.putInQueue(msg)

                return true
            }
        }

        return false
    }

    /**
     *      Start all pods of the pool.
     */
    fun startPods() {
        for(pod in podArray)
            pod.start()
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%--- private data ---%%%---%%%---%%%---%%%---%%%---%%%

    // Create all pods
    private var podArray = Array(size) { Pod("pod_$it", it) }

    /** Sorted set of pods. Pods are sorted by their usage number, plus a unique id to avoid repetition. */
    private val podSet = TreeSet<Pod>(PodComparator())

    /** Number of pods currently creating new branches. */
    private var borrowedPods: Int = 0

    /** To avoid flooding the log. */
    private var podpoolOverflowReported = false

    init {
        // Register all pods in the tree set
        for(pod in podArray) podSet.add(pod)
    }
}
