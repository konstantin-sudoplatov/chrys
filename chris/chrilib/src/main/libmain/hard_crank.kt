package libmain

import cpt.SpBreed
import cpt.SpCuteThreadPrem

object hardCrank: CrankModule() {

    object hardCid: CrankGroup {

        /// This is the root branch of the attention circle. It is set up in the circle crank group and is passed to
        // the attention circle constructor.
        val circle_breed = SpBreed(552_559_446)

        // Injected into the circle branch to make it capable of sending messages to the user.
        val userThread_prem = SpCuteThreadPrem(-83_085_443)

        override fun crank() {}
    }   //   852_186_520 -1_525_585_308 1_639_186_599 1_632_083_895 -308_279_170
}