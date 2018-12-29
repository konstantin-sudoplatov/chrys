package atn

import org.junit.Test

class BranchTest {

    @Test
    fun get() {
        val br = Branch(42, Brad(Pod("test pod", 43), 44), null)
        println(br[45])
    }
}