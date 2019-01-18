package atn

import basemain.GDEBUG_LV
import basemain.MAX_POD_THREAD_QUEUE
import basemain.POD_THREAD_QUEUE_TIMEOUT
import basemain.logit
import chribase_thread.CuteThread
import chribase_thread.MessageMsg
import chribase_thread.TerminationRequestMsg
import chribase_thread.TimeoutMsg
import cpt.ActivationIfc
import cpt.Breed
import cpt.StringQueuePrem
import libmain.*
import java.util.*
import kotlin.math.max
import kotlin.random.Random

/**
 *      Full address of branch in the pod pool
 *  @param pod pod object
 *  @param brid Branch identifier in pod. It is an integer key in the branch map the pod object.
 */
data class Brad(val pod: Pod, val brid: Int): Cloneable {

    public override fun clone(): Brad {
        return super.clone() as Brad
    }

    override fun toString(): String {
        var s = this::class.qualifiedName as String
        s += "\npod = $pod".replace("\n", "\n    ")
        s += "\n    brid = $brid"

        return s
    }
}

/**
 *      This is a thread, that contains a number of brans.
 *  @param podName Alias for threadName
 *  @param pid Pod identifier. It is the index of the pod in the pool's array of pods
 */
class Pod(
    podName: String,
    val pid: Int,               // Index of the pod in the podArray of the pod pool.
    var dlv: Int = -1,          // Debugging level. There is also branch debug level and GDEBUG_LV.
    var dBranchFilter: Int = -1 // Filter debugging messages for a branch. The field contains a brid. -1: no filtering.
): CuteThread(POD_THREAD_QUEUE_TIMEOUT, MAX_POD_THREAD_QUEUE, podName)
{

    /** Alias for threadName */
    val podName: String
        inline get() = threadName

    /** Number of brans currently assigned to the pod. */
    internal var numOfBranches = 0

    override fun toString(): String {
        var s = super.toString()
        s += "\n    numOfBranches = $numOfBranches"
        s += "\n    pid = $pid"
        return s
    }

    operator fun get(brid: Int): Branch? {
        return branchMap_[brid]
    }

    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //                                  Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$ protected data ---$$$---$$$---$$$---$$$---$$$--

    //---$$$---$$$---$$$---$$$---$$$--- protected methods ---$$$---$$$---$$$---$$$---$$$---
    protected override fun _messageProc(msg: MessageMsg): Boolean {

        when(msg) {

            is IbrMsg -> {
                val br = branchMap_[msg.destBrid] as Branch
                when(msg) {
                    is ActivateRemotelyIbr ->
                    {
                        dlog_(br, "msg = ${msg.toStr()}")

                        (br[msg.cptCid] as ActivationIfc).activate()
                        br.reasoning()

                        return true
                    }

                    is AnactivateRemotelyIbr ->
                    {
                        dlog_(br, "msg = ${msg.toStr()}")

                        (br[msg.cptCid] as ActivationIfc).anactivate()
                        br.reasoning()

                        return true
                    }

                    is TransportSingleConceptIbr ->
                    {
                        dlog_(br, "msg = ${msg.toStr()}")

                        // Inject load
                        br.add(msg.load)
                        br.reasoning()

                        return true
                    }

                    is      // parent gets report on creating child branch?
                            BranchReportsPodpoolAndParentItsCreationIbr ->
                    {
                        val origBranch = msg.origBrad.pod[msg.origBrad.brid] as Branch
                        dlog_(br,"(from ${origBranch.branchName()}): msg = ${msg.toStr()}")

                        br.addChild(msg.origBrad)    // add new child to the list of children of the parent branch

                        // Set up and activate the child's breed in the parent's space name
                        val childBreed = br[msg.origBreedCid] as Breed
                        childBreed.brad = msg.origBrad       // it's a clone of the child's brad object (not necessary, just nice)
                        childBreed.activate()

                        br.reasoning()

                        return true
                    }
                }
            }

            // Create new branch
            is ParentRequestsPodpoolCreateChildMsg -> {
                dlog_("msg = ${msg.toStr()}")
                val brid = generateSockid()
                val destBrad = Brad(this, brid)
                val br = Branch(msg.destBreedCid, destBrad, msg.parentBrad)

                // May be, inject ins
                if(msg.destIns != null)
                    for(cpt in msg.destIns)
                        br.add(cpt)

                branchMap_[brid] = br
                numOfBranches++
                _pp_.putInQueue(BranchReportsPodpoolAndParentItsCreationIbr(destBrad = msg.parentBrad,
                    origBrad = destBrad.clone(), origBreedCid = msg.destBreedCid
                ))

                br.reasoning()      // kick off

                return true
            }

            is UserTellsCircleMsg -> {
                val br = branchMap_[msg.destBrid] as Branch
                dlog_(br, "msg = ${msg.toStr()}")
                val inputBufferCpt = br[hCr.hardCid.userInputBuffer_strqprem.cid] as StringQueuePrem
                inputBufferCpt.queue.add(msg.text)
                inputBufferCpt.activate()

                br.reasoning()

                return true
            }

            // Create attention circle
            is UserRequestsDispatcherCreateAttentionCircleMsg -> {
                dlog_("msg = ${msg.toStr()}")
                val breedCid = hCr.hardCid.circle_breed.cid
                val cellid = generateSockid()
                val circle = AttentionCircle(breedCid, Brad(this, cellid), msg.userThread)
                branchMap_[cellid] = circle
                numOfBranches++
                _pp_.putInQueue(AttentionCircleReportsPodpoolAndDispatcherItsCreationMsg(msg.userThread, Brad(this, cellid)))

                circle.reasoning()

                return true
            }

            is TerminationRequestMsg -> {
                dlog_("msg = ${msg.toStr()}")
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

    /** Map Branch/origBrad. */
    private val branchMap_ = hashMapOf<Int, Branch>()

    //---%%%---%%%---%%%---%%%--- private funcs ---%%%---%%%---%%%---%%%---%%%---%%%

    /**
     *      Generate socket identifier of a branch in the pod, that is guaranteed in no use. -1 is excluded, so it
     *  can serve as a "no branch" flag.
     */
    private fun generateSockid(): Int {

        var brid: Int
        do {
            brid = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE)
        } while(brid in branchMap_ || brid == -1)

        return brid
    }

    /**
     *          Log a debugging line without filtering.
     *      The debug level is taken as a maximum of the global or thread debug level.
     *  @param line text to log
     */
    private fun dlog_(line: String) {
        if (GDEBUG_LV >= 0) {
            val effectiveLvl = max(GDEBUG_LV, this.dlv)
            if(effectiveLvl > 0)
                logit(this.podName + ": " + line)
        }
    }

    /**
     *          Log a debugging line if it passes the filter
     *  @param branch Branch, the message is meant for
     *  @param line text to log
     */
    private fun dlog_(branch: Branch, line: String) {
        if (GDEBUG_LV >= 0)
            if      // does the message pass the filter?
                (dBranchFilter == -1 || dBranchFilter == branch.ownBrad.brid)
            dlog_("(to ${branch.branchName()}): " + line)
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
class Podpool(
    val size: Int = _conf_.podPoolSize,
    var dlv: Int = -1,          // Debugging level. There is also branch debug level and GDEBUG_LV.
    var dPodFilter: Int = -1    // Filter debugging messages for a pod. The field contains a pid. -1: no filtering.
): CuteThread(0, 0, "pod_pool")
{
    protected override fun _messageProc(msg: MessageMsg?): Boolean {
        when(msg) {

            // Forward all Ibrs to the destination pods
            is IbrMsg -> {
                val destPod = msg.destPod
                dlog_(destPod, "msg: ${msg.toStr()}")

                if      //is it report for branch creation?
                        (msg is BranchReportsPodpoolAndParentItsCreationIbr)
                {   //yes: return some debts to podpool
                    hostCandidates.add(msg.origBrad.pod)
                    borrowedPods--
                }

                msg.destPod.putInQueue(msg)
                return true
            }

            is ParentRequestsPodpoolCreateChildMsg,
            is UserRequestsDispatcherCreateAttentionCircleMsg -> {
                dlog_("msg = ${msg.toStr()}")

                // Take from hostCandidates the pod with smallest usage, i.e. the first one. The pod is taken out of the
                // set, so it would not get used again before it is loaded with this branch. The pod will be returned back
                // on getting report message of starting the branch.
                if      //are all of the pods busy with dispatching new brans?
                        (hostCandidates.isEmpty())
                {   //yes: do spin-blocking - sleep a short wile then redispatch this message
                    assert(borrowedPods == pods.size)
                    if (!podpoolOverflowReported) {
                        logit("Warning: No free pods to create a branch. Waiting...")    // log the overflow without flooding
                        podpoolOverflowReported = true
                    }
                    Thread.sleep(1)
                    putInQueue(msg)

                    return true
                }
                else
                {   //no: take out a pod from the candidate's set and request the pod to create new branch/attention circle
                    val pod = hostCandidates.pollFirst()
                    borrowedPods++
                    if(podpoolOverflowReported) {
                        logit("Creating...")
                        podpoolOverflowReported = false
                    }
                    pod.putInQueue(msg)     // forward message to pod

                    return true
                }
            }

            is AttentionCircleReportsPodpoolAndDispatcherItsCreationMsg -> {
                dlog_("msg = ${msg.toStr()}")

                hostCandidates.add(msg.ownBrad.pod)
                borrowedPods--
                _atnDispatcher_.putInQueue(msg)         // forward this message to the dispatcher

                return true
            }

            is TerminationRequestMsg -> {
                dlog_("msg = ${msg.toStr()}")

                // Terminate pods
                for(pod in pods)
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
        for(pod in pods)
            pod.start()
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%--- private data ---%%%---%%%---%%%---%%%---%%%---%%%

    // Array of all pods in the pool.
    private var pods = Array(size) { Pod("pod_$it", it) }

    /** Queue of pods, as applicants for hosting new brans. It is a sorted set of pods. Pods are sorted by their
        usage number, plus a unique id to avoid repetition. The first element in the set is the most suitable candidate. */
    private val hostCandidates = TreeSet<Pod>(PodComparator())

    /** Number of pods currently creating new brans. */
    private var borrowedPods: Int = 0

    /** To avoid flooding the log. */
    private var podpoolOverflowReported = false

    /**
     *          Log a debugging line without filtering.
     *      The debug level is taken as a maximum of the global or thread debug level.
     *  @param line line to log.
     */
    private inline fun dlog_(line: String) {
        if (GDEBUG_LV >= 0) {
            val effectiveLvl = max(GDEBUG_LV, this.dlv)
            if(effectiveLvl > 0)
                logit(this.threadName + ": " + line)
        }
    }

    /**
     *          Log a debugging line if it passes the filter
     *  @param pod Pod, the message is meant for
     *  @param line line to log.
     */
    private inline fun dlog_(pod: Pod, line: String) {
        if (GDEBUG_LV >= 0) {
            val effectiveLvl = max(GDEBUG_LV, this.dlv)
            if      // does the message pass the filter?
                    (dPodFilter == -1 || dPodFilter == pod.pid)
                dlog_(line)
        }
    }

    init {
        // Register all pods in the tree set
        for(pod in pods) hostCandidates.add(pod)
    }
}
