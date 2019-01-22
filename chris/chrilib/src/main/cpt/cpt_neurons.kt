package cpt

import atn.Branch
import basemain.Cid
import cpt.abs.*
import libmain.namedCid
import java.util.*

/**
 *      Applies its acts stemCid and brans unconditionally, i.e. without consulting any premises.
 */
open class SpActionNeuron(cid: Cid): SpiritNeuron(cid) {
    override fun liveFactory(): ActionNeuron {
        return ActionNeuron(this)
    }

    /**
     *      Load the action neuron with the acts, brans and stem.
     */
    fun load(acts: Array<out SpiritAction>? = null, brans: Array<SpBreed>? = null, stem: SpiritNeuron? = null): SpActionNeuron {
        assert(_effects == null) {"${namedCid(cid)}: load() must work only once."}
        val actCids = if(acts != null) IntArray(acts.size) { acts[it].cid } else null
        val branCids = if(brans != null) IntArray(brans.size, { brans[it].cid }) else null
        super.addEffect(Float.POSITIVE_INFINITY, actCids, branCids, stem?.cid?: 0)

        return this
    }

    /**
     *      Assign/reassign the stem.
     *  @param newStem
     */
    fun stem(newStem: SpiritNeuron) {
        assert(_effects != null && _effects!!.size == 1) {"This neuron action must be loaded by the time."}
        _effects!![0].stemCid = newStem.cid
    }

    init {
        disableCutoff()
    }
}

/** Live. */
open class ActionNeuron(spActionNeuron: SpActionNeuron): Neuron(spActionNeuron) {

    override fun calculateActivation(br: Branch): Float {
        return 1f
    }

    override fun normalization() = ActivationIfc.NormalizationType.NONE
}

/**
 *      An action neuron, used for initial setting up the branCids.
 */
class SpSeed(cid: Cid): SpActionNeuron(cid) {
    override fun liveFactory(): Seed {
        return Seed(this)
    }
}

/** Live. */
class Seed(spSeed: SpSeed): ActionNeuron(spSeed)

class SpWeightNeuron(cid: Cid): SpiritNeuron(cid) {
    override fun liveFactory(): WeightNeuron {
        return WeightNeuron(this)
    }
}

class WeightNeuron(spWeightNeuron: SpWeightNeuron): Neuron(spWeightNeuron) {

    override fun calculateActivation(br: Branch): Float {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun normalization() = ActivationIfc.NormalizationType.ESQUASH
}

/**
 *      Premises comply boolean logic "And" (with respect to negations).
 */
class SpAndNeuron(cid: Cid): SpiritLogicalNeuron(cid) {
    override fun liveFactory(): AndNeuron {
        return AndNeuron(this)
    }
}

/** Live. */
class AndNeuron(spAndNeuron: SpAndNeuron): LogicalNeuron(spAndNeuron) {

    /**
            Calculate activation based on premises.
        Returns: activation value. Activation is 1 if all premises are active, else it is -1. If the list of premises is empty,
                activation is -1.
    */
    override fun calculateActivation(br: Branch): Float {

        val premises = (sp as SpiritLogicalNeuron).premises
        if (premises != null && !premises.isEmpty())
            for(prem in premises) {
                val premCpt = br[prem.premCid] as ActivationIfc
                if(premCpt.activation <= 0 && !prem.negated || premCpt.activation > 0 && prem.negated) {
                    anactivate()
                    return activation
                }
            }
        else {
            anactivate()
            return activation
        }

        activate()
        return activation
    }
}

/**
 *      Neuron, whose premises are checked one after another (in respect of negations) until an active one found. Then
 *  corresponding effect is chosen. The "corresponding" exactly means, that the first premise has activation 1, the second
 *  2 and so on. The matching effects has upper bounds 1, 2... respectively.
 *
 *      The cutoff is taken care for, meaning it would take effect if no active premises were found. Cutoff can be disabled
 *  by putting null as the first premise on the conceptual level or by calling disableCutoff() in code.
 */
class SpPickNeuron(cid: Cid): SpiritLogicalNeuron(cid) {
    override fun liveFactory(): PickNeuron {
        return PickNeuron(this)
    }

    /**
     *      Add premise and corresponding effect.
     *  @param prem premise and its possible negation. null is possible. It is not adding any premises, but replaces
     *              the cutoff with a real effect for the case when no premises match.
     *  @param actCids array of cids of actions
     *  @param branCids array of cids of breeds
     *  @param stemCid cid of new stem
     */
    fun addPremEff(prem: Prem?, actCids: IntArray? = null, branCids: IntArray? = null, stemCid: Cid = 0) {

        val effSize = _effects?.size?:0
        if      // not effects yet and premise is null
                (effSize == 0 && prem == null)
        {   // no: disable cutoff and accept 0-th effect instead
            disableCutoff()
            _effects = Array<Effect>(1) { Effect(upperBound = 0f, actCids = actCids, branCids = branCids, stemCid = stemCid) }
            return
        }

        assert(prem != null) {"Premise cannot be null here."}
        if(_premises == null)
            _premises = Array<Prem>(1){ prem!! }
        else {
            _premises = Arrays.copyOf(_premises, _premises!!.size + 1)
            _premises!![_premises!!.lastIndex] = prem!!
        }

        if(effSize == 0)
            _effects = Array<Effect>(1) { Effect(upperBound = 1f, actCids = actCids, branCids = branCids, stemCid = stemCid) }
        else {
            val upperBound = _effects!![effSize - 1].upperBound + 1f
            _effects = Arrays.copyOf(_effects, effSize + 1)
            _effects!![effSize] = Effect(upperBound = upperBound, actCids = actCids, branCids = branCids, stemCid = stemCid)
        }
    }

    /**
     *      Adapter for addPremEff()
     */
    fun add(premoid: Any?, acts: Array<SpiritAction>? = null, brans: Array<SpBreed>? = null, stem: SpiritNeuron? = null): SpPickNeuron {
        val prem = when(premoid) {
            null -> null
            is NegatedPremise -> Prem(premoid.spiritPremise.cid, negated = true)
            is SpiritPremise -> Prem(premoid.cid, negated = false)
            else -> throw IllegalStateException("Premoid must be either SpiritPremise or NegatedPremise, but it is ${premoid::class}")
        }
        val actCids = if(acts == null) null else IntArray(acts.size){ acts[it].cid }
        val branCids = if(brans == null) null else IntArray(brans.size){ brans[it].cid }
        val stemCid = stem?.cid?:0

        addPremEff(prem, actCids, branCids, stemCid)

        return this
    }
}

class PickNeuron(spPickNeuron: SpPickNeuron): LogicalNeuron(spPickNeuron) {

    /**
     *      Calculate activation and return activation value.
     *  Activation is equal the matching (respecting negations) premise number: first 1, second 2 and so on. If no premise
     *  matched or the premise array is empty, then -1.
     */
    override fun calculateActivation(br: Branch): Float {
        val premises = (sp as SpiritLogicalNeuron).premises
        if(premises == null) {
            anactivate()
            return activation
        }

        var actvn = 0f
        for(prem in premises) {
            actvn += 1f
            val premCpt = (br[prem.premCid] as ActivationIfc)
            if(premCpt.activation > 0 && !prem.negated || premCpt.activation <= 0 && prem.negated) {
                activation = actvn
                return actvn
            }
        }

        anactivate()
        return activation
    }

}
