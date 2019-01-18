package crank

import basemain.Cid
import basemain.logit
import cpt.abs.SpiritDynamicConcept
import crank.word_processing.pulCr
import libmain.*

/**
 *      List of classes, that contain crank definitions (enums and functions)
 */
private val crankModules = listOf<CrankModule>(
    hCr,            // hard cid
    mnCr,           // main
    pulCr           // parse user input
)

/**
 *      Load into the spirit map and crank all spirit dynamic concepts
 *  @param sm spirit map to load
 */
fun loadAndCrankDynamicConcepts(sm: SpiritMap) {

    // Load
    for(crankModule in crankModules)
        crankModule.loadSpiritMap(sm)

    // Crank
    for(crankModule in crankModules)
        crankModule.doCranking()
}

/**
 *      Load name map with the names of concepts.
 *  @param nm name map to load
 */
fun loadNameMap(nm: HashMap<Cid, String>?) {
    for(crankModule in crankModules)
        crankModule.loadNameMap(nm)
}

/**
 *      After concepts are loaded into the spirit map and cranked, this function compares them to the database and inserts/updates
 *  them if they are absent or differ.
 *  @param sm the spirit map
 */
fun actualizeCrankedConceptsInDb(sm: SpiritMap){

    var inserted = 0
    var updated = 0
    for((cid, cpt) in sm.map) {
        assert(cid == cpt.cid) { "Cid $cid is not equal to _sm_[cid].cid"}

        // Make sure all dynamic concept in the spirit map are present in the database and equal.
        if( cpt is SpiritDynamicConcept) {
            val dbCpt = _dm_.getConcept(cid, cpt.ver)
            if(cpt != dbCpt)
                if (dbCpt == null) {      // concept isn't found in the db? insert
                    _dm_.insertConcept(cpt)
                    inserted++
                }
                else {      // update in the db
                    _dm_.updateConcept(cpt)
                    updated++
                }
        }
    }

    logit("Added to database: $inserted, updated: $updated")
}

/**
 *      Generate and log some free cids for dynamic concepts. They are guaranteed to be not used and be unique.
 *  Note: usually the cids are generated in advance and put into the comments for using in the cranks. Moreover, they are not
 *  generated in one go. That means they can be not unique. It is unlikely, but possible. For that we rely on the
 *  checks in the code.
 */
fun logSomeFreeDynamicCids() {

    val s = StringBuilder()
    s.append("Dynamic: ")
    for(cid in _sm_.generateListOfDynamicCids(7)){
        s.append("%,d ".format(cid).replace(",", "_"))
    }
    logit(s.toString())
}
