/// Unsorted static concepts
module stat_pile;
import std.stdio;

import global, tools;
import attn_circle_thread: Caldron;

/**
        Raise stop flag on a caldron.
*/
@(1, StatCallType.none)
void _stopAndWait_(){
    writeln("In stop and wait");
}

/**
        Load a concept into the name space, if not loaded, and activate it.
*/
@(2, StatCallType.rCid_p0Cal_p1Cidar_p2Obj)
Cid activate_stat(Caldron nameSpace, Cid[] paramCids, Object extra)
{
    writeln("In activate_stat");
    return 0;
}

/**
        Load a concept into the name space, if not loaded, and activate it.
*/
@(3, StatCallType.rCid_p0Cal_p1Cidar_p2Obj)
Cid anactivate_stat(Caldron nameSpace, Cid[] paramCids, Object extra)
{
    writeln("In anactivate_stat");
    return 0;
}
