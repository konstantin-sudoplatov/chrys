package crank

import libmain.CrankEnumIfc
import org.junit.Test
import kotlin.reflect.full.*

class Crank_mainKtTest {

    @Test
    fun testCranking() {
        val mainCrank = CrankMain()
        val mainCrankClass = CrankMain::class
        for(kfun in mainCrankClass.declaredMemberFunctions) {
            kfun.call(mainCrank)
        }


        for(enClass in mainCrankClass.nestedClasses)
            for(en in enClass.java.enumConstants) {
                println(en.toString())
                println((en as CrankEnumIfc).cid)
            }

        val funRef = ::testEnum3
    }
}