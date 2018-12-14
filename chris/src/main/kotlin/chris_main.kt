import chrilib._atnDispatcher_
import chrilib._console_

fun main(args: Array<String>) {
    _atnDispatcher_.start()
    _console_.initialize(_atnDispatcher_)
    _console_.start()
}