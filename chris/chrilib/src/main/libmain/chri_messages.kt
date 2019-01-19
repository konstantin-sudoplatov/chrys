package libmain

import atn.Brad
import atn.Pod
import basemain.Cid
import chribase_thread.CuteThread
import chribase_thread.MessageMsg
import cpt.abs.DynamicConcept

data class ReaderSendsConsoleLineMsg(val text: String): MessageMsg()

data class UserRequestsDispatcherCreateAttentionCircleMsg(val userThread: CuteThread): MessageMsg()

/**
 *      Attention circle reports to pod pool, dispatcher and user that it was created and gives them its origBrad.
 *  @param userThread pod pool and dispatcher are accessible by common variables _pp_ and _attnDispatcher_. The user thread
 *                      is not, since users could be changing. So, user in order to be found must be identified.
 *  @param ownBrad new branch's address.
 */
data class AttentionCircleReportsPodpoolAndDispatcherItsCreationMsg(val userThread: CuteThread, val ownBrad: Brad): MessageMsg()

/**
 *      Uline sends its address to user to let him communicate to it.
 *  @param brad Branch address
 */
data class BranchSendsUserItsBradMsg(val brad: Brad): MessageMsg()

/**
 *      Request for creating branch.
 *  @param destBreedCid Cid of the breed for new branch
 *  @param destIns Array of live concepts to be injected into new branch
 *  @param parentBrad Address of the parent branch (for sending back report)
 */
data class ParentRequestsPodpoolCreateChildMsg(val destBreedCid: Cid, val destIns: Array<out DynamicConcept>?,
                                               val parentBrad: Brad): MessageMsg()

/**
 *      After creation a branch pod reports the fact to podpool, so that it could put the pod back into the hostCanditates set.
 *  @param origPod the pod itself
 */
class PodReportsPodpoolBranchCreationMsg(val origPod: Pod): MessageMsg()

/**
 *      Branch asks its pod to terminate it.
 *  @param origBrid branch to terminate
 */
class BranchRequestsPodToTerminateItMsg(val origBrid: Int): MessageMsg()

/**
 *      After deleting a branch pod sends podpool this message, so that it could actualize the hostCandidates set.
 *  @param origPod own object reference for podpool to actualize.
 */
class PodReportsPodpoolBranchTerminationMsg(val origPod: Pod): MessageMsg()

/**
 *      User sends a line of text to the circle. (Is sent from the user thread to a pod thread).
 *  @param destBrid Branch identifier in the pod.
 *  @param text text to send
 */
class UserTellsCircleMsg(val destBrid: Int, val text: String): MessageMsg() {
    override fun toStr(): String {
        return super.toStr() + ", text = $text"
    }
}

/**
 *      Circle (ulwrite branch) sends a line of text to user.
 *  @param text text to send
 */
class CircleTellsUserMsg(val text: String): MessageMsg()

/**
 *      Encourage user to sent next input.
 */
class CirclePromptsUserMsg(): MessageMsg()

/**
 *      Base for messages addressed to other branches (inter branch messages). Sent by a branch to another branch. If
 *  cloned concept are passed between branches, they must be cloned at the sender's, not the receiving site to prevent
 *  possible change during the traveling time.
 *  @param destBrid identifier of the destination branch
 */
abstract class IbrMsg(val destBrid: Int): MessageMsg() {

    override fun toString(): String {
        var s = super.toString()
        s += "\n    destBrid = $destBrid"

        return s
    }

    override fun toStr(): String {
        return super.toStr()
    }
}

/**
 *      Branch reports to the pod pool and its parent its creation and tells them its origBrad.
 *  @param destBrid identifier of the destination branch
 *  @param origBrad to identify itself
 *  @param origBreedCid
 */
class ChildReportsParentItsCreationIbr(destBrid: Int, val origBrad: Brad, val origBreedCid: Cid): IbrMsg(destBrid) {
    override fun toString(): String {
        var s = super.toString()
        s += "\norigBrad = $origBrad".replace("\n", "\n    ")
        s += "\n    origBreedCid = $origBreedCid"
        return s
    }

    override fun toStr(): String {
        return super.toStr() + ", branchName = ${origBrad.pod[origBrad.brid]?.branchName()?:"null"}"
    }
}

/**
 *      Child branch notifies its parent that it wants to be terminated and sends parent array of outs - concepts to be
 *  injected into the parent's name space. After sending this message child formally is still working, since its breed
 *  in the parent space is not yet anactivated. It's parent's responsibility to send the child's pod request for child
 *  termination and anactivate its breed.
 *  @param destBrid address of the parent branch
 *  @param outs array of live concepts, which child sends to parent to be injected into parents name space
 *  @param origBrad to identify itself
 *  @param origBreedCid cid of the child breed, so that the parent could identify the child.
 */
class ChildReportsParentItsTerminationIbr(destBrid: Int, val outs: Array<out DynamicConcept>?, val origBrad: Brad,
                                          val origBreedCid: Cid): IbrMsg(destBrid) {
    override fun toString(): String {
        var s = super.toString()
        s += "\norigBrad = $origBrad".replace("\n", "\n    ")
        s += "\n    origBreedCid = $origBreedCid"
        return s
    }

    override fun toStr(): String {
        return super.toStr() + ", branchName = ${origBrad.pod[origBrad.brid]?.branchName()?:"null"}"
    }
}

/**
 *      Activate concept remotely (i.e. another's branch live concept).
 *  @param destBrid identifier of the destination branch
 *  @param cptCid Cid of the concept.
 */
class ActivateRemotelyIbr(destBrid: Int, val cptCid: Cid): IbrMsg(destBrid) {
    override fun toString(): String {
        var s = super.toString()
        s += "\n    cptCid = $cptCid"
        return s
    }

    override fun toStr(): String {
        return super.toStr() + ", concept = ${cidNamed(cptCid)}"
    }
}

/**
 *      Anactivate (set activation to -1) concept remotely (i.e. another's branch live concept).
 *  @param destBrid identifier of the destination branch
 *  @param cptCid Cid of the concept.
 */
class AnactivateRemotelyIbr(destBrid: Int, val cptCid: Cid): IbrMsg(destBrid) {
    override fun toString(): String {
        var s = super.toString()
        s += "\n    cptCid = $cptCid"
        return s
    }

    override fun toStr(): String {
        return super.toStr() + ", concept = ${cidNamed(cptCid)}"
    }
}

/**
 *      Passing a single concept. The concept is cloned on sending and injected into the receiver branch on the receiving.
 *  @param destBrid identifier of the destination branch
 *  @param load The concept to pass.
 */
class TransportSingleConceptIbr(destBrid: Int, val load: DynamicConcept): IbrMsg(destBrid) {

    override fun toString(): String {
        var s = super.toString()
        s += "\nload = $load".replace("\n", "\n    ")
        return s
    }

    override fun toStr(): String {
        return super.toStr() + ", load = ${load.toStr()}"
    }
}