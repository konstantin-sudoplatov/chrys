module attn.attn_dispatcher_thread;
import std.concurrency;
import std.format;

import tools;
import global_data;
import messages;
import attn.attn_circle_thread;

/**
        Thread function for attention dispatcher.
*/
void attention_dispatcher_thread() {try {   // catchall try block for catching flying exceptions and forwarding them to the owner thread.

    // Receive messages in a cycle
    while(true) {
        import std.variant: Variant;

        Msg msg;                    // regular message
        Variant var;                // the catchall type

        // Receive new message
        receive(
            (immutable Msg m){msg = cast()m;},
            (Variant v){var = v;}
        );

        // Recognize and process the message
        if      // is it a regular message?
                (msg)
        {   // process it
            if      // does client request circle's Tid?
                    (auto m = cast(immutable ClientRequestsCircleTidFromDisp)msg)
            {   //yes: create new attention circle thread and send back its Tid
                Tid clientTid = cast()m.sender_tid();

                // the first time create the attention dispatcher object
                if
                        (!attnDisp_)
                    attnDisp_ = new AttentionDispatcher();

                // spawn the circle, cross it and send to it client's Tid
                Tid circleTid = attnDisp_.createCircleAttentionThread(clientTid);

                // give the client the correspondent's Tid
                (clientTid).send(new immutable DispatcherSuppliesClientWithCircleTid(circleTid));

                continue;
            }
            else if // TerminateAppMsg message has come?
                    (cast(TerminateAppMsg)msg) // || var.hasValue)
            {   //yes: terminate me and all my subthreads
                // send terminating message to all circles
                foreach(cir; attnDisp_.circleRegister_.byValue){
                    cir.send(new immutable TerminateAppMsg);
                }

                // terminate itself
                goto FINISH_THREAD;
            }
            else {  // unrecognized message of type Msg. Log it.
                logit(format!"Unexpected message to the attention dispatcher thread: %s"(msg));
            }
        }
        else if // has come an unexpected message?
        (var.hasValue)
        {   // log it
            logit(format!"Unexpected message to the attention dispatcher thread: %s"(var.toString));
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

/// The attention dispatcher object
AttentionDispatcher attnDisp_;

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--

/**
        This class contain functionality on creation attention circle threads and linking them to the clients. All workflow starts
    in the thread function. Dispatcher doesn't have direct access of the circles. They are located in the thread-local memory
    of thir threads and cannot be accessed from the outside. All interconnections are through the message system.
*/
class AttentionDispatcher {

    //---***---***---***---***---***--- types ---***---***---***---***---***---***

    //---***---***---***---***---***--- data ---***---***---***---***---***--

    this(){}

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                            Public
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    private:
    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

    /// AA of circle's Tids by client's Tids.
    Tid[Tid] circleRegister_;

    //---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

    /**
                    Create and start an attention circle thread if it doesn't exist yet and return its Tid.
        Parameters:
            clientTid = Tid of the client, that corresponds with that circle.

        Returns: circle's Tid.
    */
    Tid createCircleAttentionThread(Tid clientTid) {
        if      // is client in the cross already?
                (auto circleTid = clientTid in circleRegister_)
        {   //yes: return the Tid
            return cast()*circleTid;
        }
        else {  //no: create the circle, tell him the client's Tid and put the pair in the circle register
            Tid circleTid = spawn(&attn_circle_thread);
            circleTid.send(new immutable DispatcherSuppliesCircleWithClientTid(clientTid));
            circleRegister_[clientTid] = circleTid;

            return circleTid;
        }
    }

    //---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
}
