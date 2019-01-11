import cpt.logSomeFreeClids
import crank.loadAndCrankDynamicConcepts
import crank.logSomeFreeDynamicCids
import stat.loadStaticConcepts
import stat.logSomeFreeStaticCids

fun main(args: Array<String>) {
    loadStaticConcepts()
    loadAndCrankDynamicConcepts()

    logSomeFreeClids()
    logSomeFreeStaticCids()
    logSomeFreeDynamicCids()
}