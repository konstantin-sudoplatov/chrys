package cpt

import basemain.Cid
import basemain.MAX_STATIC_CID
import basemain.MIN_STATIC_CID
import basemain.logit
import cpt.abs.SpiritDynamicConcept
import libmain._sm_
import kotlin.random.Random
import kotlin.random.nextULong
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

/**
 *      Registry enum of the spirit dynamic concept classes.
 *  We need this to be able to efficiently serialize/deserialize concepts when work with the database.
 *  Do not forget register class along with its clid when add a new dynamic concept to the project.
 */
enum class Reg(val spiritClass: KClass<out SpiritDynamicConcept>, val clid: Short) {
    _1234(SpPegPrem::class, 1234),
    __1234(SpStringPrem::class, -1234)
}

/**
 *      Registry object of the spirit dynamic concept classes.
 *  It is a two-way map of class identifiers/class objects. Class identifier is a short integer.
 *  Also it contains a factory.
 */
object clReg {

    operator fun get(clid: Short): KClass<out SpiritDynamicConcept> {
        return classMap_[clid] as KClass<out SpiritDynamicConcept>
    }

    operator fun get(cpt: SpiritDynamicConcept): Short {
        return clidMap_[cpt::class] as Short
    }

    /**
     *      Factory.
     */
    fun getInstance(clid: Short): SpiritDynamicConcept {
        val clazz = classMap_[clid]
        val inst = clazz?.primaryConstructor?.call(0)
        assert(inst != null) {"Construction of class $clazz with clid $clid failed."}

        return inst!!
    }

    /** Forward map. */
    private val classMap_ = HashMap<Short, KClass<out SpiritDynamicConcept>>()

    /** Reverse map. */
    private val clidMap_ = HashMap<KClass<out SpiritDynamicConcept>, Short>()

    /**
     *          Generate cid in the static range that is not used in the spirit map.
     */
    @UseExperimental(ExperimentalUnsignedTypes::class)
    private fun generateclid(): Short {

        var cid: Short
        do {
            cid = Random.nextULong(MIN_STATIC_CID.toULong(), (MAX_STATIC_CID).toULong() + 1u).toInt()
        } while(cid in this)

        return cid
    }

    /**
     *      Constructor.
     */
    init {
        for(r in Reg.values()) {
            classMap_[r.clid] = r.spiritClass
            clidMap_[r.spiritClass] = r.clid
        }
    }
}

fun logSomeFreeClids() {

    val s = StringBuilder()
    s.append("\nStatic:  ")
    for(cid in _sm_.generateListOfClids(12)){
        s.append("%,d ".format(cid).replace(",", "_"))
    }

    logit(s.toString())
}