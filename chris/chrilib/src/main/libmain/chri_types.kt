package libmain

import basemain.*
import cpt.abs.SpStaticConcept
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

        if      // is cid not set up?
                (cpt.cid == 0)
        {   // no: generate cid
            assert(cpt is SpiritDynamicConcept) {"Only dynamic concepts can be assigned with generated cid. Concept: $cpt"}
            cpt.cid = generateDynamicCid()
        }
        else
        {   // yes: check it
            assert((cpt.cid.toUInt() >= MIN_DYNAMIC_CID && cpt.cid.toUInt() <= MAX_DYNAMIC_CID &&
                cpt is SpiritDynamicConcept) ||
                (cpt.cid.toUInt() >= MIN_STATIC_CID && cpt.cid.toUInt() <= MAX_STATIC_CID &&
                cpt is SpStaticConcept)) {"Cid ${cpt.cid} is out of its range. Concept: $cpt"}
            assert(cpt.cid !in spMap_) {"Cid ${cpt.cid} is already in the map. Concept: $cpt"}
        }

        spMap_[cpt.cid] = cpt
    }

    /**
     *      Get concept by cid. If no such concept, the IndexOutOfBoundsException is thrown.
     */
    @Synchronized operator fun get(cid: Cid): SpiritConcept {
        val cpt = spMap_[cid]
        if(cpt != null) {
            assert(cpt.cid == cid) {"Cid $cid is not equal cpt.cid = ${cpt.cid}"}
            return cpt
        }
        else
            throw IndexOutOfBoundsException("There is no concept with cid $cid in the spirit map.")
    }

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
    private val spMap_ = hashMapOf<Cid, SpiritConcept>()

    //---%%%---%%%---%%%---%%%--- private funcs ---%%%---%%%---%%%---%%%---%%%---%%%

    /**
     *          Generate cid in the dynamic range that is not used in the spirit map.
     */
    @UseExperimental(ExperimentalUnsignedTypes::class)
    private fun generateDynamicCid(): Cid {

        var cid: Cid
        do {
            cid = Random.nextULong(MIN_DYNAMIC_CID.toULong(), (MAX_DYNAMIC_CID).toULong()).toInt()
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
            cid = Random.nextULong(MIN_STATIC_CID.toULong(), (MAX_STATIC_CID).toULong()).toInt()
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

                // Fill in the spirit map
                val cpt = prop.getter.call(crankGroup) as SpiritConcept
                _sm_.add(cpt)

                // May be fill in the name map
                if(GDEBUG_LV >= 0) _nm_!![cpt.cid] = crankGroup::class.simpleName + "." + prop.name
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

class Conf() {

    /** Map of database paramerers: <param name>: <value> */
    var database = HashMap<String, String>()

    /** Number pods in podpool. */
    var podPoolSize: Int = 0
        set(value) {
            require(value >= 1) {"Too few pods in pod pool. Demanded podpool size = $value"}
            field = value
        }
}
