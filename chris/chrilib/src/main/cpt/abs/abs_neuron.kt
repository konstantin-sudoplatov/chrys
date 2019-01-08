package cpt.abs

import atn.Branch
import basemain.Cid
import cpt.ActivationIfc
import cpt.SpBreed
import libmain.arrayOfCidsNamed
import libmain.cidNamed
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
            assert( _effects == null || _effects!!.isEmpty() || value.isNaN() || value < _effects!![0].upperBound) {
                "cutoff = $value must be less then upper bound of the first span ${_effects!![0].upperBound}"
            }
            field = value
        }

    val isCutoff: Boolean
        get() = !cutoff.isNaN()

    override fun toString(): String {
        var s = super.toString()
        if(_effects == null)
            s += "\n    _effects = null"
        else {
            s += "\n    _effects = ["
            for ((i, eff) in _effects!!.withIndex())
                s += "\nEffect[$i] = $eff".replace("\n", "\n        ")
            s += "\n    ]"
        }

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

        if(_effects != null)
            for(eff in _effects!!)
                if      // is activation fitting a span?
                        (activation <= eff.upperBound)
                    return eff

        // Activation not found? Return the last empty span
        return Effect(Float.POSITIVE_INFINITY)
    }

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
     *  @param actCids Array of cids of acts. null or [] - no acts for this span.
     *  @param branCids Array of cids of brans. null or [] - no new brans for this span.
     *  @param stem Neuron of new stemCid. null - stay on the current stemCid.
     */
    open fun addEffect(upperBound: Float, actCids: IntArray? = null, branCids: IntArray? = null, stemCid: Cid = 0) {

        val eff = Effect(upperBound, actCids, branCids, stemCid)

        // May be disable cutoff
        val effSize = _effects?.size?:0
        if(effSize == 0) {
            if      // is the upper bound of the first span overlaps cutoff?
                    (eff.upperBound <= cutoff)
                disableCutoff()
        }
        else {
            assert(eff.upperBound > _effects!![_effects!!.lastIndex].upperBound)
                { "Upper bound ${eff.upperBound} must be greater than the upper bound of the last span " +
                "${_effects!![_effects!!.lastIndex].upperBound}"}
        }

        // Add new effect to the _effects array
        if(effSize == 0)
            _effects = Array<Effect>(1){ eff }
        else {
            _effects = Arrays.copyOf(_effects, _effects!!.size+1)
            _effects!![_effects!!.lastIndex] = eff
        }
    }

    /**
     *      Adapter for SpiritNeuron.addEffect(). It allows using spirit concepts instead of their cids.
     */
    open fun addEff(upBound: Float, acts: Array<out SpiritAction>? = null, brans: Array<SpBreed>? = null,
                            stem: SpiritNeuron? = null): SpiritNeuron
    {
        val actCids = if(acts != null) IntArray(acts.size) { acts[it].cid } else null
        val branCids = if(brans != null) IntArray(brans.size, { brans[it].cid }) else null
        addEffect(upBound, actCids, branCids, stem?.cid?: 0)

        return this
    }

    /**
     *      Set effects array to null.
     */
    fun resetEffects() {
        _effects = null
    }

//    /**
//     *      Load an array of effects.
//     */
//    fun loadEffs(vararg efs: Eft) {
//        assert(_effects == null) {"load() must work only once."}
//        for(ef in efs)
//            addEff(ef.upBound, ef.acts, ef.brans, ef.stem)
//    }

    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //                                  Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$ protected data ---$$$---$$$---$$$---$$$---$$$--

    protected var _effects: Array<Effect>? = null

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%--- private data ---%%%---%%%---%%%---%%%---%%%---%%%

    //---%%%---%%%---%%%---%%%--- private funcs ---%%%---%%%---%%%---%%%---%%%---%%%
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
        if(_premises == null)
            s += "\n    _premises = null"
        else {
            s += "\n    _premises = ["
            for ((i, prem) in _premises!!.withIndex())
                s += "\n_premises[$i] = $prem".replace("\n", "\n        ")
            s += "\n    ]"
        }

        return s
    }

    /**
     *      Load a number of premises' cids along with their possible negations.
     *  @param prems Prem objects - cid and negation
     */
    fun loadPrems(vararg prems: Prem) {
        if(prems.isEmpty())
            _premises = null
        else
            @Suppress("UNCHECKED_CAST")
            _premises = prems as Array<Prem>
    }

    /**
     *      Load a number of premises' cids along with their possible negations.
     *  @param premoids objects of premises possibly wrapped int the NegatedPremise objects to show that in the _premises
     *          array they should appear with the negation flag, i.e. it's their negatives that will be considered when
     *          calculating the activation value of the neuron. The wrapping is caused by prefixing the premise object with
     *          the ! sign.
     */
    open fun loadPrems(vararg premoids: Any): SpiritLogicalNeuron {

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
                {"Concept must be either NegatedPremise or SpiritPremise, but it is ${premoids[ind]::class.qualifiedName}"}
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

    fun resetPrems() {
        _premises = null
    }

    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //                                  Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$ protected data ---$$$---$$$---$$$---$$$---$$$--

    protected var _premises: Array<Prem>? = null
}

/** Live. */
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
    var upperBound: Float,              // the upper boundary of the span, including
    var actCids: IntArray? = null,      // Array of cids of acts. null or [] - no acts for this span
    var branCids: IntArray? = null,     // Array of cids of brans. null or [] - no new brans for this span
    var stemCid: Cid = 0                // 0 - stay on the current stemCid
) {
    override fun toString(): String {
        var s = this::class.qualifiedName?: ""
        s += "\n    upBound = $upperBound"
        s += "\n${arrayOfCidsNamed("actCids", actCids)}".replace("\n", "\n    ")
        s += "\n${arrayOfCidsNamed("branCids", branCids)}".replace("\n", "\n    ")
        s += "\n    stemCid = ${cidNamed(stemCid)}"

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
        s += "\n    premCid = ${cidNamed(premCid)}"
        s += "\n    negated = $negated"

        return s
    }
}

///**
// *      Used as a vararg parameters for the addEff() function
// */
//class Eft(
//    val upBound: Float,
//    val acts: Array<out SpiritAction>? = null,
//    val brans: Array<SpBreed>? = null,
//    val stem: SpiritNeuron? = null
//)