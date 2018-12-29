import crank.loadAndCrankDynamicConcepts
import libmain._atnDispatcher_
import libmain._console_
import libmain._pp_
import stat.loadStaticConcepts

/**
 * Initiation of a new session goes as following: this function starts the sequence by calling the
 * _console_.requestCreationOfAttentionCircle() function. Console sends request to the attention dispatcher. The attention
 * dispatcher sends the pod pool a request for starting the attention circle. The pod pool starts the circle, which creates
 * the uline branch, which sends the dispacher its ownBrad. Dispatcher stores the ownBrad along with the console thread reference
 * in its map and forwards the ownBrad to console. Since then the console talks to the circle directly, bypassing the dispatcher.
 *
 * The termination sequence is following: userThread enters "p" for the application terminatination, the termination request
 * is sent ot the attention dispatcher, it in its turn sends it to the pod pool, which sends it to all pods and pods
 * terminate their branches. This function waits for termination of all threads, then finishes.
 */
fun main(args: Array<String>) {

    loadStaticConcepts()
    loadAndCrankDynamicConcepts()

    _pp_.start()
    _pp_.startPods()
    _atnDispatcher_.start()
    _console_.requestCreationOfAttentionCircle(_atnDispatcher_)
    _console_.start()

    _pp_.join()
    _atnDispatcher_.join()
    _console_.join()
}