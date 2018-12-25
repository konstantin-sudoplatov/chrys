package atn

import cpt.Breed
import org.junit.Test

import org.junit.Assert.*

class BranchTest {

    @Test
    fun get() {
        val br = Branch(42, Brid(Pod("test pod", 43), 44))
        println(br[45])
    }
}