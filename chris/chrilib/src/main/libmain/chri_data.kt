package libmain

import atn.AttentionDispatcher

/** Number of pods in the pool */
const val POD_POOL_SIZE = 10

/** Shared attention dispatcher object */
val _atnDispatcher_ = AttentionDispatcher()

/** Shared console thread object */
val _console_ = ConsoleThread()
