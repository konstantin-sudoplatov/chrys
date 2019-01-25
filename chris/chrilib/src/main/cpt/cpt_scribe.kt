package cpt

import chribase_thread.CuteThread
import chribase_thread.MessageMsg

/**
 *      The data keeper. The main job is to write to the database - concepts, dictionaries, versions. May be clean out
 *  rubbish data. It is the only thread that changes the database.
 *
 *  May do some other small things like generating a stock of free cids, to be at hand, for the spirit map.
 */
class Scribe: CuteThread() {
    override fun _messageProc(msg: MessageMsg?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}