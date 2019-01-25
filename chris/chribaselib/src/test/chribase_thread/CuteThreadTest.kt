package chribase_thread

import basemain.DEFAULT_MAX_THREAD_QUEUE
import org.junit.Test

class CuteThreadTest {

    /**
     * Create a thread queue with overriden run() method. Put basemain.DEFAULT_MAX_THREAD_QUEUE messages into it, the thread that puts
     * them is blocked. Check the number of messages in the queue. Start taking out the messages, the putting thread is
     * released. Check that in the end 2*basemain.DEFAULT_MAX_THREAD_QUEUE messages have come through the queue.
     */
    @Test fun fillFreeQueueWithCongestion () {
        val thread = object: CuteThread(0, DEFAULT_MAX_THREAD_QUEUE, "primary") {

            override fun run() {
                Thread.sleep(1000)
                var i = 0
                while(true) {
                    _getBlocking()
                    i++
                    //println("got = $i")
                }
            }

            override fun _messageProc(msg: MessageMsg?): Boolean {
                return true
            }
        }.also { it.start() }


        var totalMessages = 0
        object : Thread() {
            override fun run() {
                for (i in 1..2*DEFAULT_MAX_THREAD_QUEUE) {
                    thread.putInQueue(MessageMsg())
                    totalMessages++
                    //println("put = $i")
                }
            }
        }.start()

        Thread.sleep(500)
        assert(totalMessages == DEFAULT_MAX_THREAD_QUEUE)

        Thread.sleep(2000)
        println("totalMessages $totalMessages")
        assert(totalMessages == 2* DEFAULT_MAX_THREAD_QUEUE)
    }

    /**
     * Test if timeout on the _getBlocking works
     */
    @Test fun waitWithTimeout() {
        var exitOnTimeout = false
        object : CuteThread(500, 0, "secondary") {
            override fun run() {
                _getBlocking()
                exitOnTimeout = true
                println("Finish on timeout")
            }

            override fun _messageProc(msg: MessageMsg?): Boolean {
                return true
            }
        }.also { it.start() }

        Thread.sleep(1000)
        assert(exitOnTimeout)
    }
}