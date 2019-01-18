package libmain

import cpt.SpBreed
import cpt.SpCuteThreadPrem
import cpt.SpStringQueuePrem

/**
 *      Hard cid cranks.
 */
object hCr: CrankModule() {

    object hardCid: CrankGroup {

        /// This is the root branch of the attention circle. It is set up in the circle crank group and is passed to
        // the attention circle constructor.
        val circle_breed = SpBreed(552_559_446)

        // Injected into the circle branch to make it capable of sending messages to the user.
        val userThread_threadprem = SpCuteThreadPrem(-83_085_443)

        // In this buffer the attention circle thread keeps user text lines, where they wait until get processed. Used in
        // the ulread branch.
        val userInputBuffer_strqprem = SpStringQueuePrem(852_186_520)

        override fun crank() {}
    }   //    -1_525_585_308 1_639_186_599 1_632_083_895 -308_279_170
}