package crank

import cpt.SpA_Cid
import cpt.abs.SpiritDynamicConcept
import stat.commonStat

/**
 *          Extention function for simple loading the common.logCptN_act concept.
 *  @param spCpt Concept to log
 */
fun SpA_Cid.loadlog(spCpt: SpiritDynamicConcept): Unit = load(commonStat.logConcept, spCpt)
