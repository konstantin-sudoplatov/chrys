package crank

import basemain.acts
import cpt.SpA_Cid
import cpt.SpActionNeuron
import cpt.abs.SpiritDynamicConcept
import cpt.abs.SpiritNeuron
import crank.mnCr.cmn.debugOff_act
import crank.mnCr.cmn.debugOn_act
import libmain._sm_
import stat.cmnSt

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
 *          Compact several log actCids into action neuron.
 * @param log_act log actCids to pack into the neuron.
 */
fun log_acnr(vararg log_act: SpA_Cid): SpActionNeuron {
    val acnr = SpActionNeuron(0)
    _sm_.add(acnr)
    acnr.load(acts(*log_act))
    return acnr
}

/**
 *      Log action.
 *  @param spCpt concept to log
 */
fun log1(spCpt: SpiritDynamicConcept) = mnCr.cmn.log1_act.load(cmnSt.logConcept, spCpt)

/**
 *      Log action.
 *  @param spCpt concept to log
 */
fun log2(spCpt: SpiritDynamicConcept) = mnCr.cmn.log2_act.load(cmnSt.logConcept, spCpt)

/**
 *      Log action.
 *  @param spCpt concept to log
 */
fun log3(spCpt: SpiritDynamicConcept) = mnCr.cmn.log3_act.load(cmnSt.logConcept, spCpt)

/**
 *      Log neuron.
 *  @param spCpt concept to log
 */
fun logn1(spCpt: SpiritDynamicConcept) = mnCr.cmn.log1_actn.load(acts(log1(spCpt)))

/**
 *      Log neuron.
 *  @param spCpt concept to log
 */
fun logn2(spCpt: SpiritDynamicConcept) = mnCr.cmn.log2_actn.load(acts(log2(spCpt)))

/**
 *      Log neuron.
 *  @param spCpt concept to log
 */
fun logn3(spCpt: SpiritDynamicConcept) = mnCr.cmn.log3_actn.load(acts(log3(spCpt)))
