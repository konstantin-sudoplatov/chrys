package cpt

import cpt.abs.Concept
import cpt.abs.SpiritConcept
import libmain.StaticConceptFunctor
import java.lang.UnsupportedOperationException

class SpStaticConcept(val func: StaticConceptFunctor): SpiritConcept(func.cid) {

    override fun liveFactory(): Concept {
        throw UnsupportedOperationException("Not aplicable here.")
    }
}