
fun main(args: Array<String>) {

        val d = data {
            i = 1
            this
        }
    println(d())
}

data class Data(var i: Int = 0)


fun data(init: Data.() -> Data) = {
    Data().also { it.init()}
}