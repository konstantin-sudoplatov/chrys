package libmain

import atn.AttentionDispatcher
import atn.Podpool

/** Number of pods in the pool */
const val POD_POOL_SIZE = 10

/** Shared attention dispatcher object */
val _atnDispatcher_ = AttentionDispatcher()

/** Shared console thread object */
val _console_ = ConsoleThread()

/** Pool of pods. The pod thread started in the constructor. */
val _pp_ = Podpool()

/** Shered spirit map object */
val _sm_ = SpiritMap()