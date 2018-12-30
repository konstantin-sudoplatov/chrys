package libmain

import atn.AttentionDispatcher
import atn.Podpool
import basemain.Cid
import basemain.DEBUG_ON

/** Number of pods in the pool */
const val POD_POOL_SIZE = 10

/** Shared attention dispatcher object */
val _atnDispatcher_ = AttentionDispatcher()

/** Shared console thread object */
val _console_ = ConsoleThread()

/** Pool of pods. */
val _pp_ = Podpool()

/** Shered spirit map object */
val _sm_ = SpiritMap()

/** If the DEBUG_ON flag is on, this map is created and filled up. */
val _nm_: HashMap<Cid, String>? = if(DEBUG_ON) HashMap() else null