package stat

import basemain.logit
import cpt.SpiritStaticConcept
import libmain.*


/**
 *      List of classes, that contain static concepts
 */
private val statModules = listOf(
    mainStat
)

/**
 *          Base for the stat modules.
 */
open class StatModule {

    /**
     *      Load concepts declared in this module into the spirit map
     */
    fun loadSpiritMap() {

        // Extract list of stat functors
        @Suppress("UNCHECKED_CAST")
        val statFuncs = this::class.nestedClasses.map { it.objectInstance }
            .filter { it != null && StaticConceptFunctor::class.isInstance(it) } as List<StaticConceptFunctor>

        for(statFunc in statFuncs)
            _sm_.add(SpiritStaticConcept(statFunc))
    }
}

/**
 *      Load into the spirit map all static concepts
 */
fun loadStaticConcepts() {

    // Load
    for(statModule in statModules)
        statModule.loadSpiritMap()
}

fun logSomeFreeStaticCids() {

    val s = StringBuilder()
    s.append("\nStatic:  ")
    for(cid in _sm_.generateListOfStaticCids(12)){
        s.append("%,d ".format(cid).replace(",", "_"))
    }

    logit(s.toString())
}