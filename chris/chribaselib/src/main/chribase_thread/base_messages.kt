package chribase_thread

/** Ancestor for all messages */
open class MessageMsg {
    override fun toString(): String {
        return this::class.simpleName?: "anonymous"
    }

    open fun toStr(): String {
        return this::class.simpleName?: "anonymous"
    }
}

/** Request for termination of a thread and all its successors. */
class TerminationRequestMsg(): MessageMsg()

/** If timeout happens in the CuteThread object, this message is send for processing instead of a real message. */
class TimeoutMsg(): MessageMsg()
