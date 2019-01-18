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
data class BranchSendsUserItsBrad(val brad: Brad): MessageMsg()

/**
 *      Request for creating branch.
 *  @param destBreedCid Cid of the breed for new branch
 *  @param destIns Array of live concepts to be injected into new branch
 *  @param parentBrad Address of the parent branch (for sending back report)
 */
data class ParentRequestsPodpoolCreateChildMsg(val destBreedCid: Cid, val destIns: Array<out DynamicConcept>?,
                                               val parentBrad: Brad): MessageMsg()

/**
 *      Parent branch asks the pod to terminate its child branch.
 *  @param destBrid identifier of the branch to delete
 */
class ParentRequestsPodTerminateChildMsg(val destBrid: Int): MessageMsg()

/**
 *      After deleting a branch pod sends podpool this message, so that it could actualize the hostCandidates set.
 *  @param origPod own object reference for podpool to actualize.
 */
class PodReportsToPodpoolBranchTermination(val origPod: Pod): MessageMsg()

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
 *      Base for messages addressed to other brans (inter branch messages). Sent by brans to the pod pool. The
 *  pod pool forwards them to the destination pod and branch. If cloned concept are passed between brans, they must be
 *  cloned at the sender's, not the receiving site to prevent possible change during the traveling time.
 *
 *  @param destBrad Adress of the destination branch. There is no need to clone it, since the pod and brid are taken from
 *                  destBrad on construction.
 */
abstract class IbrMsg(destBrad: Brad): MessageMsg() {
    val destPod = destBrad.pod
    val destBrid = destBrad.brid

    override fun toString(): String {
        var s = super.toString()
        s += "\ndestPod = $destPod".replace("\n", "\n    ")
        s += "\n    destBrid = $destBrid"

        return s
    }

    override fun toStr(): String {
        var s = super.toStr()
        val branchFrom: String
        return s
    }
}

/**
 *      Branch reports to the pod pool and its parent its creation and tells them its origBrad.
 *  @param destBrad To be able to find parent
 *  @param origBrad
 *  @param origBreedCid
 */
class BranchReportsPodpoolAndParentItsCreationIbr(destBrad: Brad, val origBrad: Brad, val origBreedCid: Cid): IbrMsg(destBrad)

/**
 *      Child branch notifies its parent that it wants to be terminated and sends parent array of outs - concepts to be
 *  injected into the parent's name space. After sending this message child formally is still working, since its breed
 *  in the parent space is not yet anactivated. It's parent's responsibility to send the child's pod request for child
 *  termination and anactivate its breed.
 *  @param destBrad address of the parent branch
 *  @param outs array of live concepts, which child sends to parent to be injected into parents name space
 *  @param childBreedCid cid of the child breed, so that the parent could identify the child.
 */
class ChildRequestsParentTerminatedItIbr(destBrad: Brad, val outs: Array<out DynamicConcept>?, val childBreedCid: Cid):
    IbrMsg(destBrad)

/**
 *      Activate concept remotely (i.e. another's branch live concept).
 *  @param destBrad address (pod + brid) of the destination branch
 *  @param cptCid Cid of the concept.
 */
class ActivateRemotelyIbr(destBrad: Brad, val cptCid: Cid): IbrMsg(destBrad) {
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
 *  @param destBrad address (pod + brid) of the destination branch
 *  @param cptCid Cid of the concept.
 */
class AnactivateRemotelyIbr(destBrad: Brad, val cptCid: Cid): IbrMsg(destBrad) {
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
 *  @param destBrad address (pod + brid) of the destination branch
 *  @param load The concept to pass.
 */
class TransportSingleConceptIbr(destBrad: Brad, val load: DynamicConcept): IbrMsg(destBrad) {

    override fun toString(): String {
        var s = super.toString()
        s += "\nload = $load".replace("\n", "\n    ")
        return s
    }

    override fun toStr(): String {
        return super.toStr() + ", load = ${load.toStr()}"
    }
}