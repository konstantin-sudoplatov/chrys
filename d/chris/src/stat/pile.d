/// Unsorted static concepts
module stat.pile;
import std.stdio;

import global_data, tools, common_types;
import attn.attn_circle_thread;

/**
        Load a concept into the name space, if not loaded, and activate it.
*/
@AnnoStat(1, CallType.type1)
Cid activate_stat(Caldron nameSpace, Cid[] paramCids, Object extra) {
    return 0;
}
