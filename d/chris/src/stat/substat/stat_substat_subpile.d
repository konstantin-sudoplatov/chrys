module stat_substat_subpile;
import std.stdio;

import global, tools;
import attn_circle_thread: Caldron;
import cpt_abstract: StatCallType;



/**
        Test function
*/
@(7, StatCallType.rCidar_p0Cal_p1Cidar_p2Obj)
Cid[] subpile_test_stat(Caldron nameSpace, Cid[] paramCids, Object extra)
{
writeln("In subpile_test_stat");
    return [0];
}
