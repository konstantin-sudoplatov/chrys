import crank.loadAndCrankDynamicConcepts
import crank.loadStaticConcepts
import crank.logSomeFreeCids
import libmain._sm_

fun main(args: Array<String>) {
    loadStaticConcepts()
    println(_sm_.size)
    loadAndCrankDynamicConcepts()
    println(_sm_.size)

    logSomeFreeCids()
}