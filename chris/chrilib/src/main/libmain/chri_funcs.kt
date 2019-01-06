package libmain

import basemain.Cid

/**
 *          Give cid a name if it exists in the name map.
 *  @param cid Cid
 */
fun cidNamed(cid: Cid): String {
    val s = "%,d".format(cid).replace(",", "_")
    if(_nm_ != null && cid in _nm_)
        return "$s (${_nm_[cid]})"
    else
        return "$s (noname)"
}

/**
 *          Give cid a name if it exists in the name map.
 *  @param cid Cid
 */
fun namedCid(cid: Cid): String {
    val s = "%,d".format(cid).replace(",", "_")
    if(_nm_ != null && cid in _nm_)
        return "${_nm_[cid]}($s)"
    else
        return "$s (noname)"
}
