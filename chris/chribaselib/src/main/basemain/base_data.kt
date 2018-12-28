package basemain

/** Debug level. Since it is a Java static final, the if(DEBUG > 0) {} would not be compiled into the code if
    the DEBUG is 0. So, no runtime overhead. */
const val DEBUG = 0

/** If enabled, names of the concepts will be put in the name map, so they would be visible during debugging. */
const val ENABLE_NAME_MAP = true

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

/** for improving readability of adding premises in the cranks. */
const val NEGATE = true

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
const val POD_THREAD_QUEUE_TIMEOUT = 1000
