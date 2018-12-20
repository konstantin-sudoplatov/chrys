package libmain

import atn.AttentionDispatcher
import atn.PodPool

/** Number of pods in the pool */
const val POD_POOL_SIZE = 10

/** Shared attention dispatcher object */
val _atnDispatcher_ = AttentionDispatcher().also { it.start() }

/** Shared console thread object */
val _console_ = ConsoleThread().also { it.start() }

/** Pool of pods. The pod thread started in the constructor. */
val _pp_ = PodPool().also { it.start() }

/** Shered spirit map object */
val _sm_ = SpiritMap()