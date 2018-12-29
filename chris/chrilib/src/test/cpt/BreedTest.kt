package cpt

import atn.Brad
import atn.Pod
import org.junit.Test

class BreedTest {

    @Test
    fun testToString() {
        val breed = SpBreed(42).liveFactory()
        breed.brad = Brad(Pod("test pod", 43), 44)
        println(breed)
    }
}