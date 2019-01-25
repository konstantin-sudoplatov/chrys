package libmain

import org.junit.Test

class DbManagerTest {

    @Test
    fun getConcept() {
        val dbm = DbManager(_conf_)

        val thread1 = object: Thread() {
            override fun run() {
                for(i in 0..10000) {
                    dbm.getConcept(12345678, 12345)
                }
                println("thread 1 finished")
            }
        }.also { it.start() }

        val thread2 = object: Thread() {
            override fun run() {
                for(i in 0..10000) {
                    dbm.getConcept(12345678, 12345)
                }
                println("thread 2 finished")
            }
        }.also { it.start() }

        thread1.join()
        thread2.join()
    }
}