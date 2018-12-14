package atn

import chribase_thread.CuteThread

/**
 *      Attention dispatcher:
 *  1. On user request starts and and registers an attention circle and sends it reference to the user thread.
 *  2. On the termination message from main initiates termination of all the attention threads
 */
class AttentionDispatcher(): CuteThread(0, 0)
{
}