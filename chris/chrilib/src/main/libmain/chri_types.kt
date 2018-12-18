package libmain

import basemain.Cid
import basemain.MAX_DINAMIC_CID
import basemain.MIN_DYNAMIC_CID
import cpt.abs.SpiritConcept
import kotlin.random.Random
import kotlin.random.nextLong
import kotlin.random.nextULong
import kotlin.reflect.KClass

/**
 *          Synchronized map cid/spiritConcept.
 */
class SpiritMap {

    @Synchronized operator fun get(cid: Cid) = spMap_[cid]

    @Synchronized operator fun get(crankEnum: CrankEnumIfc) = spMap_[crankEnum.cid]

    @Synchronized operator fun set(cid: Cid, cpt: SpiritConcept) {
        spMap_[cid] = cpt
    }

    @Synchronized operator fun set(crankEnum: CrankEnumIfc, cpt: SpiritConcept) {
        spMap_[crankEnum.cid] = cpt
    }

    @Synchronized operator fun contains(cid: Cid) = cid in spMap_

    @Synchronized operator fun contains(crankEnum: CrankEnumIfc) = crankEnum.cid in spMap_

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
 *          Fields and method of crank enums. To implement this interface the enums overload the spiritConcept field. It is
 *      the type of enum by the way.
 */
interface CrankEnumIfc {

    // Must be overridden
    val conceptClass: KClass<out SpiritConcept>

    // Must be overridden
    val cid: Cid

    // non-backing field
    val className: String
        get() = conceptClass.simpleName?:""
}