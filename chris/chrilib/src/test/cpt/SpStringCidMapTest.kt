package cpt

import org.junit.Test

class SpStringCidMapTest {

    @Test
    fun testToString() {
        val s = SpStringCidMap(2_000_000)
        s.map["hello"] = 2_000_001
        s.map["world"] = 2_000_002

        val l = s.liveFactory()
        assert(l["hello"] == 2_000_001)

        l.remove("hello")
        assert(l["hello"] == null)

        l["hello"] = 2_000_003
        assert(l["hello"] == 2_000_003)

        l["hello"] = 2_000_001
        assert(l["hello"] == 2_000_001)

        l["crazy"] = 2_000_004
        assert(l["crazy"] == 2_000_004)
        assert("crazy" in l)
        assert("mad" !in l)

        l.ver = 10      // fix version as 10. All changes will go into the second delta
        assert(l["hello"] == 2_000_001 && "hello" in l)
        assert(l["crazy"] == 2_000_004 && "crazy" in l)
        assert(l["world"] == 2_000_002 && "world" in l)
        assert("mad" !in l)

        l.remove("crazy")
        assert(l["crazy"] == null && "crazy" !in l)

        l["mad"] = 2_000_004
        assert(l["mad"] == 2_000_004 && "mad" in l)

        println(l)
    }
}