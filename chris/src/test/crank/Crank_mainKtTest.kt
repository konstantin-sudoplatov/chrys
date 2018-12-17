package crank

import org.junit.Test
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.memberFunctions

class Crank_mainKtTest {

    enum class hello {
        aaa,
        bbb
    }

    @Test
    fun testCranking() {
        val crankClass = ReflectionTest.getFileClass()
//        for(method in crankClass.getDeclaredCl)
//            println(method)
    }
}