package libmain

import atn.Branch
import basemain.Cid
import basemain.MAX_DINAMIC_CID
import basemain.MIN_DYNAMIC_CID
import cpt.SpPegPrem
import cpt.abs.SpiritConcept
import kotlin.random.Random
import kotlin.random.nextLong
import kotlin.random.nextULong
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.jvm.javaField

/**
 *          Synchronized map cid/spiritConcept.
 */
class SpiritMap {

    @Synchronized fun add(cpt: SpiritConcept) { spMap_[cpt.cid] = cpt }

    @Synchronized operator fun get(cid: Cid) = spMap_[cid]

    @Synchronized operator fun contains(cid: Cid) = cid in spMap_

    /**
     *          Generate cid in the dynamic range not used in the spirit map.
     */
    @Synchronized fun generateDynamicCid(): Cid {
        var cid: Cid
        @UseExperimental(ExperimentalUnsignedTypes::class)
        do {
            cid = Random.nextULong(MIN_DYNAMIC_CID.toULong(), (MAX_DINAMIC_CID).toULong() + 1u).toInt()
        } while(cid in this)

        return cid
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
            .filter { it != null && CrankGroup::class.isInstance(it)} as List<Any>

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
        val statFuncs = this::class.nestedClasses.map { it.objectInstance }
            .filter { it != null && SF::class.isInstance(it) || SFC::class.isInstance(it)
                    || SFLC::class.isInstance(it)} as List<*>

        for(statFunc in statFuncs) {
            _sm_.add(SpStatConcept(statFunc))
        }
    }
}

/**
 *      Base for static function functors.
 */
abstract class BaseStaticFunction(val cid: Cid)

abstract class StaticFunction(cid: Cid): BaseStaticFunction(cid) {
    abstract fun func(br: Branch, vararg par: Cid): Unit
}
typealias SF = StaticFunction

abstract class StaticFunctionReturnCid(cid: Cid): BaseStaticFunction(cid) {
    abstract fun func(br: Branch, vararg par: Cid): Cid
}
typealias SFC = StaticFunctionReturnCid

abstract class StaticFunctionReturnListOfCids(cid: Cid): BaseStaticFunction(cid) {
    abstract fun func(br: Branch, vararg par: Cid): List<Cid>
}
typealias SFLC = StaticFunctionReturnListOfCids
