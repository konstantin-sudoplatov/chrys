import basemain.Cid

enum class FCid(val cid: Cid) {
    // Dictionary of russian word forms
    russianWordFormMap(-610_616_222)
}   //  839_298_106 -758_057_277 -1_627_508_299 -1_530_438_504 68_415_566 403_449_370

fun loadFixedCidsIntoNameMap(nm: HashMap<Cid, String>) {
    for(fCid in FCid.values()) {
        assert(fCid.cid !in nm) {"${fCid}(${fCid.cid}): this cid is already present in the name map."}
        nm[fCid.cid] = fCid.name
    }
}