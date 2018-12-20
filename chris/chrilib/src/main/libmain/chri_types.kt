package libmain

import atn.Branch
import basemain.*
import cpt.SpiritStaticConcept
import cpt.abs.SpiritConcept
import cpt.abs.SpiritDynamicConcept
import kotlin.random.Random
import kotlin.random.nextULong
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubtypeOf

/**
 *          Synchronized map cid/spiritConcept.
 */
class SpiritMap {

    val size
        get() = spMap_.size

    /**
     *      Add a concept to the spirit map. If cid of the concept is not set (0), then it will be generated.
     *  @param cpt concept to add
     */
    @UseExperimental(ExperimentalUnsignedTypes::class)
    @Synchronized fun add(cpt: SpiritConcept) {

        if
                (cpt.cid == 0)
        {
            assert(cpt is SpiritDynamicConcept)
            cpt.cid = generateDynamicCid()
        }
        else {
            assert((cpt.cid.toULong() >= MIN_DYNAMIC_CID.toULong() && cpt.cid.toULong() <= MAX_DYNAMIC_CID.toULong() &&
                cpt is SpiritDynamicConcept) ||
                (cpt.cid.toULong() >= MIN_STATIC_CID.toULong() && cpt.cid.toULong() <= MAX_STATIC_CID.toULong() &&
                cpt is SpiritStaticConcept))
        }

        spMap_[cpt.cid] = cpt
    }

    @Synchronized operator fun get(cid: Cid) = spMap_[cid]

    @Synchronized operator fun contains(cid: Cid) = cid in spMap_

    @Synchronized fun generateListOfDynamicCids(size: Int): List<Cid> {
        return listOf<Cid>(*Array<Cid>(size, {generateDynamicCid()}))
    }

    @Synchronized fun generateListOfStaticCids(size: Int): List<Cid> {
        return listOf<Cid>(*Array<Cid>(size, {generateStaticCid()}))
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%--- private data ---%%%---%%%---%%%---%%%---%%%---%%%

    /**  The spirit map */
    private val spMap_ = mutableMapOf<Cid, SpiritConcept>()

    //---%%%---%%%---%%%---%%%--- private funcs ---%%%---%%%---%%%---%%%---%%%---%%%

    /**
     *          Generate cid in the dynamic range that is not used in the spirit map.
     */
    @UseExperimental(ExperimentalUnsignedTypes::class)
    private fun generateDynamicCid(): Cid {

        var cid: Cid
        do {
            cid = Random.nextULong(MIN_DYNAMIC_CID.toULong(), (MAX_DYNAMIC_CID).toULong() + 1u).toInt()
        } while(cid in this)

        return cid
    }

    /**
     *          Generate cid in the static range that is not used in the spirit map.
     */
    @UseExperimental(ExperimentalUnsignedTypes::class)
    private fun generateStaticCid(): Cid {

        var cid: Cid
        do {
            cid = Random.nextULong(MIN_STATIC_CID.toULong(), (MAX_STATIC_CID).toULong() + 1u).toInt()
        } while(cid in this)

        return cid
    }
}

/**
 *          Base for the crank modules.
 */
open class CrankModule() {

    /**
     *      Load concepts declared in all crank groups of the module into the spirit map
     */
    fun loadSpiritMap() {

        // Extract list of crank groups
        @Suppress("UNCHECKED_CAST")
        val crankGroups = this::class.nestedClasses.map { it.objectInstance }
            .filter { it != null && CrankGroup::class.isInstance(it)} as List<CrankGroup>

        // For every group load its concepts into the spirit map
        for(crankGroup in crankGroups) {
            for(prop in crankGroup::class.declaredMemberProperties) {
                assert(prop.returnType.isSubtypeOf(SpiritConcept::class.createType()))
                    {"Property must be of type SpiritConcept and it is $prop"}
                val cpt = prop.getter.call(crankGroup) as SpiritConcept
                _sm_.add(cpt)
            }
        }
    }

    /**
     *      Run crank functions for all groups in the module
     */
    fun doCranking() {

        // Extract list of crank groups
        val crankGroups = this::class.nestedClasses.map { it.objectInstance }
            .filter { it != null && CrankGroup::class.isInstance(it)} as List<*>

        // Call crank() functions for all groups in the module
        for(crankGroup in crankGroups)
            (crankGroup as CrankGroup).crank()
    }
}

/**
 *          A crank group inherits this interface just in order we could check in reflection that it's a crank group
 */
interface CrankGroup {
    fun crank()
}

/**
 *          Base for the stat modules.
 */
open class StatModule {

    /**
     *      Load concepts declared in this module into the spirit map
     */
    fun loadSpiritMap() {

        // Extract list of stat functors
        @Suppress("UNCHECKED_CAST")
        val statFuncs = this::class.nestedClasses.map { it.objectInstance }
            .filter { it != null && SF::class.isInstance(it) || SFC::class.isInstance(it)
                    || SFLC::class.isInstance(it)} as List<StaticConceptFunctor>

        for(statFunc in statFuncs)
            _sm_.add(SpiritStaticConcept(statFunc))
    }
}

/**
 *      Base for static function functors.
 */
abstract class StaticConceptFunctor(val cid: Cid)

abstract class StaticConceptFunctorReturnUnit(cid: Cid): StaticConceptFunctor(cid) {
    abstract fun func(br: Branch, vararg par: Cid): Unit
}
typealias SF = StaticConceptFunctorReturnUnit

abstract class StaticConceptFunctorReturnCid(cid: Cid): StaticConceptFunctor(cid) {
    abstract fun func(br: Branch, vararg par: Cid): Cid
}
typealias SFC = StaticConceptFunctorReturnCid

abstract class StaticConceptFunctorReturnListOfCids(cid: Cid): StaticConceptFunctor(cid) {
    abstract fun func(br: Branch, vararg par: Cid): List<Cid>
}
typealias SFLC = StaticConceptFunctorReturnListOfCids
