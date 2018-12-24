package libmain

import cpt.SpBreed

object hardCrank: CrankModule() {

    object hardCids: CrankGroup {

        /// This is the root branch of the attention circle. It is set up in the circle crank group and will be passed to
        // the attention circle constructor.
        val circle_breed = SpBreed(552_559_446)

        override fun crank() {}
    }   //  -83_085_443 852_186_520 -1_525_585_308 1_639_186_599 1_632_083_895 -308_279_170
}