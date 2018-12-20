import crank.loadAndCrankDynamicConcepts
import crank.loadStaticConcepts
import libmain._atnDispatcher_
import libmain._console_

fun main(args: Array<String>) {

    loadStaticConcepts()
    loadAndCrankDynamicConcepts()

    _console_.requestCreationOfAttentionCircle(_atnDispatcher_)

    _atnDispatcher_.join()
    _console_.join()
}