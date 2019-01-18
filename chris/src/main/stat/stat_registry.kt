package stat

import basemain.GDEBUG_LV
import basemain.logit
import cpt.abs.SpStaticConcept
import libmain._nm_
import libmain._sm_
import stat.word_processing.puiSt


/**
 *      List of classes, that contain static concepts
 */
private val statModules = listOf(
    cmnSt,      // common
    mnSt,       // main
    puiSt       // parse user input
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
            .filter { it != null && SpStaticConcept::class.isInstance(it) } as List<SpStaticConcept>

        for(statFunc in statFuncs) {

            // Put the static concept into the spirit map
            _sm_.add(statFunc)

            // May be fill in the name map
            if(GDEBUG_LV >= 0) _nm_!![statFunc.cid] = this::class.simpleName + "." + statFunc::class.simpleName
        }
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
    s.append("Static:  ")
    for(cid in _sm_.generateListOfStaticCids(12)){
        s.append("%,d ".format(cid).replace(",", "_"))
    }
    logit(s.toString())
}