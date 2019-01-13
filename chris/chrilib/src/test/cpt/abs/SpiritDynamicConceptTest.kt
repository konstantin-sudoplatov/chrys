package cpt.abs

import basemain.acts
import basemain.brans
import cpt.*
import libmain._cr_
import org.junit.Test

class SpiritDynamicConceptTest {

    @Test
    fun testSerialization() {
        val scp = SpAndNeuron(2_000_001)    // object to serialize
        scp.loadPrems(
            SpStringPrem(2_000_002),
            !SpPegPrem(2_000_003)
        ).addEff(
            1f,
            acts(
                SpA(2_000_004),
                SpA_Cid(2_000_005)
            ),
            brans(
                SpBreed(2_000_006),
                SpBreed(2_000_007)
            ),
            stem = SpPickNeuron(2_000_008)
        ).addEff(
            Float.POSITIVE_INFINITY,
            acts(
                SpA_2Cid(2_000_009),
                SpA_LCid(2_000_010)
            ),
            brans(
                SpBreed(2_000_011),
                SpBreed(2_000_012)
            ),
            stem = SpWeightNeuron(2_000_013)
        )

        val sCD = scp.serialize()
        val dcp = _cr_.construct(sCD.clid)     // object to deserialize
        dcp.deserialize(sCD)

        assert(dcp == scp)
    }
}