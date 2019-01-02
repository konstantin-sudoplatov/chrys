package libmain

import atn.Brad
import basemain.Cid
import chribase_thread.CuteThread
import chribase_thread.MessageMsg
import cpt.abs.DynamicConcept

class ReaderSendsConsoleLineMsg(val text: String): MessageMsg()

class CirclePromptsUserMsg(): MessageMsg()

class UserRequestsDispatcherCreateAttentionCircleMsg(val userThread: CuteThread): MessageMsg()

/**
 *      Attention circle reports to pod pool, dispatcher and user that it was created and gives them its ownBrad.
 *  @param userThread pod pool and dispatcher are accessible by common variables _pp_ and _attnDispatcher_. The user thread
 *                      is not, since users could be changing. So, user in order to be found must be identified.
 *  @param ownBrad new branch's address.
 */
class AttentionCircleReportsPodpoolAndDispatcherItsCreationMsg(val userThread: CuteThread, val ownBrad: Brad): MessageMsg()

/**
 *      Uline sends its address to user to let him communicate to it.
 *  @param brad Branch address
 */
class BranchSendsUserItsBrad(val brad: Brad): MessageMsg()

/**
 *      Request for creating branch.
 *  @param destBreedCid Cid of the breed for new branch
 *  @param destIns Array of live concepts to be injected into new branch
 *  @param parentBrad Address of the parent branch (for sending back report)
 */
class BranchRequestsPodpoolCreateChildMsg(val destBreedCid: Cid, val destIns: Array<DynamicConcept>?, val parentBrad: Brad): MessageMsg()

/**
 *      Branch reports to the pod pool and its parent its creation and tells them its ownBrad.
 *  @param parentBrad To be able to find parent
 *  @param ownBrad
 *  @param ownBreedCid
 */
class BranchReportsPodpoolAndParentItsCreationMsg(val parentBrad: Brad, val ownBrad: Brad, val ownBreedCid: Cid): MessageMsg()

/**
 *      User sends a line of text to the circle. (Is sent from the user thread to a pod thread).
 *  @param destBrid Branch identifier in the pod.
 */
class UserTellsCircleIbr(val destBrid: Int, val text: String): MessageMsg()

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
}

/**
 *      Activate concept remotely (i.e. another's branch live concept).
 *  @param destBrad address (pod + brid) of the destination branch
 *  @param cptCid Cid of the concept.
 */
class ActivateIbr(destBrad: Brad, val cptCid: Cid): IbrMsg(destBrad)

/**
 *      Anactivate (set activation to -1) concept remotely (i.e. another's branch live concept).
 *  @param destBrad address (pod + brid) of the destination branch
 *  @param cptCid Cid of the concept.
 */
class AnactivateIbr(destBrad: Brad, val cptCid: Cid): IbrMsg(destBrad)

/**
 *      Passing a single concept. The concept is cloned on sending and injected into the receiver branch on the receiving.
 *  @param destBrad address (pod + brid) of the destination branch
 *  @param cpt The concept to pass.
 *  @param sourceBrad address of the originating branch
 */
class TransportSingleConceptIbr(destBrad: Brad, val cpt: DynamicConcept, val sourceBrad: Brad): IbrMsg(destBrad)