package chrilib

import atn.AttentionDispatcher
import atn.Brid
import chribase.EMULATE_CONSOLE
import chribase_thread.MessageMsg
import chribase_thread.CuteThread
import chribase_thread.RequestTerminationMsg

/**
 * This class waits for messages from the attention circle which it prints on the console on one hand and user lines from
 * the Readln thread on, which it sends to the circle the other hand. It is also responsible for requesting start of the
 * attention circle. This request on initialization it sends to attention dispatcher. Also it termination of work is
 * originated in here and the request is sent to the main thread, which sends it to all subsystems.
 */
class ConsoleThread(): CuteThread(0, 0) {

    /**
     *      Request creation the attention circle and wait for its brid before doing start() on this thread.
     *  The attention dispatcher thread must be already started.
     *  @param atnDisp initialized and started attention dispatcher thread
     */
    fun initialize(atnDisp: AttentionDispatcher) {
        // Require circle creation and wait for its brid
        atnDisp.putInQueue(UserRequestsDispatcherCreateNewCircleMsg(this))
        while(true) {
            val msg = _getBlocking()
            if(msg is CirleSendsUserItsBridMsg) {
                circleBrid_ = msg.circleBrid
                break
            }
        }
    }

    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //                                  Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$ protected data ---$$$---$$$---$$$---$$$---$$$--

    //---$$$---$$$---$$$---$$$---$$$--- protected methods ---$$$---$$$---$$$---$$$---$$$---

    protected override fun _messageProc(msg: MessageMsg?): Boolean {
        when (msg) {
            is ReaderSendsConsoleLineMsg -> {
                if      // did user request termination?
                        (msg.text == "p")
                {   //yes: request termination from dispatcher and pass it to the ancesstor
                    _atnDispatcher_.putInQueue(RequestTerminationMsg())
                    this.putInQueue(RequestTerminationMsg())
                }
                else {//no: resend the console line to the circle
                    circleBrid_.pod.putInQueue(UserTellsCircleIbr(circleBrid_.branchInd, msg.text))
                }
                return true
            }

            is CirclePromptsUserMsg -> {
                if(!EMULATE_CONSOLE)
                    print("> ")
                else {
                    // Send to the circle next line from the list
                    val line = userLinesIterator_.next()
                    print("> $line")
                    if      // did the emulator request termination?
                            (line == "p")
                    {   //yes: request termination from dispatcher and to itself
                        _atnDispatcher_.putInQueue(RequestTerminationMsg())
                        this.putInQueue(RequestTerminationMsg())
                    }
                    else {//no: resend the console line to the circle
                        circleBrid_.pod.putInQueue(UserTellsCircleIbr(circleBrid_.branchInd, line))
                    }
                }
                return true
            }
        }
        return false    // the "false" will make the CuteThread object to log an unrecognized message
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%--- private data ---%%%---%%%---%%%---%%%---%%%---%%%

    private lateinit var circleBrid_: Brid

    /** This iterator used to answer circle's prompts at debugging */
    private lateinit var userLinesIterator_: ListIterator<String>

    /**
     *      Object construction.
     */
    init {
        if(!EMULATE_CONSOLE)
            // Thread for asynchronous reading from console
            Thread() {
                print("> ")
                while(true) {
                    val line = readLine()
                    _console_.putInQueue(ReaderSendsConsoleLineMsg(line ?: ""))
                    if(line == "p") break
                }
            }.start()
        else {
            userLinesIterator_ = listOf("hello", "world", "p").listIterator()
        }
    }
}
