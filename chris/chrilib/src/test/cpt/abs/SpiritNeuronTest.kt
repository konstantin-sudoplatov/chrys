package cpt.abs

import cpt.SpActionNeuron
import org.junit.Test

import org.junit.Assert.*

class SpiritNeuronTest {

    @Test
    fun testSelectEffect() {
        var nrn = object: SpiritNeuron(2_000_042) {
            override fun liveFactory(): Concept {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        assert(nrn.isCutoff)
        nrn.addEff(0f, null, SpActionNeuron(2_000_001), null)
        nrn.cutoff = -1f
        assert(nrn.isCutoff)
        nrn.addEff(1f, null, SpActionNeuron(2_000_002), null)
        assert(nrn.isCutoff)

        println(nrn)

        val eff = nrn.selectEffect(2f)
        println(eff)
    }
}
