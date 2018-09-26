module attn_dispatcher_thread;
import std.concurrency;
import std.format;

import tools_pile;
import global;
import messages;
import crank_pile;
import attn_circle_thread;

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
            if      // is that client's request for circle's Tid?
                    (auto m = cast(immutable UserRequestsCircleTid_msg)msg)
            {   //yes: create new attention circle thread and send back its Tid
                Tid clientTid = cast()m.senderTid();

                // Create and start an attention circle thread if it doesn't exist yet, send back its Tid.
                Tid circleTid;
                if      // is client in the register already?
                        (auto circleTidPtr = clientTid in circleRegister_)
                {   //yes: take the circle's Tid from the register
                    circleTid = *circleTidPtr;
                }
                else {  //no: create the circle, tell him the client's Tid and put the pair in the circle register
                    circleTid = spawn(&caldron_thread_func, true, 0);
                    circleTid.send(new immutable DispatcherSuppliesCircleWithUserTid_msg(clientTid));
                    circleRegister_[clientTid] = circleTid;
                }
                continue;
            }
            else if // TerminateAppMsg message has come?
                    (cast(TerminateApp_msg)msg) // || var.hasValue)
            {   //yes: terminate me and all my subthreads
                // send terminating message to all circles
                foreach(cir; circleRegister_.byValue){
                    cir.send(new immutable TerminateApp_msg);
                }

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
            throw ex;
        }
        else if // has come an unexpected message?
                (var.hasValue)
        {   // log it
            if(var.type == typeid(OwnerTerminated))
                send(thisTid, new immutable TerminateApp_msg);

            logit(format!"Unexpected message of type Variant to the attention dispatcher thread: %s"(var.toString));
            continue;
        }
    }

    FINISH_THREAD:
} catch(Throwable e) { ownerTid.send(cast(shared)e); } }

//###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
//
//                               Private
//
//###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
private:
//---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

/// Circle's Tids by client's Tids.
Tid[Tid] circleRegister_;

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
