package libmain

import atn.AttentionDispatcher
import atn.Brid
import basemain.EMULATE_CONSOLE
import chribase_thread.MessageMsg
import chribase_thread.CuteThread
import chribase_thread.TerminationRequestMsg
import chribase_thread.TimeoutMsg

/**
 * This class waits for messages from the attention circle which it prints on the console on one hand and user lines from
 * the Readln thread on, which it sends to the circle the other hand. It is also responsible for requesting start of the
 * attention circle. This request on initialization it sends to attention dispatcher. Also it termination of work is
 * originated in here and the request is sent to the libmain thread, which sends it to all subsystems.
 */
class ConsoleThread(threadName: String = "console"): CuteThread(1000, 0, threadName) {

    /**
     *      Request creation the attention circle and wait for its bridObj before doing start() on this thread.
     *  The attention dispatcher thread must be already started.
     *  @param atnDisp initialized and started attention dispatcher thread
     */
    fun requestCreationOfAttentionCircle(atnDisp: AttentionDispatcher) {
        // Require circle creation and wait for its bridObj
        atnDisp.putInQueue(UserRequestsDispatcherCreateAttentionCircleMsg(this))

        // Get the circle bridObj.
        assert(!this.isAlive) {"This console thread must not be yet started."}
        var msg: MessageMsg
        do {
            msg = _getBlocking()
        } while(msg !is AttentionCircleReportsPodpoolDispatcherUserItsCreation)
        circleBrid_ = msg.bridObj
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
                    _atnDispatcher_.putInQueue(TerminationRequestMsg())
                    this.putInQueue(TerminationRequestMsg())
                }
                else {//no: resend the console line to the circle
                    circleBrid_!!.pod.putInQueue(UserTellsCircleIbr(circleBrid_!!.brid, msg.text))
                }
                return true
            }

            is CirclePromptsUserMsg -> {
                if(!EMULATE_CONSOLE)
                    print("> ")
                else {  // Send to the circle next line from the list or request termination
                    val line = userLinesIterator_.next()
                    print("> $line")
                    if      // did the emulator request termination?
                            (line == "p")
                    {   //yes: request termination from dispatcher and to itself
                        _atnDispatcher_.putInQueue(TerminationRequestMsg())
                        this.putInQueue(TerminationRequestMsg())
                    }
                    else {//no: resend the console line to the circle
                        circleBrid_!!.pod.putInQueue(UserTellsCircleIbr(circleBrid_!!.brid, line))
                    }
                }
                return true
            }

            is CircleSendsUserItsBridMsg -> {
                circleBrid_ = msg.circleBrid
                return true
            }

            is TimeoutMsg -> {
                if(EMULATE_CONSOLE)
                {  // Send to the circle next line from the list or request termination
                    val line = userLinesIterator_.next()
                    println("> $line")
                    if      // did the emulator request termination?
                            (line == "p")
                    {   //yes: request termination from dispatcher and to itself
                        _atnDispatcher_.putInQueue(TerminationRequestMsg())
                        this.putInQueue(TerminationRequestMsg())
                    }
                    else {//no: resend the console line to the circle
                        circleBrid_!!.pod.putInQueue(UserTellsCircleIbr(circleBrid_!!.brid, line))
                    }
                }
                return true
            }

            is TerminationRequestMsg -> {
                return true    // let the base class terminate the thread
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

    /** The circle branch to talk to. */
    private var circleBrid_: Brid? = null

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
