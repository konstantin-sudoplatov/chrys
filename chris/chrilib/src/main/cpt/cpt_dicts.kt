package cpt

import basemain.CUR_VER_FLAG
import basemain.Cid
import basemain.Ver
import cpt.abs.Dict
import cpt.abs.SpiritDict

/**
 *      Map<String, Cid>. The base of the map is located in the spirit part and deltas in the live part.
 */
class SpStringCidMap(cid: Cid): SpiritDict(cid) {

    override val map = HashMap<String, Cid>()

    override fun toString(): String {
        val s = StringBuilder()
        s.append("\n    map(size: ${map.size}) = [")
        for(key in map.keys.take(5))
            s.append("\n        $key: ${map[key]}")
        s.append("\n    ]")

        return super.toString() + s.toString()
    }

    override fun liveFactory()= StringCidMap(this)
}

/**
 *      Live part.
 *  Branch cannot directly update the spirit part of the concept, so, when changes need to be done, they come into the live
 *  part as deltas to the spirit part. There are two levels of delta. Normally only the first one is used, but in case
 *  when branch wants to get current changes written to the database and asks system to do so, it must stop changing the
 *  first level and start updating the second. The first level meanwhile is being assimilated by the system.
 */
class StringCidMap(spStringCidMap: SpStringCidMap): Dict(spStringCidMap) {

    /**
     *      Set new version.
     *  Setting new version means that the first delta is fixed and all changes must go into the second delta.
     */
    var ver: Ver = CUR_VER_FLAG
        set(value) {
            secondDelta = StringCidMapDelta()
            field = value
        }

    override fun toString(): String {
        var s = super.toString()
        s += "\nfirstDelta = $firstDelta".replace("\n", "\n    ")
        s += "\nsecondDelta = $secondDelta".replace("\n", "\n    ")

        return s
    }

    operator fun get(key: String): Cid? {
        val first = firstDelta.get(key, default = super.get(key))
        return if(secondDelta == null) first else secondDelta!!.get(key, first)
    }

    operator fun set(key: String, value: Cid) {
        val delta = secondDelta?: firstDelta
        delta.adds[key] = value
        delta.dels.remove(key)
    }

    fun remove(key: String): Cid? {
        val delta = secondDelta?: firstDelta
        val removedCid = this[key]
        delta.adds.remove(key)
        delta.dels.add(key)

        return removedCid
    }

    operator fun contains(key: String): Boolean {
        val first = firstDelta.contains(key, default = super.contains(key))
        return if(secondDelta == null) first else secondDelta!!.contains(key, first)
    }

    /** First level of delta. */
    private val firstDelta = StringCidMapDelta()

    /** Second level of delta. */
    private var secondDelta: StringCidMapDelta? = null
}

/**
 *      Common base for all map deltas.
 */
abstract class BaseMapDelta() {
    abstract val adds: Map<out Any, Cid>
    abstract val dels: Set<Any>

    override fun toString(): String {
        val s = java.lang.StringBuilder(this::class.qualifiedName as String)

        s.append("\n    adds(size: ${adds.size}) = [")
        for(key in adds.keys.take(5))
            s.append("\n        $key: ${adds[key]}")
        s.append("\n    ]")

        s.append("\n    dels(size: ${dels.size}) = [ ")
        for(key in dels.take(5))
            s.append("$key, ")
        s.append("]")

        return s.toString()
    }

    /**
     *      Get the value by key with respect to the changes.
     *  @param key key
     *  @param default The base. This cid (or null, if there is no value to that key) is returned if there was no changes
     *      to that key.
     */
    fun get(key: String, default: Cid?): Cid? {
        assert(!(key in adds && key in dels)) {"Key $key is in both adds and dels."}
        val v = adds[key]
        if(v != null) return v
        if(key in dels) return null
        return default
    }

    /**
     *      Check if the key is in the map, respecting the changes.
     *  @param key key
     *  @param default this is the base value. It is returned if there was no changes to that key.
     */
    fun contains(key: String, default: Boolean): Boolean {
        assert(!(key in adds && key in dels)) {"Key $key is in both adds and dels."}
        return when (key) {
            in adds -> true
            in dels -> false
            else -> default
        }
    }
}

/**
 *      Implementation of delta for the HashMap<String, Cid>
 */
class StringCidMapDelta: BaseMapDelta() {
    override val adds = HashMap<String, Cid>()
    override val dels = HashSet<String>()
}
