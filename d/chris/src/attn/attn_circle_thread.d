module attn.attn_circle_thread;
import std.concurrency;
import std.format;

import tools;
import messages;

/**
            Main work horse of the system. It provides the room for doing reasoning on some branch.
    This class, as well as it successors, must be shared, i.e. used for creating of shared objects. It must be a shared object
    to be able to provide threads a thread function (entry point for them).
*/
class Caldron {

    /**
            Constructor.
    */
    this() {}


protected:

    /**
            Message processing for caldron. All of the caldron workflow starts here.
        Parameters:
            msg = message from another caldron or the outside world including messages from user, e.g. lines from console
                  and service messages, like TerminateAppMsg for termination.
        Returns: true if the message was recognized, else false.

    */
    bool msgProcessing(immutable Msg msg) {
        return true;
    }

    /// Use to check if it is a circle or a caldron, rather than doing cast(AttentionCircle). This way it's gona be faster.
    bool _iAmCircle_ = false;
}

/// This class immeadiately works with the attention cilent. It creates a tree of caldrons as it works and it is theroot
/// of that tree.
class AttentionCircle: Caldron {

    /**
            Constructor.
        Parameters:
            clientTid = Tid of the client of this attention circle.
    */
    this(Tid clientTid) {
        super();
        _iAmCircle_ = true;
        clientTid_ = clientTid;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    private:
    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

    /// Tid of the correspondent's thread
    Tid clientTid_;

    //---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
}

/**
        Thread function for caldron and attention circle as its successor.
*/
void attn_circle_thread() {try{

    // Receive messages in a cycle
    while(true) {
        import std.variant: Variant;

        Msg msg;
        Variant var;

        // Receive new message
        receive(
        (immutable Msg m) {msg = cast()m;},
        (Variant v) {var = v;}
        );

        // Recognize and process the message
        if (msg)
        {   // TODO: a lot to do here
            if      // is it Tid of the client sent by Dispatcher?
            (auto m = cast(immutable DispatcherSuppliesCircleWithClientTid)msg)
            {   //yes: create the attention circle object
                Tid clientTid = cast()m.sender_tid;
                if      // circle is not created yet?
                (caldron_ is null)
                {
                    caldron_ = new AttentionCircle(clientTid);
                }
                assert(caldron_._iAmCircle_);
            }
            else if // TerminateAppMsg message has come?
            (cast(TerminateAppMsg)msg) // || var.hasValue)
            {   //yes: terminate me and all my subthreads
                // TODO: send terminating messages to all caldrons
                //foreach(cir; attnDisp_.tidCross_.circles){
                //    cir.send(new immutable TerminateAppMsg);
                //}

                // terminate itself
                goto FINISH_THREAD;
            }
        }
        else {  // unrecognized message of type Msg. Log it.
            logit(format!"Unexpected message to the caldron thread: %s"(msg));
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

/// The attention circle object
Caldron caldron_;

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
