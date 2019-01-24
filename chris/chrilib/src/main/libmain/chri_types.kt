package libmain

import basemain.*
import cpt.abs.SpStaticConcept
import cpt.abs.SpiritConcept
import cpt.abs.SpiritDynamicConcept
import db.DataBase
import kotlin.random.Random
import kotlin.random.nextULong
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubtypeOf

/**
 *          Synchronized map cid/spiritConcept.
 */
class SpiritMap(val dbm: DbManager) {

    /** Current (the latest actual) version. */
    var curVer: Ver = 0

    /** Minimal actual version. */
    var minVer: Ver = 0

    /** Minimal stale version. Is to be cleared up to the minVer. */
    var staleVer: Ver = 0;

    /**  The spirit map */
    val map = hashMapOf<Int, SpiritConcept>()

    /**
     *      Add a concept to the spirit map. If cid of the concept is not set (0), then it will be generated.
     *  @param cpt concept to add
     */
    @UseExperimental(ExperimentalUnsignedTypes::class)
    @Synchronized fun add(cpt: SpiritConcept, ver: Ver = CUR_VER_FLAG) {

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
            assert(mapKey(cpt.cid, ver) !in map) {"Cid ${cpt.cid} is already in the map. Concept: $cpt"}
        }

        // Must be new
        require(cpt.cid !in this) {"Cid ${cidNamed(cpt.cid)} must not be present in the map nor in DB, but it is."}

        // Put new concept in the map and DB
        map[mapKey(cpt.cid, ver)] = cpt
        if(cpt is SpiritDynamicConcept)
            dbm.insertConcept(cpt)
    }

    /**
     *      Get concept by cid. If no such concept, the IndexOutOfBoundsException is thrown.
     *  @param cid
     *  @param ver version to check
     *  @return the concept or null if not found
     */
    @Synchronized operator fun get(cid: Cid, ver: Ver = CUR_VER_FLAG): SpiritConcept? {
        var cpt = map[mapKey(cid, ver)]

        // The most likely case is when the commitVer parameter equal the CUR_VER_FLAG
        if(cpt != null) {
            assert(cpt.cid == cid && cpt.ver == ver) {"Cid $cid($ver) is not equal cpt.cid = ${cpt!!.cid}(${cpt!!.ver})"}
            return cpt
        }
        else
            if(ver == CUR_VER_FLAG) {
                // May be the commitVer is not CUR_VER_FLAG but the curVer instead.
                cpt = map[mapKey(cid, curVer)]
                if (cpt != null) {
                    assert(cpt.cid == cid && cpt.ver == ver) { "Cid $cid($ver) is not equal cpt.cid = ${cpt!!.cid}(${cpt!!.ver})" }
                    return cpt
                }
            }

        // May be it is in the db under the CUR_VER_FLAG version
        cpt = _dm_.getConcept(cid, ver)
        if(cpt != null) {
            map[mapKey(cid, ver)] = cpt
            return cpt
        }
        else
            if(ver == CUR_VER_FLAG) {
                // May be it is in the db under the curVer version
                cpt = _dm_.getConcept(cid, curVer)
                if (cpt != null) {
                    map[mapKey(cid, curVer)] = cpt
                    return cpt
                }
            }

        // Concept not found
        return null
    }

    /**
     *      Check if the map or DB contains this cid. Concept is in the map or DB if it has either the CUR_VER_FLAG or
     *  curVer version.
     *  @param cid
     *  @return true/false
     */
    @Synchronized operator fun contains(cid: Cid): Boolean {
        if(map[mapKey(cid, CUR_VER_FLAG)] != null)
            return true
        else
            if(map[mapKey(cid, curVer)] != null)
                return true

        if(dbm.getConcept(cid, CUR_VER_FLAG) != null)
            return true
        else
            if(dbm.getConcept(cid, curVer) != null)
                return true

        return false
    }

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

    /**
     *      Mix cid and version into a key for the spirit map.
     *  @param cid cid
     *  @param ver concept version
     */
    private fun mapKey(cid: Cid, ver: Ver): Int {
        return cid xor (ver.toInt() shl 16)
    }

    init {
        staleVer = requireNotNull(dbm.getParam("_stale_ver_")?.toShort()) {"Parameter _stale_ver_ not found in the DB."}
        minVer = requireNotNull(dbm.getParam("_min_ver_")?.toShort()) {"Parameter _min_ver_ not found in the DB."}
        curVer = requireNotNull(dbm.getParam("_cur_ver_")?.toShort()) {"Parameter _cur_ver_ not found in the DB."}
    }
}

/**
 *          Base for the crank modules.
 */
open class CrankModule() {

    /**
     *      Load concepts declared in all crank groups of the module into the spirit map.
     *  @param sm spirit map to load concepts to
     */
    fun loadSpiritMap(sm: SpiritMap) {

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
                require(cpt.cid.toUInt().toULong() >= MIN_DYNAMIC_CID && cpt.cid.toUInt().toULong() <= MAX_DYNAMIC_CID &&
                        cpt.cid !in sm.map)
                sm.map[cpt.cid] = cpt

                // May be fill in the name map
                if(GDEBUG_LV >= 0) _nm_!![cpt.cid] = crankGroup::class.simpleName + "." + prop.name
            }
        }
    }

    /**
     *      Load name map with the names of concepts.
     *  @param nm name map to load
     */
    fun loadNameMap(nm: HashMap<Cid, String>?) {

        // Extract list of crank groups
        @Suppress("UNCHECKED_CAST")
        val crankGroups = this::class.nestedClasses.map { it.objectInstance }
            .filter { it != null && CrankGroup::class.isInstance(it)} as List<CrankGroup>

        // For every group load its concepts into the spirit map
        for(crankGroup in crankGroups) {
            for(prop in crankGroup::class.declaredMemberProperties) {
                val cpt = prop.getter.call(crankGroup) as SpiritConcept
                nm!![cpt.cid] = crankGroup::class.simpleName + "." + prop.name
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
 *      Settings in the yaml config are parsed in here.
 */
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

/**
 *      All communication with the database goes through this class. On initialization it creates an object of the DataBase
 *  class which opens the database connection and provides methods for interacting with the tables.
 */
class DbManager(conf: Conf) {

    /**
     *      Close the database connection.
     */
    fun close() {
        db_.close()
    }

    /** The database connection and methods for working with tables. */
    val db_ = DataBase(
            conf.database["connectionString"]!!,
            conf.database["dbName"]!!,
            conf.database["schema"]!!,
            conf.database["user"]!!,
            conf.database["password"]!!
    )

    /**
     *      Get parameter value.
     *  @param parName
     */
    fun getParam(parName: String): String? {
        return db_.params.getParam(parName)
    }

    /**
     *      Set parameter value.
     *  @param parName
     *  @param value
     */
    fun setParam(parName: String, value: String?) {
        db_.params.setParam(parName, value)
    }

    /**
     *      Get concept with designated cid and commitVer.
     *  @param cid concept identifier
     *  @param ver concept version
     *  @return spirit dynamic concept on null if not found. The static concepts are not kept in the database.
     */
    fun getConcept(cid: Cid, ver: Ver): SpiritDynamicConcept? {
        val sCD = db_.concepts.getConcept(cid, ver)
        if(sCD == null) return null

        val cpt = _cr_.construct(sCD.clid)
        cpt.deserialize(sCD)

        return cpt
    }

    /**
     *      Insert a concept into the database.
     *  @param cpt spirit dynamic concept to insert
     */
    fun insertConcept(cpt: SpiritDynamicConcept) {
        val sCD = cpt.serialize()
        db_.concepts.insertConcept(sCD.cid, sCD.ver, sCD.clid, sCD.stable?.array(), sCD.transient?.array())
    }

    /**
     *      Update a concept in the database.
     *  @param cpt spirit dynamic concept to update
     */
    fun updateConcept(cpt: SpiritDynamicConcept) {
        val sCD = cpt.serialize()
        db_.concepts.updateConcept(sCD.cid, sCD.ver, sCD.clid, sCD.stable?.array(), sCD.transient?.array())
    }

    /**
     *      Get all versions of given concept in the database.
     *  @param cid cid
     *  @return array of versions or null if there no record with this cid in the database.
     */
    fun getConceptVersions(cid: Cid): ShortArray? {
        return db_.concepts.getConceptVersions(cid)
    }
}