import basemain.Cid

/**
 *      Some concepts must have known to code base cids, but should not participate in the cranking since are changed
 *  dynamically.
 */
enum class FCid(val cid: Cid) {

    // Dictionary of russian word forms
    russianWordformMap(-610_616_222)
}   //  839_298_106 -758_057_277 -1_627_508_299 -1_530_438_504 68_415_566 403_449_370

/**
 *      Put fixed cids from the enum above into the name map.
 */
fun loadFixedCidsIntoNameMap(nm: HashMap<Cid, String>) {
    for(fCid in FCid.values()) {
        assert(fCid.cid !in nm) {"${fCid}(${fCid.cid}): this cid is already present in the name map."}
        nm[fCid.cid] = fCid.name
    }
}