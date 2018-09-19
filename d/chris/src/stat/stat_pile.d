/// Unsorted static concepts
module stat_pile;
import std.stdio;
import std.format;
import std.concurrency;

import global, tools;
import interfaces;
import messages;
import attn_circle_thread: Caldron;
import cpt_abstract, cpt_premises;

/// Raise stop flag on a caldron.
@(1, StatCallType.p0Cal)
void stopAndWait_stat(Caldron cald){
    cald.requestStopAndWait;
}

/**
        Send to log the toString() of a concept.
    Parameters:
        cld = caldron as a name space for cids.
        operandCid = a concept from the caldron's name space to print out
*/
@(2, StatCallType.p0Calp1Cid)
void logConcept_stat(Caldron cald, Cid conceptCid) {
    logit(cald[conceptCid].toString, TermColor.blue);
}

/**
        Increment debug level, but not more than 3.
    Parameters:
        cld = caldron as a name space for cids.
*/
@(3, StatCallType.p0Cal)
void incrementDebugLevel_stat(Caldron cald) {
    import attn_circle_thread: dynDebug;
    if (dynDebug < 3 ) ++dynDebug;
}

/**
        Decrement debug level, but not less than 0.
    Parameters:
        cld = caldron as a name space for cids.
*/
@(4, StatCallType.p0Cal)
void decrementDebugLevel_stat(Caldron cald) {
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
void sendTidToUser_stat(Caldron cald, Cid userTidPremCid, Cid ulineBreedCid) {
    import std.concurrency: Tid, send;
    import messages: CircleSuppliesUserWithItsTid;
    checkCid!TidPremise(cald, userTidPremCid);
    checkCid!Breed(cald, ulineBreedCid);

    auto userTidPrem = cast(TidPremise)cald[userTidPremCid];
    auto ulineBreed = cast(Breed)cald[ulineBreedCid];
    send(userTidPrem.tid, new immutable CircleSuppliesUserWithItsTid(ulineBreed.tid));
}

/**
            Set activation for a concept in the current space name.
    Parameters:
        cald = current caldron
        conceptCid = concept in the current name space to set activation
        activation = activation value
*/
@(6, StatCallType.p0Calp1Cidp2Float)
void setActivation_stat(Caldron cald, Cid conceptCid, float activation)
{
    assert(cast(BinActivationIfc)cald[conceptCid] || cast(EsquashActivationIfc)cald[conceptCid],
            format!("Destination concept %s must imlement one of the activation interfaces (except ActivationIfc)," ~
             "which it doesn't.")(typeid(cald[conceptCid])));
    if      // is it binary activation?
            (auto op = cast(BinActivationIfc)cald[conceptCid])
        if(activation > 0)
        {
            assert(activation == 1, format!"activation for BinActivationIfc can only be 1 or -1, but it is %s"
                    (activation));
            op.activate;
        }
        else {
            assert(activation == -1, format!"activation for BinActivationIfc can only be 1 or -1, but it is %s"
                    (activation));
            op.anactivate;
        }
    else    //no: it is esquash
        (cast(EsquashActivationIfc)cald[conceptCid]).activation = activation;
}

/**
            Set activation for a concept in another space name.
    Parameters:
        cald = current caldron
        branchBreedCid = destination caldron breed
        conceptCid = concept to set activation
        activation = activation value
*/
@(7, StatCallType.p0Calp1Cidp2Cidp3Float)
void setActivationInBranch_stat(Caldron cald, Cid branchBreedCid, Cid conceptCid, float activation)
{
    checkCid!BinActivationIfc(cald, conceptCid);
    auto br = scast!(Breed)(cald[branchBreedCid]);
    send(br.tid, new immutable IbrSetActivationMsg(conceptCid, activation));
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
void sendConceptToBranch_stat(Caldron cald, Cid breedCid, Cid loadCid) {
    checkCid!Breed(cald, breedCid);
    checkCid!Concept(cald, loadCid);

    Breed br = cast(Breed)cald[breedCid];
    try {
        send(br.tid, new immutable IbrSingleConceptPackageMsg(cald[loadCid]));
    } catch(Exception e) {  // Something happened with the destination thread
        // anactivate the destination thread breed
        br.anactivate;
    }
}
