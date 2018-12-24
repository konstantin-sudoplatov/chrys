package cpt

import atn.Brid
import atn.Pod
import org.junit.Test

import org.junit.Assert.*

class BreedTest {

    @Test
    fun testToString() {
        val breed = SpBreed(42).liveFactory()
        breed.brid = Brid(Pod("test pod", 43), 44)
        println(breed)
    }
}