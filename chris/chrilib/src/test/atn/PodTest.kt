package atn

import libmain.POD_POOL_SIZE
import org.junit.Test

class PodPoolTest {

    @Test
    fun comparatorTest() {
        val pod = Pod("test_pod", 0)
        val pod1 = Pod("test_pod1", 1)
        val pod11 = Pod("test_pod1", 1)

        assert(PodComparator().compare(pod, pod1) == -1)
        assert(PodComparator().compare(pod1, pod) == 1)
        assert(PodComparator().compare(pod1, pod11) == 0)
    }

    @Test
    fun poolCreationTest() {
        val pool = PodPool()
        assert(pool.size == POD_POOL_SIZE)
    }
}