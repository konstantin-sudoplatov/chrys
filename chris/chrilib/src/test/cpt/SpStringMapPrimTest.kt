package cpt

import org.junit.Test

class SpStringMapPrimTest {

    @Test
    fun testToString() {
        val m = SpStringMapPrim(2_000_000)
        m.map["hello"] = 2_000_001
        m.map["world"] = 2_000_002

        println(m)
    }
}