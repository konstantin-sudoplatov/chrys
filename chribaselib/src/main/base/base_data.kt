package base

/** Debug level. Since it is a Java static final, the if(debug>0) {} would not compile if the debug is 0. So, no runtime overhead. */
const val debug = 0

/** Type of the concept identifier. */
typealias Cid = Int

/** Up to this number of messages can be put into the ThreadQueue object. If more, the putInQueue() method blocks. */
const val MAX_THREAD_QUEUE_SIZE = 250
