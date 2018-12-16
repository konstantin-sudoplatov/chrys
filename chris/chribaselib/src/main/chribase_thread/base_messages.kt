package chribase_thread

/** Ancestor for all messages */
open class MessageMsg

/** Request for termination of a thread and all its successors. */
class TerminationRequestMsg(): MessageMsg()

/** If timeout happens in the CuteThread object, this message is send for processing instead of a real message. */
class TimeoutMsg(): MessageMsg()

/** Line of text. */
class TextLineMsg(val text: String): MessageMsg()