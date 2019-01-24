package basemain

interface PrimDelta<T> {

    val baseValue: T

    var firstDelta: T?

    var secondDelta: T?

    var commitVer: Ver

    fun get(): T =
        if(commitVer == CUR_VER_FLAG)
            firstDelta?: baseValue
        else
            secondDelta?: firstDelta?: baseValue

    fun set(value: T) {
        if(commitVer == CUR_VER_FLAG)
            firstDelta = value
        else
            secondDelta = value
    }

    fun convertToString(): String {
        var s: String = "\ncommitVer = $commitVer"
        s += "\nfirstDelta = $firstDelta"
        s += "\nsecondDelta = $secondDelta"

        return s
    }
}

/**
 *      Changing a dictionary with two levels of the delta - the firstDelta and secondDelta objects of type MapDelta.
 */
interface DictDelta<K, V> {

    /** Base map. */
    val baseMap: Map<K, V>

    /** First level of delta. */
    val firstDelta: MapDelta<K, V>

    /** Second level of delta. */
    var secondDelta: MapDelta<K, V>?

    /** Version to be transacted. CUR_VER_FLAG - no commission ordered yet. */
    var commitVer: Ver

    /**
     *      Get a value from the base map respecting changes in the deltas
     *  @param key
     *  @return value or null
     */
    operator fun get(key: K): V? {
        val first = firstDelta.get(key, default = baseMap[key])
        return if(secondDelta == null) first else secondDelta!!.get(key, first)
    }

    /**
     *      Set new value by changing deltas without changing the base map.
     *  @param key
     *  @param value
     */
    operator fun set(key: K, value: V) {
        val delta = secondDelta?: firstDelta    // isolate delta that has to change
        delta.adds[key] = value
        delta.dels.remove(key)
    }

    /**
     *      Remove value by changing deltas without changing the base map.
     *  @param key
     *  @return value or null depending on weather this value existed in the base map or deltas
     */
    fun remove(key: K): V? {
        val delta = secondDelta?: firstDelta
        val removedCid = this[key]
        delta.adds.remove(key)
        delta.dels.add(key)

        return removedCid
    }

    /**
     *      Check if the key exists in the base map or deltas.
     *  @param key
     *  @return true/false
     */
    operator fun contains(key: K): Boolean {
        val first = firstDelta.contains(key, default = key in baseMap)
        return if(secondDelta == null) first else secondDelta!!.contains(key, first)
    }

    /** Make the toString() function of an implementer simpler. */
    fun convertToString(): String {
        var s = "\ncommitVer = $commitVer"
        s += "\nfirstDelta = $firstDelta"
        s += "\nsecondDelta = $secondDelta"

        return s
    }
}

/**
 *      Changing a map without changing it. All changes are hold in the delta - the adds and dels maps.
 */
class MapDelta<K, V> {
    val adds = HashMap<K, V>()
    val dels = HashSet<K>()

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
    fun get(key: K, default: V?): V? {
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
    fun contains(key: K, default: Boolean): Boolean {
        assert(!(key in adds && key in dels)) {"Key $key is in both adds and dels."}
        return when (key) {
            in adds -> true
            in dels -> false
            else -> default
        }
    }
}
