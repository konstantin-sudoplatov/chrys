package basemain

/** Global debug level. >= 0 means that debug is enabled, so the debug code should be generated including filling up the
    name map. Since it is a Java static final, there should be no code generated if it is < 0.*/
const val GDEBUG_LV = 0

/** Concept identifier type. */
typealias Cid = Int

/** Concept version type. */
typealias Cvr = Short

// Ranges of cids as unsigned - generated as ulong, then assigned to Cid and Cvr. 0 excluded as special.
@ExperimentalUnsignedTypes
const val MIN_STATIC_CID: ULong = 1u
@ExperimentalUnsignedTypes
const val MAX_STATIC_CID: ULong = 100_000u
@ExperimentalUnsignedTypes
const val MIN_DYNAMIC_CID: ULong = 2_000_000u
@ExperimentalUnsignedTypes
const val MAX_DYNAMIC_CID: ULong = 4_294_967_295u    // UInt.MAX_VALUE (can't be casted statically)

/** Enable/disable manual console input. */
const val EMULATE_CONSOLE = true

/** Maximum number of messages that can be put into the CuteThread object. If more, the putInQueue() method blocks.
    Can be changed on creation the object. */
const val DEFAULT_MAX_THREAD_QUEUE = 0

/** Time in miliseconds the thread is waiting for messages. When it elapses, the TimeoutMsg message is generated.
    Can be changed on creation the object. */
const val DEFAULT_THREAD_QUEUE_TIMEOUT = 0

/** Maximum messages in the pod thread */
const val MAX_POD_THREAD_QUEUE = 250

/** Timeout in miliseconds for the pod thread. */
const val POD_THREAD_QUEUE_TIMEOUT = 10000
