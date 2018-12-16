package atn

import chribase_thread.CuteThread
import chribase_thread.MessageMsg
import chribase_thread.TerminationRequestMsg
import libmain.CircleSendsUserItsBridMsg
import libmain.UserRequestsDispatcherCreateNewCircleMsg
import libmain._console_

/**
 *      Attention dispatcher:
 *  1. On user request starts and and registers an attention circle and sends it reference to the user thread.
 *  2. On the termination message from libmain initiates termination of all the attention threads
 */
class AttentionDispatcher(threadName: String = "dispatcher"): CuteThread(0, 0, threadName)
{
    override fun _messageProc(msg: MessageMsg): Boolean {
        when(msg) {
            is UserRequestsDispatcherCreateNewCircleMsg -> {
                //Todo: this is for debagging, need real implementation
                _console_.putInQueuePriority(CircleSendsUserItsBridMsg(Brid(Pod("dummy_pod", 0), 0)))
                return true
            }

            is TerminationRequestMsg -> {
                //todo: not forget to terminate all circles and pods
                return true    // let the base class terminate the thread
            }
        }

        return false    // message not recognized
    }
}