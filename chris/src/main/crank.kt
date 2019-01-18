
import cpt.logSomeFreeClids
import crank.actualizeCrankedConceptsInDb
import crank.loadAndCrankDynamicConcepts
import crank.logSomeFreeDynamicCids
import libmain.SpiritMap
import libmain._dm_
import stat.logSomeFreeStaticCids

fun main(args: Array<String>) {
    val sm = SpiritMap(_dm_)

    loadAndCrankDynamicConcepts(sm)
    actualizeCrankedConceptsInDb(sm)

    logSomeFreeClids()
    logSomeFreeStaticCids()
    logSomeFreeDynamicCids()
}