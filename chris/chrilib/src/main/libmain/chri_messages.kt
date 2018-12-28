package libmain

import atn.Brid
import basemain.Cid
import chribase_thread.CuteThread
import chribase_thread.MessageMsg
import cpt.Breed
import cpt.abs.Concept
import cpt.abs.DynamicConcept

class ReaderSendsConsoleLineMsg(val text: String): MessageMsg()

class CirclePromptsUserMsg(): MessageMsg()

class UserRequestsDispatcherCreateAttentionCircleMsg(val userThread: CuteThread): MessageMsg()

/**
 *      Attention circle reports to pod pool, dispatcher and user that it was created and gives them its ownBrid.
 *  @param userThread pod pool and dispatcher are accessible by common variables _pp_ and _attnDispatcher_. The user thread
 *                      is not, since users could be changing. So, user in order to be found must be identified.
 *  @param ownBrid new branch's address.
 */
class AttentionCircleReportsPodpoolDispatcherUserItsCreationMsg(val userThread: CuteThread, val ownBrid: Brid): MessageMsg()

/**
 *      Request for creating branch.
 *  @param destBreedCid Cid of the breed for new branch
 *  @param destIns Array of live concepts to be injected into new branch
 *  @param parentBrid Address of the parent branch (for sending back report)
 */
class BranchRequestsPodpoolCreateChildMsg(val destBreedCid: Cid, val destIns: Array<DynamicConcept>?, val parentBrid: Brid): MessageMsg()

/**
 *      Branch reports to the pod pool and its parent its creation and tells them its ownBrid.
 *  @param parentBrid To be able to find parent
 *  @param ownBrid
 *  @param ownBreedCid
 */
class BranchReportsPodpoolAndParentItsCreationMsg(val parentBrid: Brid, val ownBrid: Brid, val ownBreedCid: Cid): MessageMsg()

/**
 *      Base for messages addressed to pods (inter branch messages)
 *  @param destBrid identifier of the destination branch
 */
abstract class PodIbr(val destBrid: Int): MessageMsg()

class UserTellsCircleIbr(destBrid: Int, val text: String): PodIbr(destBrid)