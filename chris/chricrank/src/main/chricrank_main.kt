import basemain.Cid
import cpt.abs.Concept
import cpt.abs.SpiritConcept
import kotlin.reflect.KClass

class TestConcept(cid: Cid): SpiritConcept(cid) {
    override fun live_factory(): Concept {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

interface TestInterface {

    val concept: SpiritConcept

    val cid: Cid
    get() { return concept.cid }

    val clazz: KClass<out SpiritConcept>
    get() { return concept::class}

    val className: String
    get() { return concept::class.simpleName?:"noname"}
}

typealias VeryDetailedNameOfTheTestEnum = TestEnum
enum class TestEnum(override val concept: SpiritConcept): TestInterface {
    aaa(TestConcept(1)),
    bbb(TestConcept(2));
}

fun main(args: Array<String>) {
    println(VeryDetailedNameOfTheTestEnum.aaa.cid)
    println(VeryDetailedNameOfTheTestEnum.bbb.clazz)
    println(VeryDetailedNameOfTheTestEnum.bbb.className)
}