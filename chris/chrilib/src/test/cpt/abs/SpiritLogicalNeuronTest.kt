package cpt.abs

import cpt.SpStringPrem
import cpt.StringPrem
import org.junit.Test

import org.junit.Assert.*

class SpiritLogicalNeuronTest {

    @Test
    fun addPrems() {

        val nrn = object: SpiritLogicalNeuron(2_000_001) {
            override fun liveFactory(): Concept {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        val strPrem1 = SpStringPrem(2_000_002)
        val strPrem2 = SpStringPrem(2_000_003)

        nrn.loadPrems(
            strPrem1,
            !strPrem2
        )
        println(nrn)
    }
}