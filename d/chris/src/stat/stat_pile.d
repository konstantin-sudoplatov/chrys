/// Unsorted static concepts
module stat_pile;
import std.stdio;

import global, tools;
import interfaces;
import attn_circle_thread: Caldron;

/// Raise stop flag on a caldron.
@(1, StatCallType.p0Cal)
void _stopAndWait_(Caldron cld){
    cld._requestStopAndWait_;
}

/// Load a concept into the name space, if not loaded, and activate it.
@(2, StatCallType.p0Calp1Cid)
void activate_stat(Caldron cld, Cid operandCid)
{
    auto op = scast!BinActivationIfc(cld._cpt_(operandCid));
    op.activate;
}

/// Load a concept into the name space, if not loaded, and activate it.
@(3, StatCallType.p0Calp1Cid)
void anactivate_stat(Caldron cld, Cid operandCid)
{
    auto op = scast!BinActivationIfc(cld._cpt_(operandCid));
    op.anactivate;
}

@(4, StatCallType.p0Calp1Cid)
void send_tid_to_user(Caldron cld, Cid operandCid) {

}