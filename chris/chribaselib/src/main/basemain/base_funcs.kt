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

/** Adapter for ar() */
inline fun<reified SpA> acts(vararg elem: SpA) = Array<SpA>(elem.size){ elem[it]}

/** Adapter for ar() */
inline fun<reified SpBreed> brans(vararg elem: SpBreed) = Array<SpBreed>(elem.size){ elem[it]}