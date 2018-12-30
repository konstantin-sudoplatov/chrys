package basemain

/**
 *      Log line of text.
 *  @param logLine line to log.
 */
fun logit(logLine: String) {
    println(logLine)
}

/**
 *      Convert vararg to array.
 *  @param T type parameter.
 *  @param elem elements of the array
 *  @return Array of elements
 */
inline fun<reified T> ar(vararg elem: T) = Array<T>(elem.size){ elem[it]}

/** Alias for the ar() function. */
inline fun<reified T> acts(vararg elem: T) = Array<T>(elem.size){ elem[it]}

/** Alias for the ar() function. */
inline fun<reified T> brans(vararg elem: T) = Array<T>(elem.size){ elem[it]}

/** Alias for the ar() function. */
inline fun<reified T> ins(vararg elem: T) = Array<T>(elem.size){ elem[it]}

/** Alias for the ar() function. */
inline fun<reified T> outs(vararg elem: T) = Array<T>(elem.size){ elem[it]}
