/// Unsorted static concepts
module stat_pile;
import std.stdio;

import global, tools;
import interfaces;
import attn_circle_thread: Caldron;
import cpt_abstract, cpt_premises;

/// Raise stop flag on a caldron.
@(1, StatCallType.p0Cal)
void _stopAndWait_(Caldron cald){
    cald.requestStopAndWait;
}

/**
        Send to log the toString() of a concept.
    Parameters:
        cld = caldron as a name space for cids.
        operandCid = a concept from the caldron's name space to print out
*/
@(2, StatCallType.p0Calp1Cid)
void logConcept(Caldron cald, Cid conceptCid) {
    logit(cald[conceptCid].toString, TermColor.blue);
}

/**
        Increment debug level, but not more than 3.
    Parameters:
        cld = caldron as a name space for cids.
*/
@(3, StatCallType.p0Cal)
void incrementDebugLevel(Caldron cald) {
    import attn_circle_thread: dynDebug;
    if (dynDebug < 3 ) ++dynDebug;
}

/**
        Decrement debug level, but not less than 0.
    Parameters:
        cld = caldron as a name space for cids.
*/
@(4, StatCallType.p0Cal)
void decrementDebugLevel(Caldron cald) {
    import attn_circle_thread: dynDebug;
    if (dynDebug < 0 ) --dynDebug;
}

/**
        Send user a Tid. It supposed to be tid of the uline branch.
    Parameters:
        cld = caldron as a name space for cids.
        operandCid = a tid primitive, containing the Tid.
*/
@(5, StatCallType.p0Calp1Cidp2Cid)
void sendTidToUser(Caldron cald, Cid userTidPremCid, Cid ulineBreedCid) {
    import std.concurrency: Tid, send;
    import messages: CircleSuppliesUserWithItsTid;
    checkCid!TidPremise(cald, userTidPremCid);
    checkCid!Breed(cald, ulineBreedCid);

    auto userTidPrem = cast(TidPremise)cald[userTidPremCid];
    auto ulineBreed = cast(Breed)cald[ulineBreedCid];
    send(userTidPrem.tid, new immutable CircleSuppliesUserWithItsTid(ulineBreed.tid));
}

/// Load a concept into the name space, if not loaded, and activate it.
@(6, StatCallType.p0Calp1Cid)
void activateStat(Caldron cald, Cid operandCid)
{
    auto op = scast!BinActivationIfc(cald[operandCid]);
    op.activate;
}

/// Load a concept into the name space, if not loaded, and activate it.
@(7, StatCallType.p0Calp1Cid)
void anactivateStat(Caldron cald, Cid operandCid)
{
    auto op = scast!BinActivationIfc(cald[operandCid]);
    op.anactivate;
}

/**
        Send concept object to a branch. The concept is injected into the branch's name space. If there is already such a concept
    in the branch name space, it will be overriden. The concept is treated by the receiving side like immutable, i.e before
    injection it will be cloned.
    Parameters:
        cld = caldron as a name space for cids.
        breedCid = breed of the addressed branch as its identifier
        loadCid = concept to send
*/
@(8, StatCallType.p0Calp1Cidp2Cid)
void sendConceptToBranch(Caldron cald, Cid breedCid, Cid loadCid) {
    import std.concurrency: Tid, send;
    import messages: SingleConceptPackageMsg;
    checkCid!Breed(cald, breedCid);
    checkCid!Concept(cald, loadCid);

    Breed br = cast(Breed)cald[breedCid];
    try {
        send(br.tid, new immutable SingleConceptPackageMsg(cald[loadCid]));
    } catch(Exception e) {  // Something happened with the destination thread
        // anactivate the destination thread breed
        br.anactivate;
    }
}
