package cpt.abs

import basemain.Cid
import cpt.ActivationIfc


/**
 *          Base for all premises. Premises are mostly live entities since their main activity is on the branch local
 *   level. The activation interface they implement works only in the live realm never penetrating the spirit level. They
 *   can occasionally keep their data in the spirit part though, like the Breed concept keeps its corresponding Seed cid there.
 *
 *  @param cid
 */
abstract class SpiritPremise(cid: Cid): SpiritDynamicConcept(cid) {

    /**
     *      An overload of the "!" operator that returns this object wrapped in the NegatedPremise object.
     *  It is used in the SpiritLogicalNeuron.addPrems() func to distinguish between the premise and its negation.
     */
    operator fun not() = NegatedPremise(this)
}

/**
 *          Base for live premises.
 */
abstract class Premise(spiritDynamicConcept: SpiritDynamicConcept): DynamicConcept(spiritDynamicConcept), ActivationIfc {
    override var activation: Float = -1f

    override fun normalization() = ActivationIfc.NormalizationType.BIN


    /**
     *      Copy live concepts, without copying spirit part.
     *  @param dest Live concept to copyLive data to
     */
    open fun copyLive(dest: Premise) {
        dest.activation = activation
    }

    override fun toString(): String {
        var s = super.toString()
        s += "\n    activation = $activation"
        return s
    }
}

/**
 *          If SpiritPremise is wrapped into this class, it means that the premise's activation should be negated at assessing.
 *      Used in the SpiritLogicalNeuron.loadPrems()
 */
class NegatedPremise(val spiritPremise: SpiritPremise)