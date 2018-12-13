package base_thread

/** Ancestor for all messages */
open class MessageMsg

/** Request for termination of a thread and all its successors. */
class RequestTerminationMsg(): MessageMsg()

/** If timeout happens in the ThreadQueue object, this message is send for processing instead of a real message. */
class TimeoutMsg(): MessageMsg()