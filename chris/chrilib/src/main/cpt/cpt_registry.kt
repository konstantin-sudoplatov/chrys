package cpt

import basemain.Clid
import basemain.MAX_CLID
import basemain.MIN_CLID
import basemain.logit
import cpt.abs.SpiritDynamicConcept
import libmain._cr_
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

/**
 *      Registry of the spirit dynamic concept classes.
 *  We need this to be able to efficiently serialize/deserialize concepts when work with the database.
 *  Do not forget register class along with its clid when add a new dynamic concept to the project.
 */
enum class Reg(val clazz: KClass<out SpiritDynamicConcept>, val clid: Clid) {

    // actions
    _28_337(clazz = SpA::class, clid = 28_337),
    __11_624(clazz = SpA_Cid::class, clid = -11_624),
    _12_108(clazz = SpA_2Cid::class, clid = 12_108),
    __17_681(clazz = SpA_LCid::class, clid = -17_681),

    // neurons
    __19_891(clazz = SpActionNeuron::class, clid = -19_891),
    _11_242(clazz = SpSeed::class, clid = 11_242),
    _13_772(clazz = SpWeightNeuron::class, clid = 13_772),
    _1_121(clazz = SpAndNeuron::class, clid = 1_121),
    __20_770(clazz = SpPickNeuron::class, clid = -20_770),

    // premises
    _26_189(clazz = SpBradPrem::class, clid = 26_189),
    _1_896(clazz = SpBreed::class, clid = 1_896),
    __2_566(clazz = SpCuteThreadPrem::class, clid = -2_566),
    _6_843(clazz = SpPegPrem::class, clid = 6_843),
    _16_254(clazz = SpStringPrem::class, clid = 16_254),
    __27_321(clazz = SpStringQueuePrem::class, clid = -27_321),

    // primitives
    _8_328(clazz = SpMarkPrim::class, clid = 8_328),
    __32_327(clazz = SpStringPrim::class, clid = -32_327),
    __5_006(clazz = SpNumPrim::class, clid = -5_006),
    __7_614(clazz = SpStringCidDict::class, clid = -7_614),

    //__26_035(clazz = ::class, clid = -26_035),
    //__18_796(clazz = ::class, clid = -18_796),
    //_16_390(clazz = ::class, clid = 16_390),
    //__25_733(clazz = ::class, clid = -25_733),
    //_2_756(clazz = ::class, clid = 2_756),

    //_(clazz = ::class, clid = ),
}   //2_408 15_264 -24_107 14_771 13_472 9_936 -20_690 -13_916 21_559 25_385 -6_183 15_099

/**
 *      Registry object of the spirit dynamic concept classes.
 *  It is a two-way map of class identifiers/class objects. Class identifier is a short integer.
 *  Also it contains a factory, that generates dynamic concepts objects on based their clid.
 */
class ClassRegistry {

    operator fun get(clid: Clid): KClass<out SpiritDynamicConcept> {
        assert(clid in classMap_) {"Clid $clid is not present in the class registry."}
        return classMap_[clid] as KClass<out SpiritDynamicConcept>
    }

    operator fun get(clazz: KClass<out SpiritDynamicConcept>): Clid {
        assert(clazz in clidMap_) {"Class $clazz is not present in the class registry."}
        return clidMap_[clazz] as Short
    }

    operator fun contains(clid: Clid): Boolean {
        return clid in classMap_
    }

    operator fun contains(cpt: SpiritDynamicConcept): Boolean {
        return cpt::class in clidMap_
    }

    /**
     *      Factory. Creates object of a class with designated clid.
     *  @param clid class identifier
     */
    fun construct(clid: Clid): SpiritDynamicConcept {
        val clazz = classMap_[clid]
        val inst = clazz?.primaryConstructor?.call(0)
        assert(inst != null) {"Construction of class $clazz with clid $clid failed."}

        return inst!!
    }

    fun generateListOfClids(size: Int): List<Clid> {
        return listOf<Clid>(*Array<Clid>(size, {generateClid()}))
    }

    /** Forward map. */
    private val classMap_ = HashMap<Clid, KClass<out SpiritDynamicConcept>>()

    /** Reverse map. */
    private val clidMap_ = HashMap<KClass<out SpiritDynamicConcept>, Clid>()

    /**
     *          Generate cid in the static range that is not used in the spirit map.
     */
    @UseExperimental(ExperimentalUnsignedTypes::class)
    private fun generateClid(): Clid {

        var clid: Clid
        do {
            clid = Random.nextInt(MIN_CLID, MAX_CLID).toShort()
        } while(clid in _cr_)

        return clid
    }

    /**
     *      Constructor.
     */
    init {
        for(r in Reg.values()) {
            classMap_[r.clid] = r.clazz
            clidMap_[r.clazz] = r.clid
        }
    }
}

fun logSomeFreeClids() {

    val s = StringBuilder()
    s.append("Clids:  ")
    for(clid in _cr_.generateListOfClids(12)){
        s.append("%,d ".format(clid).replace(",", "_"))
    }
    logit(s.toString())
}
