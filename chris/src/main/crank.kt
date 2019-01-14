
import cpt.logSomeFreeClids
import crank.actualizeCrankedConceptsInDb
import crank.loadAndCrankDynamicConcepts
import crank.logSomeFreeDynamicCids
import stat.logSomeFreeStaticCids

fun main(args: Array<String>) {
    //loadStaticConcepts()

    loadAndCrankDynamicConcepts()
    actualizeCrankedConceptsInDb()

    logSomeFreeClids()
    logSomeFreeStaticCids()
    logSomeFreeDynamicCids()
}