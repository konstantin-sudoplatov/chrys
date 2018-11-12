/// Unsorted static concepts
module stat.stat_main;
import std.stdio;
import std.string;

import proj_data, proj_funcs;

import stat.stat_types;
import cpt.cpt_interfaces, cpt.abs.abs_concept, cpt.abs.abs_premise, cpt.cpt_premises;
import atn.atn_caldron;
import chri_data, chri_types;


/// Raise stop flag on a caldron.
@(1, StatCallType.p0Cal)
void wait_stat(Caldron cld){
    cld.requestWait;
}

/// Raise stop flag on a caldron.
@(2, StatCallType.p0Cal)
void stop_stat(Caldron cld){
    import std.concurrency: Tid, send;
    import messages: IbrBranchDevise_msg;

    //yes: send to the parent my devise
    send(cld.parentThread.tid, new immutable IbrBranchDevise_msg(cld.breedCid));

    // request leaving the reasoning
    cld.requestStop;
}

/// Raise stop flag on a caldron.
@(3, StatCallType.p0Cal)
void checkUp_stat(Caldron cld){
    cld.checkUp;
}

/**
        Send to log the toString() of a concept.
    Parameters:
        cld = caldron as a name space for cids.
        operandCid = a concept from the caldron's name space to print out
*/
@(4, StatCallType.p0Calp1Cid)
void logConcept_stat(Caldron cld, Cid conceptCid) {
    logit(cld[conceptCid].toString, TermColor.blue);
}

/**
        Set the dynamic debugging level in the module attn_circle_thread.
    Parameters:
        cld = caldron as a name space for cids (not used here)
*/
@(5, StatCallType.p0Cal)
void setDebugLevel_1_stat(Caldron cld) {
    debug cld.dynDebug = 1;
}

/**
        Set the dynamic debugging level in the module attn_circle_thread.
    Parameters:
        cld = caldron as a name space for cids (not used here)
*/
@(6, StatCallType.p0Cal)
void setDebugLevel_2_stat(Caldron cld) {
    debug cld.dynDebug = 2;
}

/**
        Set the dynamic debugging level in the module attn_circle_thread.
    Parameters:
        cld = caldron as a name space for cids (not used here)
*/
@(7, StatCallType.p0Cal)
void setDebugLevel_0_stat(Caldron cld) {
    debug cld.dynDebug = 0;
}

/**
        Send user a Tid. It supposed to be tid of the uline branch.
    Parameters:
        cld = caldron as a name space for cids.
        operandCid = a tid primitive, containing the user Tid.
*/
@(8, StatCallType.p0Calp1Cidp2Cid)
void sendTidToUser_stat(Caldron cld, Cid userTidPremCid, Cid ulineBreedCid) {
    import std.concurrency: Tid, send;
    import messages: CircleProvidesUserWithItsTid_msg;
    checkCid!TidPrem(cld, userTidPremCid);
    checkCid!Breed(cld, ulineBreedCid);

    auto userTidPrem = cast(TidPrem)cld[userTidPremCid];
    auto ulineBreed = cast(Breed)cld[ulineBreedCid];
    send(userTidPrem.tid, new immutable CircleProvidesUserWithItsTid_msg(ulineBreed.tid));
}

/**
            Send user a message, telling him that the circle waits next line from him.
    Parameters:
        cld = caldron as a name space for cids.
        operandCid = a tid primitive, containing the user Tid.
*/
@(9, StatCallType.p0Calp1Cid)
void requestUserInput(Caldron cld, Cid userTidPremCid) {
    import std.concurrency: Tid, send;
    import messages: CircleListensToUser_msg;
    Tid tid = scast!TidPrem(cld[userTidPremCid]).tid;
    send(tid, new immutable CircleListensToUser_msg);
}

/**
            Send the user thread a line of text to dispay on the screen.
    Parameters:
        cld = caldron as a name space for cids.
        operandCid = a tid primitive, containing the user Tid.
        stringPremCid = string premise, containing the text
*/
@(10, StatCallType.p0Calp1Cidp2Cid)
void sendUserOutput(Caldron cld, Cid userTidPremCid, Cid stringPremCid) {
    import std.concurrency: Tid, send;
    import messages: CircleTellsUser_msg;
    Tid tid = scast!TidPrem(cld[userTidPremCid]).tid;
    string s = scast!StringPrem(cld[stringPremCid]).text;
    send(tid, new immutable CircleTellsUser_msg(s));
}

/**
            Activate a concept in own name space
    Parameters:
        cld = current caldron
        cptCid = concept to activate
*/
@(11, StatCallType.p0Calp1Cid)
void activate_stat(Caldron cld, Cid cptCid) {
    (scast!BinActivationIfc(cld[cptCid])).activate;
}

/**
            Anactivate a concept in own name space
    Parameters:
        cld = current caldron
        cptCid = concept to anactivate
*/
@(12, StatCallType.p0Calp1Cid)
void anactivate_stat(Caldron cld, Cid cptCid) {
    (scast!BinActivationIfc(cld[cptCid])).anactivate;
}

/**
        Activate a concept in a given name space. The receiving side will also get a tid premise with tid of the
    caller branch.
    Parameters:
        cld = current caldron
        destBreedCid = breed of the destination branch
        cptCid = concept to activate
*/
@(13, StatCallType.p0Calp1Cidp2Cid)
void activateRemotely_stat(Caldron cld, Cid destTidCid, Cid cptCid) {
    import std.concurrency: Tid, send;
    import messages: IbrSetActivation_msg;
    checkCid!BinActivationIfc(cld, cptCid);
    auto br = scast!TidPrem(cld[destTidCid]);
    assert(br.tid != Tid.init, "%s, %s(%,?s) is not initialized: tid = %s".format(cld.cldName, cptName(destTidCid),
            '_', destTidCid, '_', br.tid));
    send(br.tid, new immutable IbrSetActivation_msg(cptCid, +1));
}

/**
        Anactivate a concept in a given name space. The receiving side will also get a tid premise with tid of the
    caller branch.
    Parameters:
        cld = current caldron
        destBreedCid = breed of the destination branch
        cptCid = concept to anactivate
*/
@(14, StatCallType.p0Calp1Cidp2Cid)
void anactivateRemotely_stat(Caldron cld, Cid destTidCid, Cid cptCid) {
    import std.concurrency: Tid, send;
    import messages: IbrSetActivation_msg;
    checkCid!BinActivationIfc(cld, cptCid);
    auto br = scast!TidPrem(cld[destTidCid]);
    assert(br.tid != Tid.init, "%s, %s(%,?s) is not initialized: tid = %s".format(cld.cldName, cptName(destTidCid),
            '_', destTidCid, '_', br.tid));
    send(br.tid, new immutable IbrSetActivation_msg(cptCid, -1));
}

/**
        Send concept object to a branch. The concept is injected into the branch's name space. If there is already such concept
    in the branch name space, it will be overriden. The concept is cloned on sending, so that receiving side will get the
    concept as it was at the moment of calling this function. If it were cloned on the receiving side it could get changed
    during the traveling time. The receiving side will also get a tid premise with tid of the caller branch.
    Parameters:
        cld = caldron as a name space for cids.
        breedCid = breed of the addressed branch as its identifier
        loadCid = concept to send
*/
@(15, StatCallType.p0Calp1Cidp2Cid)
void sendConceptToBranch_stat(Caldron cld, Cid destTidCid, Cid loadCid) {
    import std.concurrency: Tid, send;
    import messages: IbrSingleConceptPackage_msg;
    checkCid!Concept(cld, loadCid);
    Concept cpt = cld[loadCid];
    assert(!cast(ActivationIfc)cpt || (cast(ActivationIfc)cpt).activation > 0,
            "%s, %s(%,?s) is anactivated, which should not be.".format(cld.cldName, cptName(loadCid), '_', loadCid));

    auto br = scast!TidPrem(cld[destTidCid]);
    assert(br.tid != Tid.init, "%s, %s(%,?s) is not initialized: tid = %s".format(cld.cldName, cptName(destTidCid),
            '_', destTidCid, '_', br.tid));
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
        cld = caldron
        bufPrem = string queue premise to take lines from
        strPrem = string premise to put the line in
*/
@(16, StatCallType.p0Calp1Cidp2Cid)
void popUserInputLineFromBuffer_stat(Caldron cld, Cid bufPrem, Cid strPrem) {
    checkCid!(BinActivationIfc)(cld, bufPrem);
    checkCid!(BinActivationIfc)(cld, strPrem);
    auto uBuf = scast!StringQueuePrem(cld[bufPrem]);
    assert(!uBuf.empty && uBuf.activation == 1, "Must not get called if the user buffer is empty.");
    auto uline = scast!StringPrem(cld[strPrem]);
    uline.text = uBuf.popFront;
    uline.activate;
    if(uBuf.empty) uBuf.anactivate;
}

/**
        Copy content of one premise to another (only live part, exluding cid, ver, and all data not relevant to logic).
    Concepts must be exactly the same runtime type.
    Parameters:
        cld = caldron
        fromCid = source premise
        toCid = destination premise
*/
@(17, StatCallType.p0Calp1Cidp2Cid)
void copyPremise(Caldron cld, Cid fromCid, Cid toCid) {
    auto from = scast!Premise(cld[fromCid]);
    auto to = scast!Premise(cld[toCid]);
    from.copy(to);      // the copy() will check for the type identity
}