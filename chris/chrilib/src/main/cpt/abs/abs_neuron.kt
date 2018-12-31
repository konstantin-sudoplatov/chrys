package cpt.abs

import atn.Branch
import basemain.Cid
import cpt.ActivationIfc
import cpt.SpBreed
import java.util.*

/**
        Base for all neurons.
*/
abstract class SpiritNeuron(cid: Cid): SpiritDynamicConcept(cid) {

    /** If activation <= cutoff then result of the selectEffects() function is automatically Effect(cutoff, 0, null, null),
        which means action stopAndWait and no brans. This allows to get rid of the first span from -infinity to 0, which
        is most often used as an antiactive span. */
    var cutoff: Float = 0f
        set(value) {
            assert( effects_ == null || effects_!!.isEmpty() || value.isNaN() || value < effects_!![0].upperBound) {
                "cutoff = $value must be less then upper bound of the first span ${effects_!![0].upperBound}"
            }
            field = value
        }

    val isCutoff: Boolean
        get() = !cutoff.isNaN()

    override fun toString(): String {
        var s = super.toString()
        s += "\n    effects_ = $effects_"
        if(effects_ != null)
            for((i, eff) in effects_!!.withIndex())
                s += "\nEffect[$i] = $eff".replace("\n", "\n    ")

        return s
    }

    /**
     *      Set cutoff to Float.NaN.
     */
    fun disableCutoff() { cutoff = Float.NaN }

    /**
                Get effects corresponding to given activation.
        Parameters:
            activation = activation value
        Returns:
            the Effect struct as the Voldemort value.
    */
    fun selectEffect(activation: Float): Effect {
        if      // is activation falling into the cutoff span?
                (activation <= cutoff)
            // return an empty effect
            return Effect(cutoff)

        if(effects_ != null)
            for(eff in effects_!!)
                if      // is activation fitting a span?
                        (activation <= eff.upperBound)
                    return eff

        // Activation not found? Return the last empty span
        return Effect(Float.POSITIVE_INFINITY)
    }

    /** Adapter. */
    fun addEff(upBound: Float, acts: Array<out SpiritAction>? = null, brans: Array<SpBreed>? = null, stem: SpiritNeuron? = null) {
        val actCids = if(acts != null) IntArray(acts.size) { acts[it].cid } else null
        val branCids = if(brans != null) IntArray(brans.size, { brans[it].cid }) else null
        addEff_(upBound, actCids, branCids, stem)
    }

    /**
     *      Load a group of effects.
     */
    fun loadEffs(vararg efs: Eft) {
        for(ef in efs)
            addEff(ef.upBound, ef.acts, ef.brans, ef.stem)
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%--- private data ---%%%---%%%---%%%---%%%---%%%---%%%

    private var effects_: Array<Effect>? = null

    //---%%%---%%%---%%%---%%%--- private funcs ---%%%---%%%---%%%---%%%---%%%---%%%

    /**
     *      Add acts and brans for a new span of the activation values. If cutoff is enabled, which is the default,
     *  the the number of spans is bigger by one, than defined in the effects array. The first dummy span of
     *  Effect(cutoff, null, null) ocupies the region [-float.infinity, cutoff].
     *
     *      You can disable cutoff in three ways:
     *
     *      1. the function disableCutoff()
     *      2. the property neuron.cutoff = float.nan
     *      3. defining the first span with the upBound <= cutoff.
     *
     *  @param upperBound The upper boundary of the span, including.
     *  @param actions Array of cids of acts. null or [] - no acts for this span.
     *  @param stem Neuron of new stemCid. null - stay on the current stemCid.
     *  @param branches Array of cids of brans. null or [] - no new brans for this span.
     */
    private fun addEff_(upperBound: Float, actions: IntArray? = null, branches: IntArray? = null, stem: SpiritNeuron? = null) {

        val eff = Effect(upperBound, actions, branches, stem?.cid?: 0)

        // May be disable cutoff
        if(effects_ == null || effects_!!.isEmpty()) {
            if      // is the upper bound of the first span overlaps cutoff?
                    (eff.upperBound <= cutoff)
                disableCutoff()
        }
        else {
            assert(eff.upperBound > effects_!![effects_!!.lastIndex].upperBound)
                { "Upper bound ${eff.upperBound} must be greater than the upper bound of the last span " +
                "${effects_!![effects_!!.lastIndex].upperBound}"}
        }

        // Add new effect to the effects_ array
        if(effects_ == null)
            effects_ = Array<Effect>(1){ eff }
        else {
            effects_ = Arrays.copyOf(effects_, effects_!!.size+1)
            effects_!![effects_!!.lastIndex] = eff
        }
    }
}

/** Live. */
abstract class Neuron(spNeuron: SpiritNeuron): DynamicConcept(spNeuron), ActivationIfc {

    override var activation = -1f   // anactivated by default


    /**
                Calculate activation based on premises or lots.
        @return activation value
    */
    abstract fun calculateActivation(br: Branch): Float

    fun calculateActivationAndSelectEffect(br: Branch): Effect {
        return (sp as SpiritNeuron).selectEffect(calculateActivation(br))
    }
}

abstract class SpiritLogicalNeuron(cid: Cid): SpiritNeuron(cid) {

    val premises: Array<Prem>?
        get() = _premises

    override fun toString(): String {
        var s = super.toString()
        s += "\n    _premises = $_premises"
        if(_premises != null)
            for((i, prem) in _premises!!.withIndex())
                s += "\n_premises[$i] = $prem".replace("\n", "\n    ")

        return s
    }

    /**
     *      Load a number of premises' cids along with their possible negations.
     *  @param premoids objects of premises possibly wrapped int the NegatedPremise objects to show that in the _premises
     *          array they should appear with the negation flag, i.e. it's their negatives that will be considered when
     *          calculating the activation value of the neuron. The wrapping is caused by prefixing the premise object with
     *          the ! sign.
     */
    fun loadPrems(vararg premoids: Any): SpiritLogicalNeuron {

        if      // nothing to add?
                (premoids.isEmpty())
        {   //no: clear premises, return
            _premises = null
            return this
        }

        // Form list of Prem objects based on the premoids array
        val premList = mutableListOf<Prem>()
        for(ind in premoids.indices) {
            assert(premoids[ind] is NegatedPremise || premoids[ind] is SpiritPremise)
            val premoid = premoids[ind]
            when(premoid) {
                is SpiritPremise -> {
                    premList.add(Prem(premoid.cid, false))
                }

                is NegatedPremise -> {
                    premList.add(Prem(premoid.spiritPremise.cid, true))
                }
            }
        }

        // Move the Prem objects to the _premises array
        _premises = Array<Prem>(premoids.size) { premList[it]}

        return this
    }

    protected var _premises: Array<Prem>? = null
}

abstract class LogicalNeuron(spLogicalNeuron: SpiritLogicalNeuron): Neuron(spLogicalNeuron) {

    override fun normalization() = ActivationIfc.NormalizationType.BIN
}

/**
 *      Defines a span of activation values ]previous span's upper boundary, the new upper boundary].
 *  For the first span it is [Float.NEGATIVE_INFINITY, upBound]. If the last span is unspecified, it's
 *  ]upBound, Float.POSITIVE_INFINITY].
 *
 *  For typealias Cid=Int used the IntArray arrays. If Cid changes for Long, for example, it also must be changed.
 */
class Effect(
    val upperBound: Float,              // the upper boundary of the span, including
    val actions: IntArray? = null,      // Array of cids of acts. null or [] - no acts for this span
    val branches: IntArray? = null,     // Array of cids of brans. null or [] - no new brans for this span
    val stemCid: Cid = 0                // 0 - stay on the current stemCid
) {
    override fun toString(): String {
        var s = this::class.qualifiedName?: ""
        s += "\n    upBound = $upperBound"
        s += "\n    acts = $actions"
        s += "\n    stemCid = $stemCid"
        s += "\n    brans = $branches"

        return s
    }
}

/**
 *      Defines an element of the premises array of a logical neuron.
 */
class Prem(
    val premCid: Cid,       // premise concept cid
    val negated: Boolean    // possibly the negation of the premise is to be considered
) {
    override fun toString(): String {
        var s = this::class.qualifiedName?: ""
        s += "\n    premCid = $premCid"
        s += "\n    negated = $negated"

        return s
    }
}

/**
 *      Used as a vararg parameters for the loadEffs() function
 */
class Eft(
    val upBound: Float,
    val acts: Array<out SpiritAction>? = null,
    val brans: Array<SpBreed>? = null,
    val stem: SpiritNeuron? = null
)