package cpt.abs

import cpt.SpActionNeuron
import org.junit.Test

class SpiritNeuronTest {

    @Test
    fun testSelectEffect() {
        var nrn = object: SpiritNeuron(2_000_042) {
            override fun liveFactory(): DynamicConcept {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        assert(nrn.isCutoff)
        nrn.addEff(0f, null, null, SpActionNeuron(2_000_001))
        nrn.cutoff = -1f
        assert(nrn.isCutoff)
        nrn.addEff(1f, null, null, SpActionNeuron(2_000_002))
        assert(nrn.isCutoff)

        println(nrn)

        val eff = nrn.selectEffect(2f)
        println(eff)
    }
}
