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

/**
 *      Render array of cids to a string.
 *  @param arrName array name
 *  @param arr array
 *  @param lim limit of elements of array to print
 */
fun arrayOfCidsNamed(arrName: String, arr: IntArray?, lim: Int = 5): String {
    var s: String
    if(arr == null) {
        s = "$arrName = null"
        return s
    }
    else
        s = "$arrName(size = ${arr.size}) = ["

    for(cid in arr.take(lim)) {
        s += "\n    ${cidNamed(cid)}"
    }
    s += "\n]"

    return s
}