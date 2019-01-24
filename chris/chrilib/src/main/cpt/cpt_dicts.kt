package cpt

import basemain.*
import cpt.abs.Dict
import cpt.abs.SpiritDict

/**
 *      Map<String, Cid>. The base of the map is located in the spirit part and deltas in the live part.
 */
class SpStringCidDict(cid: Cid): SpiritDict(cid) {

    val map = HashMap<String, Cid>()

    override fun toString(): String {
        val s = StringBuilder()
        s.append("\n    map(size: ${map.size}) = [")
        for(key in map.keys.take(5))
            s.append("\n        $key: ${map[key]}")
        s.append("\n    ]")

        return super.toString() + s.toString()
    }

    override fun liveFactory()= StringCidDict(this)
}

/**
 *      Live part.
 *  Branch cannot directly update the spirit part of the concept, so, when changes need to be done, they come into the live
 *  part as deltas to the spirit part. There are two levels of delta. Normally only the first one is used, but in case
 *  when branch wants to get current changes written to the database and asks system to do so, it must stop changing the
 *  first level and start updating the second. The first level meanwhile is being assimilated by the system.
 */
class StringCidDict(spStringCidDict: SpStringCidDict): Dict(spStringCidDict), DictDelta<String, Cid> {

    /** Base map. */
    override val baseMap: Map<String, Cid>
        get() = (sp as SpStringCidDict).map

    /** First level of delta. */
    override val firstDelta = MapDelta<String, Cid>()

    /** Second level of delta. */
    override var secondDelta: MapDelta<String, Cid>? = null

    /**
     *      Set new version.
     *  Setting new version means that the first delta is fixed and all changes must go into the second delta.
     */
    override var commitVer: Ver = CUR_VER_FLAG
        set(value) {
            secondDelta = MapDelta()
            field = value
        }

    override fun toString(): String {
        return super.toString() + convertToString().replace("\n", "\n    ")
    }
}
