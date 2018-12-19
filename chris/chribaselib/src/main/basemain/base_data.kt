package basemain

/** Concept identifier type. */
typealias Cid = Int

/** Concept version type. */
typealias Cvr = Short

// Ranges of cids as unsigned - generated as ulong, then assigned to Cid and Cvr. 0 excluded as special.
const val MIN_STATIC_CID = 1;
const val MAX_STATIC_CID = 100_000;
const val MIN_DYNAMIC_CID = 2_000_000;
@ExperimentalUnsignedTypes
const val MAX_DINAMIC_CID = UInt.MAX_VALUE;

/** Debug level. Since it is a Java static final, the if(basemain.debug>0) {} would not compile if the basemain.debug is 0. So, no runtime overhead. */
const val DEBUG = 0

/** Enable/disable manual console input. */
const val EMULATE_CONSOLE = true

/** Maximum number of messages that can be put into the CuteThread object. If more, the putInQueue() method blocks.
    Can be changed on creation the object. */
const val DEFAULT_MAX_THREAD_QUEUE = 250

/** Time in miliseconds the thread is waiting for messages. When it elapses, the TimeoutMsg message is generated.
    Can be changed on creation the object. */
const val DEFAULT_THREAD_QUEUE_TIMEOUT = 1000
