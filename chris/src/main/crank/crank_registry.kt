package crank

import basemain.logit
import cpt.abs.SpiritDynamicConcept
import libmain.CrankModule
import libmain._dm_
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

/**
 *      After concepts are loaded into the spirit map and cranked, this function compares them to the database and inserts/updates
 *  them if they are absent or differ.
 *  Prerequisites: all dynamic concepts must by loaded into _sm_ and cranked.
 */
fun actualizeCrankedConceptsInDb(){

    var inserted: Int = 0
    var updated: Int = 0
    for((cid, cpt) in _sm_.map) {
        assert(cid == cpt.cid) { "Cid $cid is not equal to _sm_[cid].cid"}

        // Make sure all dynamic concept in the spirit map are present in the database and equal.
        if( cpt is SpiritDynamicConcept) {
            val dbCpt = _dm_.getConcept(cid)
            if(cpt != dbCpt)
                when {
                    dbCpt == null -> {      // concept isn't found in the db? insert
                        _dm_.insertConcept(cpt)
                        inserted++
                    }
                    else -> {      // update in the db
                        _dm_.updateConcept(cpt)
                        updated++
                    }
                }
        }
    }

    logit("Added to database: $inserted, updated: $updated")
}

fun logSomeFreeDynamicCids() {

    val s = StringBuilder()
    s.append("Dynamic: ")
    for(cid in _sm_.generateListOfDynamicCids(7)){
        s.append("%,d ".format(cid).replace(",", "_"))
    }
    logit(s.toString())
}
