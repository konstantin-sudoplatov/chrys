package cpt.abs

import basemain.Cid
import cpt.ActivationIfc
import java.lang.IllegalArgumentException


/**
 *          Base for all premises. Premises are mostly live entities since their main activity is on the branch local
 *   level. The activation interface they implement works only in the live realm never penetrating the spirit level. They
 *   can occasionally keep their data in the spirit part though, like the Breed concept keeps its corresponding Seed cid there.
 *
 *  @param cid
 */
abstract class SpiritPremise(cid: Cid): SpiritDynamicConcept(cid)

/**
 *          Base for live premises.
 */
abstract class Premise(spiritDynamicConcept: SpiritDynamicConcept): DynamicConcept(spiritDynamicConcept), ActivationIfc {
    override var activation: Float = -1f

    override fun normalization() = ActivationIfc.NormalizationType.BIN

    override fun toString(): String {
        var s = super.toString()
        s += "\n    activation = $activation"
        return s
    }

    /**
            Copy meaningful fields of this concept to the destination. Both concepts must be exactly the same runtime type.
        Such fields as cid, ver or the usage statistics, for example, are left intact.
        @param destination = concept to copy data to
        Returns: changed destination concept
    */
    fun copy(destination: Premise) {
        assert(this::class == destination::class)
        destination.activation = activation
    }
}