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
    shared this() {}

    /**
                    Thread function for caldron and attention circle as its successor.
    */
    shared final void thread_function() {try{
        import std.variant: Variant;

        Msg msg;
        Variant var;

        while(true) {
            receive(
                (immutable Msg m) {msg = cast()m;},
                (Variant v) {var = v;}
            );

            if (msg)
            {   // TODO: a lot to do here

            }
            else {  // unrecognized message of type Msg. Log it.
                logit(format!"Unexpected message to the caldron thread: %s"(msg));
            }
        }
    FINISH_THREAD:
    } catch(Throwable e) { ownerTid.send(cast(shared)e); } }

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
}

/// This class immeadiately works with the attention cilent. It creates a tree of caldrons as it works and it is theroot
/// of that tree.
class AttentionCircle: Caldron {

    /**
                    Constructor.
        Parameters:
            clientTid = client's Tid of the client.
    */
    shared this(Tid clientTid) {
        super();
        clientTid_ = cast(shared)clientTid;
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
