package base_thread

import chribaselib.MAX_THREAD_QUEUE_SIZE
import org.junit.Test

class ThreadQueueTest {

    @Test
    fun put_in_queue() {
        val thread = object : ThreadQueue() {
            override fun run() {
                Thread.sleep(1000)
                var i = 0;
                while(true) {
                    get_blocking()
                    i++
                    println("got = $i")
                }
                println("thread1 is finished")
            }
        }.also { it.start() }

        object : Thread() {
            override fun run() {
                for (i in 1..2*MAX_THREAD_QUEUE_SIZE) {
                    thread.put_in_queue(BaseMessage())
                    println("put = $i")
                }
            }
        }.start()

        Thread.sleep(3000)
    }

    @Test
    fun get_blocking() {
    }
}