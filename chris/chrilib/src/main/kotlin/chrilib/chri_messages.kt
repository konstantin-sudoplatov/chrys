package chrilib

import atn.Brid
import chribase_thread.MessageMsg

class ReaderSendsConsoleLineMsg(val text: String): MessageMsg()

class CirclePromptsUserMsg(): MessageMsg()

class UserRequestsDispatcherCreateNewCircleMsg(val userThread: Thread): MessageMsg()

class CircleSendsUserItsBridMsg(val circleBrid: Brid): MessageMsg()

/**
 *      Base for messages addressed to pods (inter branch messages)
 *  @param destBridInd branch index of the destination branch in the pod
 */
abstract class PodIbr(val destBridInd: Int): MessageMsg()

class UserTellsCircleIbr(destBridInd: Int, val text: String): PodIbr(destBridInd)