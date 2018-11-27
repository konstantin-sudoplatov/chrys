module atn.atn_dispatcher;
import std.stdio;
import std.concurrency, core.thread;
import std.format;

import proj_data, proj_funcs;

import chri_data;
import messages;
import atn.atn_caldron;

/**
        Thread function for attention dispatcher.
*/
void attention_dispatcher_thread_func() {try {   // catchall try block for catching flying exceptions and forwarding them to the owner thread.

    // Receive messages in a cycle
    while(true) {
        import std.variant: Variant;

        Msg msg;                    // regular message
        Throwable ex;               // exception message from a child
        Variant var;                // the catchall type

        // Receive new message
        receive(
            (immutable Msg m){msg = cast()m;},
            (shared Throwable e){ex = cast()e;},
            (Variant v){var = v;}
        );

        // Recognize and process the message
        if      // is it a regular message?
                (msg)
        {   // process it
            if      // is it the pool's request for creating threads?
                    (auto m = cast(immutable CaldronThreadPoolAsksDispatcherForThreadBatch_msg)msg)
            {   // yes: create and push them to the pool
                _threadPool_.addThreadBatch;
            }
            else if // is it the pool's request for stopping a thread?
                    (auto m = cast(CaldronThreadPoolAsksDispatcherToJoinThread_msg)msg)
            {
                (cast()m.thread).join;
            }
            else if // is that client's request for circle's Tid?
                    (auto m = cast(immutable UserRequestsCircleTid_msg)msg)
            {   //yes: create new attention circle thread and send back its Tid
                Tid clientTid = cast()m.senderTid();

                // Create and start an attention circle thread if it doesn't exist yet, send back its Tid.
                Tid circleTid;
                if      // is client in the register already?
                        (auto circleThread = clientTid in circleRegister_)
                {   //yes: take the circle's Tid from the register
                    circleTid = circleThread.tid;
                }
                else {  //no: create the circle, tell him the client's Tid and put the pair in the circle register
                    auto atnCircle = new AttentionCircle;
                    _threadPool_.addThreadBatch;   // to guarantee that the pool is not empty
                    CaldronThread circleThread = _threadPool_.pop(atnCircle);
                    atnCircle.myThread = circleThread;
                    circleRegister_[clientTid] = circleThread;
                    circleThread.tid.send(new immutable DispatcherProvidesCircleWithUserTid_msg(clientTid));
                }
            }
            else if // TerminateAppMsg message has come?
                    (cast(TerminateApp_msg)msg) // || var.hasValue)
            {   //yes: terminate me and all my subthreads
                // send terminating message to all circles
                foreach(circle; circleRegister_.byValue){
                    circle.tid.send(new immutable TerminateApp_msg);
                    while(circle.tid in _threadPool_) {
                        Thread.sleep(SPIN_WAIT);
                    }   // wait while terminated
                }

                send(cast()_mainTid_, new immutable CirclesAreFinished_msg);    // tell to main func

                // terminate itself
                goto FINISH_THREAD;
            }
            else {  // unrecognized message of type Msg. Log it.
                logit(format!"Unexpected message to the attention dispatcher thread: %s"(typeid(msg)));
            }
        }
        else if // exception message?
                (ex)
        {   // rethrow exception
            foreach(circle; circleRegister_.byValue){
                circle.tid.send(new immutable TerminateApp_msg);
            }
            throw ex;
        }
        else if // has come an unexpected message?
                (var.hasValue)
        {   // log it
            if(var.type == typeid(OwnerTerminated))
                send(thisTid, new immutable TerminateApp_msg);

            logit(format!"Unexpected message of type Variant to the attention dispatcher thread: %s"(var.toString));
        }
    }

    FINISH_THREAD:
} catch(Throwable e) { ownerTid.send(cast(shared)e); } }

//###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
//
//                               Private
//
//###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

//---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

/// Circle's Tids by client's Tids.
private CaldronThread[Tid] circleRegister_;

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
