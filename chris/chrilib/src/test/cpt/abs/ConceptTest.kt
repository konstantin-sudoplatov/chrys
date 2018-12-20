package cpt.abs

import cpt.SpStringPrem
import cpt.StringPrem
import org.junit.Test

import org.junit.Assert.*

class ConceptTest {

    @Test
    fun clone() {
        val cpt = SpStringPrem(2_000_999).live_factory() as StringPrem

    }
}