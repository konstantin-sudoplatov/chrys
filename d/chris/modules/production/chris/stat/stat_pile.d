/// Unsorted static concepts
module stat_pile;
import std.stdio;
import std.format;

import global, tools_pile;
import interfaces;
import attn_circle_thread: Caldron;
import cpt_abstract, cpt_premises;

/// Raise stop flag on a caldron.
@(1, StatCallType.p0Cal)
void stopAndWait_stat(Caldron cld){
    cld.requestStopAndWait;
}

/**
        Send to log the toString() of a concept.
    Parameters:
        cld = caldron as a name space for cids.
        operandCid = a concept from the caldron's name space to print out
*/
@(2, StatCallType.p0Calp1Cid)
void logConcept_stat(Caldron cld, Cid conceptCid) {
    logit(cld[conceptCid].toString, TermColor.blue);
}

/**
        Set the dynamic debugging level in the module attn_circle_thread.
    Parameters:
        cld = caldron as a name space for cids (not used here)
*/
@(3, StatCallType.p0Cal)
void setDebugLevel_1_stat(Caldron cld) {
    import attn_circle_thread: dynDebug;
    dynDebug = 1;
}

/**
        Set the dynamic debugging level in the module attn_circle_thread.
    Parameters:
        cld = caldron as a name space for cids (not used here)
*/
@(4, StatCallType.p0Cal)
void setDebugLevel_2_stat(Caldron cld) {
    import attn_circle_thread: dynDebug;
    dynDebug = 2;
}

/**
        Set the dynamic debugging level in the module attn_circle_thread.
    Parameters:
        cld = caldron as a name space for cids (not used here)
*/
@(5, StatCallType.p0Cal)
void setDebugLevel_0_stat(Caldron cld) {
    import attn_circle_thread: dynDebug;
    dynDebug = 0;
}

/**
        Send user a Tid. It supposed to be tid of the uline branch.
    Parameters:
        cld = caldron as a name space for cids.
        operandCid = a tid primitive, containing the user Tid.
*/
@(6, StatCallType.p0Calp1Cidp2Cid)
void sendTidToUser_stat(Caldron cld, Cid userTidPremCid, Cid ulineBreedCid) {
    import std.concurrency: Tid, send;
    import messages: CircleSuppliesUserWithItsTid_msg;
    checkCid!TidPremise(cld, userTidPremCid);
    checkCid!Breed(cld, ulineBreedCid);

    auto userTidPrem = cast(TidPremise)cld[userTidPremCid];
    auto ulineBreed = cast(Breed)cld[ulineBreedCid];
    send(userTidPrem.tid, new immutable CircleSuppliesUserWithItsTid_msg(ulineBreed.tid));
}

/**
            Send user a message, telling him that the circle waits next line from him.
    Parameters:
        cld = caldron as a name space for cids.
        operandCid = a tid primitive, containing the user Tid.
*/
@(7, StatCallType.p0Calp1Cid)
void requestUserInput(Caldron cld, Cid userTidPremCid) {
    import std.concurrency: Tid, send;
    import messages: CircleListensToUser_msg;
    Tid tid = scast!TidPremise(cld[userTidPremCid]).tid;
    send(tid, new immutable CircleListensToUser_msg);
}

/**
            Send the user thread a line of text to dispay on the screen.
    Parameters:
        cld = caldron as a name space for cids.
        operandCid = a tid primitive, containing the user Tid.
        stringPremCid = string premise, containing the text
*/
@(8, StatCallType.p0Calp1Cidp2Cid)
void sendUserOutput(Caldron cld, Cid userTidPremCid, Cid stringPremCid) {
    import std.concurrency: Tid, send;
    import messages: CircleTalksToUser_msg;
    Tid tid = scast!TidPremise(cld[userTidPremCid]).tid;
    string s = scast!StringPremise(cld[stringPremCid]).line;
    send(tid, new immutable CircleTalksToUser_msg(s));
}

/**
            Activate a concept in own name space
    Parameters:
        cld = current caldron
        cptCid = concept to activate
*/
@(9, StatCallType.p0Calp1Cid)
void activate_stat(Caldron cld, Cid cptCid) {
    (scast!BinActivationIfc(cld[cptCid])).activate;
}

/**
            Anactivate a concept in own name space
    Parameters:
        cld = current caldron
        cptCid = concept to anactivate
*/
@(10, StatCallType.p0Calp1Cid)
void anactivate_stat(Caldron cld, Cid cptCid) {
    (scast!BinActivationIfc(cld[cptCid])).anactivate;
}

/**
            Activate a concept in a given name space
    Parameters:
        cld = current caldron
        destBreedCid = breed of the destination branch
        cptCid = concept to activate
*/
@(11, StatCallType.p0Calp1Cidp2Cid)
void activateRemotely_stat(Caldron cld, Cid destBreedCid, Cid cptCid) {
    import std.concurrency: Tid, send;
    import messages: IbrSetActivation_msg;
    checkCid!BinActivationIfc(cld, cptCid);
    auto br = scast!Breed(cld[destBreedCid]);
    send(br.tid, new immutable IbrSetActivation_msg(cptCid, +1));
}

/**
            Anactivate a concept in a given name space
    Parameters:
        cld = current caldron
        destBreedCid = breed of the destination branch
        cptCid = concept to anactivate
*/
@(12, StatCallType.p0Calp1Cidp2Cid)
void anactivateRemotely_stat(Caldron cld, Cid destBreedCid, Cid cptCid) {
    import std.concurrency: Tid, send;
    import messages: IbrSetActivation_msg;
    checkCid!BinActivationIfc(cld, cptCid);
    auto br = scast!Breed(cld[destBreedCid]);
    send(br.tid, new immutable IbrSetActivation_msg(cptCid, -1));
}

/**
        Send concept object to a branch. The concept is injected into the branch's name space. If there is already such a concept
    in the branch name space, it will be overriden. The concept is cloned on sending, so that receiving side will get the
    concept as it was at this moment. Initially, it was cloned on the receiving side and happened to change through the
    time of traveling. Corrected.
    Parameters:
        cld = caldron as a name space for cids.
        breedCid = breed of the addressed branch as its identifier
        loadCid = concept to send
*/
@(13, StatCallType.p0Calp1Cidp2Cid)
void sendConceptToBranch_stat(Caldron cld, Cid breedCid, Cid loadCid) {
    import std.concurrency: Tid, send;
    import messages: IbrSingleConceptPackage_msg;
    checkCid!Breed(cld, breedCid);
    checkCid!Concept(cld, loadCid);

    Breed br = cast(Breed)cld[breedCid];
    try {
        send(br.tid, new immutable IbrSingleConceptPackage_msg(cld[loadCid].clone));
    } catch(Exception e) {  // Something happened with the destination thread
        // anactivate the destination thread breed
        br.anactivate;
    }
}

/**
            Move the last in queue line from a string buffer to a string premise.
    Parameters:
        bufPrem = string queue premise to take lines from
        strPrem = string premise to put the line in
*/
@(14, StatCallType.p0Calp1Cidp2Cid)
void getUserInputLineFromBuffer_stat(Caldron cld, Cid bufPrem, Cid strPrem) {
    checkCid!(BinActivationIfc)(cld, bufPrem);
    checkCid!(BinActivationIfc)(cld, strPrem);
    auto uBuf = scast!StringQueuePremise(cld[bufPrem]);
    assert(!uBuf.empty && uBuf.activation == 1, "Must not get called if the user buffer is empty.");
    auto uline = scast!StringPremise(cld[strPrem]);
    uline.line = uBuf.pull;
    uline.activate;
    if(uBuf.empty) uBuf.anactivate;
}