package libmain

import org.junit.Test

import org.junit.Assert.*

class SpiritMapTest {

    @Test
    fun generateDynamicCid() {

        for(i in 0..7){
            val s = "%,d".format(_sm_.generateDynamicCid()).replace(",", "_")
            print("$s ")
        }
    }
}