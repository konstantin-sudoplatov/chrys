package libmain

import atn.AttentionDispatcher
import atn.Podpool
import basemain.CONFIG_FILE
import basemain.Cid
import basemain.GDEBUG_LV
import db.connectToDb
import java.sql.Connection

/** Number of pods in the pool */
const val POD_POOL_SIZE = 10

/** Configuration parameters from the yaml config. */
var _conf_: Conf = parseConfig(CONFIG_FILE)   // Conf() here is just placeholder, to avoid having Conf? instead of Conf type. It'll be replaced with parsing the config file.

/** The database connection. */
val _db_: Connection = connectToDb(
        _conf_.database["connectionString"] as String,
        _conf_.database["dbName"] as String,
        _conf_.database["user"] as String,
        _conf_.database["password"] as String
)

/** Shared attention dispatcher object */
val _atnDispatcher_ = AttentionDispatcher()

/** Shared console thread object */
val _console_ = ConsoleThread()

/** Pool of pods. */
val _pp_ = Podpool()

/** Shered spirit map object */
val _sm_ = SpiritMap()

/** If the DEBUG_ON flag is on, this map is created and filled up. */
val _nm_: HashMap<Cid, String>? = if(GDEBUG_LV >= 0) HashMap() else null
