package base_thread

import base.MAX_THREAD_QUEUE_SIZE
import org.junit.Test

class ThreadQueueTest {

    /**
     * Create a thread queue with overriden run() method. Put MAX_THREAD_QUEUE_SIZE messages into it, the thread that puts
     * them is blocked. Check the number of messages in the queue. Start taking out the messages, the putting thread is
     * released. Check that in the end 2*MAX_THREAD_QUEUE_SIZE messages have come through the queue.
     */
    @Test fun fillFreeQueueWithCongestion () {
        val thread = object : ThreadQueue(0) {
            override fun run() {
                Thread.sleep(1000)
                var i = 0
                while(true) {
                    _getBlocking()
                    i++
                    //println("got = $i")
                }
            }
        }.also { it.start() }


        var totalMessages = 0
        object : Thread() {
            override fun run() {
                for (i in 1..2*MAX_THREAD_QUEUE_SIZE) {
                    thread.putInQueue(MessageMsg())
                    totalMessages++
                    //println("put = $i")
                }
            }
        }.start()

        Thread.sleep(500)
        assert(totalMessages == MAX_THREAD_QUEUE_SIZE)

        Thread.sleep(2000)
        println("totalMessages $totalMessages")
        assert(totalMessages == 2* MAX_THREAD_QUEUE_SIZE)
    }

    /**
     * Test if timeout on the _getBlocking works
     */
    @Test fun waitWithTimeout() {
        var exitOnTimeout = false
        object : ThreadQueue(500) {
            override fun run() {
                _getBlocking()
                exitOnTimeout = true
                println("Finish on timeout")
            }
        }.also { it.start() }

        Thread.sleep(1000)
        assert(exitOnTimeout)
    }
}