import libmain._atnDispatcher_
import libmain._console_

fun main(args: Array<String>) {

    _atnDispatcher_.start()
    _console_.initialize(_atnDispatcher_)
    _console_.start()

    _atnDispatcher_.join()
    _console_.join()
}