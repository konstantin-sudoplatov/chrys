package libmain

import atn.Brid
import basemain.Cid
import chribase_thread.CuteThread
import chribase_thread.MessageMsg
import cpt.Breed

class ReaderSendsConsoleLineMsg(val text: String): MessageMsg()

class CirclePromptsUserMsg(): MessageMsg()

class UserRequestsDispatcherCreateAttentionCircleMsg(val userThread: CuteThread): MessageMsg()

class AttentionCircleReportsPodpoolDispatcherUserItsCreationMsg(val userThread: CuteThread, val brid: Brid): MessageMsg()

class BranchRequestsCreationChildMsg(val destBreedCid: Cid, parentBrid: Brid): MessageMsg()

/**
 *      Base for messages addressed to pods (inter branch messages)
 *  @param destBrid identifier of the destination branch
 */
abstract class PodIbr(val destBrid: Int): MessageMsg()

class UserTellsCircleIbr(destBrid: Int, val text: String): PodIbr(destBrid)