import crank.loadAndCrankDynamicConcepts
import crank.logSomeFreeDynamicCids
import libmain._sm_
import stat.loadStaticConcepts
import stat.logSomeFreeStaticCids

fun main(args: Array<String>) {
    loadStaticConcepts()
    println(_sm_.size)
    loadAndCrankDynamicConcepts()
    println(_sm_.size)

    logSomeFreeStaticCids()
    logSomeFreeDynamicCids()
}