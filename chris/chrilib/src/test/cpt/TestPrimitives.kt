package cpt

import org.junit.Test

class TestPrimitives {

    @Test
    fun stringPrim() {
        val s = SpStringPrim(2_000_000)
        s.load("hello")

        val l = s.liveFactory()
        assert(l.get() == "hello")
        l.set("Hello")
        assert(l.get() == "Hello")

        l.commitVer = 10
        assert(l.get() == "Hello")

        l.set("world")
        assert(l.get() == "world")

        l.set("Hello")
        assert(l.get() == "Hello")

        l.set("hello")
        assert(l.get() == "hello")

        println(l)
    }

    @Test
    fun stringCidDict() {
        val s = SpStringCidDict(2_000_000)
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

        l.commitVer = 10      // fix version as 10. All changes will go into the second delta
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