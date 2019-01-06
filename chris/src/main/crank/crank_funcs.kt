package crank

import basemain.acts
import cpt.SpA_Cid
import cpt.SpActionNeuron
import cpt.abs.SpiritDynamicConcept
import cpt.abs.SpiritNeuron
import crank.mainCrank.common.debugOff_act
import crank.mainCrank.common.debugOn_act
import libmain._sm_
import stat.commonStat

/**
 *          Chain stems. All the stems in chain must be action neurons, only the last one can be a something else.
 *  @param next the next stem.
 */
infix fun SpActionNeuron.then(next: SpiritNeuron): SpActionNeuron {
    this.stem(next);
    return this
}

/**
 *          Request tracing through branch, pod and pod pool.
 */
fun debugOn_actn(): SpActionNeuron {
    val acnr = SpActionNeuron(0)
    _sm_.add(acnr)
    acnr.load(acts(debugOn_act))
    return acnr
}

/**
 *          Reset tracing through branch, pod and pod pool.
 */
fun debugOff_actn(): SpActionNeuron {
    val acnr = SpActionNeuron(0)
    _sm_.add(acnr)
    acnr.load(acts(debugOff_act))
    return acnr
}

/**
 *          Compact several log actions into action neuron.
 * @param log_act log actions to pack into the neuron.
 */
fun log_acnr(vararg log_act: SpA_Cid): SpActionNeuron {
    val acnr = SpActionNeuron(0)
    _sm_.add(acnr)
    acnr.load(acts(*log_act))
    return acnr
}

/**
 *          Create and load an action of logging a specified dynamic concept.
 * @param spCpt concept to log_act
 */
fun log_act(spCpt: SpiritDynamicConcept): SpA_Cid {
    val logAct = SpA_Cid(0)
    _sm_.add(logAct)
    logAct.load(commonStat.logConcept, spCpt)

    return logAct
}
