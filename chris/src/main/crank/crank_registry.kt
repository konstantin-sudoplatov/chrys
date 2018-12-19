package crank

import basemain.logit
import libmain._sm_
import stat.statMain

/**
 *      List of classes, that contain static concepts
 */
private val statModules = listOf(
    statMain
)

/**
 *      List of classes, that contain crank definitions (enums and functions)
 */
private val crankModues = listOf(
    crankMain
)

/**
 *      Load into the spirit map all static concepts
 */
fun loadStaticConcepts() {

    // Load
    for(statModule in statModules)
        statModule.loadSpiritMap()
}

/**
 *      Load into the spirit map and crank all dynamic concepts
 */
fun loadAndCrankDynamicConcepts() {

    // Load
    for(crankModule in crankModues)
        crankModule.loadSpiritMap()

    // Crank
    for(crankModule in crankModues)
        crankModule.doCranking()
}

fun logSomeFreeCids() {

    val s = StringBuilder()
    s.append("Dynamic: ")
    for(cid in _sm_.generateListOfDynamicCids(7)){
        s.append("%,d ".format(cid).replace(",", "_"))
    }
    s.append("\nStatic:  ")
    for(cid in _sm_.generateListOfStaticCids(7)){
        s.append("%,d ".format(cid).replace(",", "_"))
    }

    logit(s.toString())
}