package crank

import basemain.logit
import libmain.CrankModule
import libmain._sm_
import libmain.hCr

/**
 *      List of classes, that contain crank definitions (enums and functions)
 */
private val crankModules = listOf<CrankModule>(
    hCr,
    mnCr
)

/**
 *      Load into the spirit map and crank all dynamic concepts
 */
fun loadAndCrankDynamicConcepts() {

    // Load
    for(crankModule in crankModules)
        crankModule.loadSpiritMap()

    // Crank
    for(crankModule in crankModules)
        crankModule.doCranking()
}

fun logSomeFreeDynamicCids() {

    val s = StringBuilder()
    s.append("Dynamic: ")
    for(cid in _sm_.generateListOfDynamicCids(7)){
        s.append("%,d ".format(cid).replace(",", "_"))
    }

    logit(s.toString())
}
