/// Unsorted static concepts
module stat.pile;
import std.stdio;

import global, tools, common_types;
import attn.attn_circle_thread: Caldron;
import cpt.holy: StatCallType;

/**
        Load a concept into the name space, if not loaded, and activate it.
*/
@(5, StatCallType.rCid_p0Cal_p1Cidar_p2Obj)
Cid anactivate_stat(Caldron nameSpace, Cid[] paramCids, Object extra)
{
    writeln("In anactivate_stat");
    return 0;
}

/**
        Load a concept into the name space, if not loaded, and activate it.
*/
@(1, StatCallType.rCid_p0Cal_p1Cidar_p2Obj)
Cid activate_stat(Caldron nameSpace, Cid[] paramCids, Object extra)
{
    writeln("In activate_stat");
    return 0;
}
