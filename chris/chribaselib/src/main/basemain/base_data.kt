package basemain

/** Global debug level. >= 0 means that debug is enabled, so the debug code should be generated including filling up the
    name map. Since it is a Java static final, there should be no code generated if it is < 0.*/
const val GDEBUG_LV = 0

/** Concept identifier type. */
typealias Cid = Int

/** Concept version type. */
typealias Ver = Short

/** Client identifier type */
typealias Clid = Short

// Ranges of cids and clids as unsigned - generated as ulong, then assigned to Cid and Ver. 0 excluded as special.
@ExperimentalUnsignedTypes
const val MIN_STATIC_CID: ULong = 1u
@ExperimentalUnsignedTypes
const val MAX_STATIC_CID: ULong = 100_000u
@ExperimentalUnsignedTypes
const val MIN_DYNAMIC_CID: ULong = 2_000_000u
@ExperimentalUnsignedTypes
const val MAX_DYNAMIC_CID: ULong = 4_294_967_295u    // UInt.MAX_VALUE (can't be casted statically)
const val MIN_CLID: Int = 1
const val MAX_CLID: Int = 65_535
const val MIN_VER: Ver = 0
const val MAX_VER: Ver = 32766
const val CUR_VER_FLAG: Ver = 32767     // flag, showing that it is the last available concept version
const val VER_GAP: Int = 50             // minimum allowed span between the newest and stale versions (how many is left before the newest will cactch up with the stale).

// Config file
//const val CONFIG_FILE = "chris_config.yaml"       // for loading yaml with class loader
const val CONFIG_FILE = "/home/su/iskint/chris/src/main/chris_config.yaml"      // for loading yaml as a file

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
