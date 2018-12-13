import base_thread.ThreadQueue

/**
 * This class waits for messages from the attention circle which it prints on the console on one hand and user lines from
 * the Readln thread on, which it sends to the circle the other hand. It is also responsible for requesting start of the
 * attention circle. This request on initialization it sends to attention dispatcher. Also it termination of work is
 * originated in here and the request is sent to the main thread, which sends it to all subsystems.
 */
class ConsoleThread(timeoutMsecs: Int ): ThreadQueue(timeoutMsecs) {

}