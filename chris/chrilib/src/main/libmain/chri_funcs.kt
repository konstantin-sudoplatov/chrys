package libmain

import basemain.Cid

/**
 *          Give cid a name if it exists in the name map.
 *  @param cid Cid
 */
fun namedCid(cid: Cid) = if(_nm_ != null && cid in _nm_) "$cid (${_nm_[cid]})" else "$cid (noname)"
