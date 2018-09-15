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

/**
        Send to log the toString() of a concept.
    Parameters:
        cld = caldron as a name space for cids.
        operandCid = a concept from the caldron's name space to print out
*/
@(2, StatCallType.p0Calp1Cid)
void logConcept(Caldron cld, Cid operandCid) {
    logit(cld._cpt_(operandCid).toString, TermColor.blue);
}

/**
        Send user a Tid.
    Parameters:
        cld = caldron as a name space for cids.
        operandCid = a tid primitive, containing the Tid.
*/
@(3, StatCallType.p0Calp1Cid)
void sendTidToUser(Caldron cld, Cid operandCid) {
    assert(false, "not realized yet.");
}

/// Load a concept into the name space, if not loaded, and activate it.
@(4, StatCallType.p0Calp1Cid)
void activateStat(Caldron cld, Cid operandCid)
{
    auto op = scast!BinActivationIfc(cld._cpt_(operandCid));
    op.activate;
}

/// Load a concept into the name space, if not loaded, and activate it.
@(5, StatCallType.p0Calp1Cid)
void anactivateStat(Caldron cld, Cid operandCid)
{
    auto op = scast!BinActivationIfc(cld._cpt_(operandCid));
    op.anactivate;
}

/**
        Send a branch concept object.
    Parameters:
        cld = caldron as a name space for cids.
        breedCid = breed of the branch to send the message to
        loadCid = cid of a concept in the caldron's name space to send
*/
@(6, StatCallType.p0Calp1Cid)
void sendConceptToBranch(Caldron cld, Cid breedCid, Cid loadCid) {
//    assert(false, "not realized yet.");
}
