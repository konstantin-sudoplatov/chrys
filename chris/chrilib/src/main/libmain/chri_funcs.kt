package libmain

import basemain.Cid
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

/**
 *          Give cid a name if it exists in the name map.
 *  @param cid Cid
 */
fun cidNamed(cid: Cid): String {
    val s = "%,d".format(cid).replace(",", "_")
    if(_nm_ != null && cid in _nm_)
        return "$s (${_nm_[cid]})"
    else
        return "$s (noname)"
}

/**
 *          Give cid a name if it exists in the name map.
 *  @param cid Cid
 */
fun namedCid(cid: Cid): String {
    val s = "%,d".format(cid).replace(",", "_")
    if(_nm_ != null && cid in _nm_)
        return "${_nm_[cid]}($s)"
    else
        return "$s (noname)"
}

/**
 *      Render array of cids to a string.
 *  @param arrName array name
 *  @param arr array
 *  @param lim limit of elements of array to print
 */
fun arrayOfCidsNamed(arrName: String, arr: IntArray?, lim: Int = 5): String {
    var s: String
    if(arr == null) {
        s = "$arrName = null"
        return s
    }
    else
        s = "$arrName(size = ${arr.size}) = ["

    for(cid in arr.take(lim)) {
        s += "\n    ${cidNamed(cid)}"
    }
    s += "\n]"

    return s
}

/**
 *      Parse yaml config into the Conf object.
 *  Define inputStream as a file, if you don't want to make the config file part of the source code. Some
 *  extra logic will be needed to specify the config path in more civilized manner than this. The declaration would look like:
 *
 *  val inputStream = FileReader(File("src/main/chris_config.yaml"))
 *
 *  Define inputStream through class loader if it is acceptable to have the config file a part of source code.
 *
 *  val inputStream = object {}::class.java.classLoader.getResourceAsStream("chris_config.yaml")
 */
fun parseConfig(fileName: String): Conf {
    val inputStream = object {}::class.java.classLoader.getResourceAsStream(fileName)
    return Yaml(Constructor(Conf::class.java)).load<Conf>(inputStream)
}