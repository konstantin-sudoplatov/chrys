package atn

import basemain.*
import chribase_thread.CuteThread
import chribase_thread.MessageMsg
import chribase_thread.TerminationRequestMsg
import chribase_thread.TimeoutMsg
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
        s += "\n    sellidcellid = $brid"

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
    val pid: Int,       // Index of the pod in the podArray of the pod pool.
    val dlv: Int = 0    // Debugging level. There is also branch debug level and GLOBAL_DEBUG_LEVEL.
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

    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //                                  Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$ protected data ---$$$---$$$---$$$---$$$---$$$--

    //---$$$---$$$---$$$---$$$---$$$--- protected methods ---$$$---$$$---$$$---$$$---$$$---

    protected override fun _messageProc(msg: MessageMsg): Boolean {

        when(msg) {

            // Create new branch
            is BranchRequestsPodpoolCreateChildMsg -> {
                dlog_ { ar("msg came: $msg") }
                val brid = generateSockid()
                val destBrad = Brad(this, brid)
                val branch = Branch(msg.destBreedCid, destBrad, msg.parentBrad)

                // May be, inject ins
                if(msg.destIns != null)
                    for(cpt in msg.destIns)
                        branch.add(cpt)

                branchMap_[brid] = branch
                numOfBranches++
                _pp_.putInQueue(BranchReportsPodpoolAndParentItsCreationMsg(parentBrad = msg.parentBrad,
                    ownBrad = destBrad.clone(), ownBreedCid = msg.destBreedCid
                ))

                branch.reasoning()      // kick off

                return true
            }

            // Parent gets report on creating child branch
            is BranchReportsPodpoolAndParentItsCreationMsg -> {
                dlog_ { ar("msg came: $msg") }
                val parentBranch = branchMap_[msg.parentBrad.brid]
                parentBranch!!.addChild(msg.ownBrad)    // add new child to the list of children of the parent branch

                // Activate and fill in the child's breed in the space name of parent
                val childBreed = parentBranch[msg.ownBreedCid] as Breed
                childBreed.brad = msg.ownBrad       // it's a clone of the child's brad object (not necessary, just nice)
                childBreed.activate()

                parentBranch.reasoning()

                return true
            }

            is UserTellsCircleIbr -> {
                val branch = branchMap_[msg.destBrid] as Branch
                val inputBufferCpt = branch[hardCrank.hardCid.userInputBuffer_prem.cid] as StringQueuePrem
                inputBufferCpt.queue.add(msg.text)
                inputBufferCpt.activate()

                return true
            }

            // Create attention circle
            is UserRequestsDispatcherCreateAttentionCircleMsg -> {
                dlog_ { ar("msg came: $msg") }
                val breedCid = hardCrank.hardCid.circle_breed.cid
                val cellid = generateSockid()
                val circle = AttentionCircle(breedCid, Brad(this, cellid), msg.userThread)
                branchMap_[cellid] = circle
                numOfBranches++
                _pp_.putInQueue(AttentionCircleReportsPodpoolAndDispatcherItsCreationMsg(msg.userThread, Brad(this, cellid)))

                circle.reasoning()

                return true
            }

            is TerminationRequestMsg -> {
                dlog_ { ar(
                    "",
                    "msg came: $msg"
                ) }
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

    /** Map Branch/ownBrad. */
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

    /**
     *      Log a debugging line. The debug level is taken as a maximum of the global or thread debug level. The lambda
     *  provides an array of lines, corresponding to the debug levels, where the first array element corresponds to the
     *  level 1. If there is no corresponding line, than the last line of the array is used. If the line is empty, nothing
     *  is logged.
     *  @param lines Lamba, resulting in ar array of strings, one of which will be logged.
     */
    private inline fun dlog_(lines: () -> Array<String>) {
        if (DEBUG_ON) {
            val effectiveLvl = max(GLOBAL_DEBUG_LEVEL, this.dlv)
            if(effectiveLvl <= 0) return
            if      // is there a line corresponding to the debug level?
                    (effectiveLvl <= lines().size)
            {    //yes: log that line
                if(lines()[effectiveLvl-1] != "") logit(this.podName + ": " + lines()[effectiveLvl-1])
            }
            else //no: log the last line of the array
                if(lines()[lines().lastIndex] != "") logit(this.podName + ": " + lines()[lines().lastIndex])
        }
    }

    /**
     *      Log a debugging line. The debug level is taken as a maximum of the global, thread or branch debug level. The lambda
     *  provides an array of lines, corresponding to the debug levels, where the first array element corresponds to the
     *  level 1. If there is no corresponding line, than the last line of the array is used. If the line is empty, nothing
     *  is logged.
     *  @param brid Branch identifier
     *  @param lines Lamba, resulting in ar array of strings, one of which will be logged.
     */
    private inline fun dlog_(brid: Int, lines: () -> Array<String>) {
        if (DEBUG_ON) {
            val branch = branchMap_[brid]
            val branchLvl = if(branch == null) 0 else branch.dlv
            val effectiveLvl = max(max(GLOBAL_DEBUG_LEVEL, this.dlv), branchLvl)
            if(effectiveLvl <= 0) return
            if      // is there a line corresponding to the debug level?
                    (effectiveLvl <= lines().size)
            {   //yes: log that line
                if(lines()[effectiveLvl-1] != "") logit(this.podName + ": " + lines()[effectiveLvl-1])
            }
            else //no: log the last line of the array
                if(lines()[lines().lastIndex] != "") logit(this.podName + ": " + lines()[lines().lastIndex])
        }
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
    protected override fun _messageProc(msg: MessageMsg?): Boolean {
        when(msg) {

            is BranchRequestsPodpoolCreateChildMsg,
            is UserRequestsDispatcherCreateAttentionCircleMsg -> {

                // Take from hostCandidates the pod with smallest usage, i.e. the first one. The pod is taken out of the
                // set, so it would not get used again before it is loaded with this branch. The pod will be returned back
                // on getting report message of starting the branch.
                if      //are all of the pods busy with dispatching new brans?
                        (hostCandidates.isEmpty())
                {   //yes: do spin-blocking - sleep a short wile then redispatch this message
                    assert(borrowedPods == pods.size)
                    if (!podpoolOverflowReported) {
                        logit("no free pods to create a branch")    // log the overflow without flooding
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
                    podpoolOverflowReported = false
                    pod.putInQueue(msg)     // forward message to pod

                    return true
                }
            }

            is BranchReportsPodpoolAndParentItsCreationMsg -> {
                hostCandidates.add(msg.ownBrad.pod)
                borrowedPods--
                msg.parentBrad.pod.putInQueue(msg)      // forward this message to the parent's pod

                return true
            }

            is AttentionCircleReportsPodpoolAndDispatcherItsCreationMsg -> {

                hostCandidates.add(msg.ownBrad.pod)
                borrowedPods--
                _atnDispatcher_.putInQueue(msg)         // forward this message to the dispatcher

                return true
            }

            is TerminationRequestMsg -> {

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

    init {
        // Register all pods in the tree set
        for(pod in pods) hostCandidates.add(pod)
    }
}
