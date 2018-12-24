package atn

import chribase_thread.CuteThread
import chribase_thread.MessageMsg
import chribase_thread.TerminationRequestMsg
import libmain.*

/**
 *      Attention dispatcher:
 *  1. On user request starts and and registers an attention circle and sends it reference to the user thread.
 *  2. On the termination message from libmain initiates termination of all the attention threads
 */
class AttentionDispatcher: CuteThread(0, 0, "dispatcher")
{
    override fun _messageProc(msg: MessageMsg): Boolean {
        when(msg) {

            is UserRequestsDispatcherCreateAttentionCircleMsg -> {
                circleRegistry_[msg.user] = null
                _pp_.putInQueue(UserRequestsDispatcherCreateAttentionCircleMsg(msg.user))

                //Todo: this is a dummy to send user someone to talk to. Remove it when real circle is created.
                _console_.putInQueuePriority(CircleSendsUserItsBridMsg(Brid(Pod("dummy_pod", 0), 0)))
                return true
            }

            is AttentionCircleReportsPodpoolDispatcherUserItsCreation -> {
                circleRegistry_[msg.user] = msg.brid
                msg.user.putInQueue(msg)
                return true
            }

            is TerminationRequestMsg -> {
                _pp_.putInQueue(msg)    // terminate pod pool
                return true    // let the base class terminate the thread
            }
        }

        return false    // message not recognized
    }

    /** Map of Circle branch/user. The branch can be temporarily null, until it is defined on the circle initialization */
    private val circleRegistry_ = hashMapOf<CuteThread, Brid?>()
}