package stat

import atn.Branch
import basemain.Cid
import chribase_thread.CuteThread
import cpt.*
import libmain.BranchSendsUserItsBrad
import libmain.CirclePromptsUserMsg
import libmain.CircleTellsUserMsg

/** Main stat container */
object mnSt: StatModule() {

/**
 *      Send branch address to the user.
 */
object branchSendsUserItsBrad: FCid(19_223) {
    /**
     *  @param br current branch
     *  @param userThread_premCid Cid of the premise, containing the user thread object reference.
    */
    @Suppress
    override fun func(br: Branch, userThread_premCid: Cid) {
        val userThread = (br[userThread_premCid] as CuteThreadPrem).thread as CuteThread
        userThread.putInQueue(BranchSendsUserItsBrad(br.ownBrad.clone()))
    }
}

/**
 *      Send line of text to user.
 */
object circleTellsUser: F2Cid(93_270) {
    /**
     *  @param br current branch
     *  @param userThread_premCid Cid of the premise, containing the user thread object reference.
     *  @param load Cid of the string premise, containing the text
    */
    @Suppress
    override fun func(br: Branch, userThread_premCid: Cid, load: Cid) {
        val userThread = (br[userThread_premCid] as CuteThreadPrem).thread as CuteThread
        val text = (br[load] as StringPrem).text
        userThread.putInQueue(CircleTellsUserMsg(text))
    }
}

object circlePromtsUser: FCid(27_383) {
    override fun func(br: Branch,  userThread_premCid: Cid) {
        val userThread = (br[userThread_premCid] as CuteThreadPrem).thread as CuteThread
        userThread.putInQueue(CirclePromptsUserMsg())
    }
}

/**
 *      Take line from the string queue and give it string premise. Take care of the activation status of both concepts.
 */
object extractLineFromStringQueue: F2Cid(24_107) {
    override fun func(br: Branch, queuePrem: Cid, stringPrem: Cid) {
        val queuePrem = br[queuePrem] as StringQueuePrem
        val queue = queuePrem.queue
        assert(!queue.isEmpty()) {"If we got here, the queue must not be empty."}
        val strPrem = br[stringPrem] as StringPrem
        strPrem.text = queue.poll()
        strPrem.activate()
        if(queue.isEmpty()) queuePrem.anactivate()
    }
}
}   //      40_288 26_161 92_115 86_381 92_962 42_567 37_654 56_252 52_663 46_910
