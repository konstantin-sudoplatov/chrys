package crank

import cpt.SpA_Cid
import cpt.abs.SpiritDynamicConcept
import libmain._sm_
import stat.commonStat

/**
 *          Create and load an action of logging a specified dynamic concept.
 * @param spCpt Concept to log_act
 */
fun log_act(spCpt: SpiritDynamicConcept): SpA_Cid {
    val logAct = SpA_Cid(0)
    _sm_.add(logAct)
    logAct.load(commonStat.logConcept, spCpt)

    return logAct
}
