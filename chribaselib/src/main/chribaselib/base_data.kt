package chribaselib

/** Type of the concept identifier. */
typealias Cid = Int

/** Up to this number of messages can be put into the ThreadQueue object. If more, the putInQueue() method blocks. */
const val MAX_THREAD_QUEUE_SIZE = 250
