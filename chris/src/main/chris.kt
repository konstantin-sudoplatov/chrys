
import basemain.GDEBUG_LV
import cpt.logSomeFreeClids
import crank.actualizeCrankedConceptsInDb
import crank.loadAndCrankDynamicConcepts
import crank.loadNameMap
import crank.logSomeFreeDynamicCids
import libmain.*
import stat.loadStaticConcepts
import stat.logSomeFreeStaticCids
import java.io.FileNotFoundException

/**
 * Initiation of a new session goes this way: this function starts the sequence by calling the
 * _console_.requestCreationOfAttentionCircle() function. Console sends request to the attention dispatcher. The attention
 * dispatcher sends the pod pool a request for starting the attention circle. The pod pool starts the circle, which creates
 * the ulread branch, which sends the dispacher its origBrad. Dispatcher stores the origBrad along with the console thread reference
 * in its map and forwards the origBrad to console. Since then the console talks to the circle directly, bypassing the dispatcher.
 *
 * The termination sequence is following: userThread enters "p" for the application terminatination, the termination request
 * is sent ot the attention dispatcher, it in its turn sends it to the pod pool, which sends it to all pods and pods
 * terminate their brans. This function waits for termination of all threads, then finishes.
 */
@Throws(FileNotFoundException::class)
fun main(args: Array<String>) {

    // Crank manually programmed concepts and put them in the database.
    val sm = SpiritMap(_dm_)        // temporary spirit map only for cranking and actualizing the DB
    loadAndCrankDynamicConcepts(sm)
    actualizeCrankedConceptsInDb(sm)

    // Load static concept into the spirit map. All the rest will be loaded dynamically from the DB.
    loadStaticConcepts()
    if(GDEBUG_LV >= 0) {
        loadNameMap(_nm_)
        loadFixedCidsIntoNameMap(_nm_!!)
    }

    logSomeFreeClids()
    logSomeFreeStaticCids()
    logSomeFreeDynamicCids()

    _pp_.start()
    _pp_.startPods()
    _atnDispatcher_.start()
    _console_.requestCreationOfAttentionCircle(_atnDispatcher_)
    _console_.start()

    _pp_.join()
    _atnDispatcher_.join()
    _console_.join()
    _dm_.close()
}