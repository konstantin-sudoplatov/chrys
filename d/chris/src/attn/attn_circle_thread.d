module attn_circle_thread;
import std.concurrency;
import std.format;

import global, tools;
import cpt_holy_abstract, cpt_holy;
import cpt_live_abstract, cpt_live;
import messages;

/**
            Main work horse of the system. It provides the room for doing reasoning on some branch.
    This class, as well as it successors, must be shared, i.e. used for creating of shared objects. It must be a shared object
    to be able to provide threads a thread function (entry point for them).
*/
class Caldron {

    /**
            Constructor.
        Parameters:
            seed = the seed concept of the caldron
    */
    this(Cid seed) {
        tid_ = cast(immutable)thisTid;      // catch the Tid
        this.head_ = seed;
    }

    /// Getter.
    @property const(Tid) tid() const {
        return tid_;
    }

protected:

    /**
            Message processing for caldron. All of the caldron workflow starts here.
        Parameters:
            msg = message from another caldron or the outside world including messages from user, e.g. lines from console
                  and service messages, like TerminateAppMsg for termination.
        Returns: true if the message was recognized, else false.

    */
    bool msgProcessing(immutable Msg msg) {
        if // is it a request for starting reasoning?
                (cast(StartReasoningMsg)msg)
        {
            reasoning_;
            return true;
        }
        return false;
    }

    /// Used to check if it is a circle or a caldron, rather than doing cast(AttentionCircle). This way it's gona be faster.
    bool _iAmCircle = false;


    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

    /// Tid of the caldron.
    immutable Tid tid_;

    /// Live map. All holy concepts, that are ever addressed by the caldron are wrapped in corresponding live object and
    /// put in this map. So, caldron always works with its own instance of a concept.
    private Concept[Cid] lm_;

    /// The head of the branch.
    private Cid head_;

    //---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

    /**
            Does the cycle of assessments. When the assessment chain cannot be continued, because it has to wait for something,
       for example results from other caldrons or a reaction of the chatter, this function returns and this loop goes to
       processing other events in the queue or waits if the event queue is empty.
     */
    void reasoning_() {

    }

    /**
                Get concept by cid.
            If concept is present in the live map, get it from there, if not, generate it based on the holy concept and put it
        in the live map.
        Parameters:
            cid = cid of the concept or its correspondig enum.
        Returns: the live concept object
    */
    Concept cpt(Cid cid) {
        assert(cid in _hm_);
        if
                (auto p = cid in lm_)
            return *p;
        else
            return _hm_[cid].live_factory;
    }

    /// Ditto.
    Concept cpt(string name) {
        assert(name in _nm_ && _nm_[name] in _hm_);
        return cpt(_nm_[name]);
    }
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
        super(GlobalConcepts.chat_seed);    // use standard seed for starting a chat
        _iAmCircle = true;
        clientTid_ = clientTid;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

    /// Tid of the correspondent's thread
    private Tid clientTid_;

    //---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
}

/**
        Thread function for caldron and attention circle as its successor.
*/
void attn_circle_thread_func() {try{

    // Receive messages in a cycle
    while(true) {
        import std.variant: Variant;

        immutable Msg msg;
debug   Variant var;

        // Receive new message
debug   receive(
            (immutable Msg m) {(cast()msg) = cast()m;},
            (Variant v) {var = v;}
        );
else    receive(
            (immutable Msg m) {(cast()msg) = cast()m;}
        );

debug   if(var.hasValue) {  // unrecognized message of type Variant. Log it.
            logit(format!"Unexpected message to the caldron thread: %s"(var.toString));
            continue;
        }

        // Recognize and process the message
        assert(msg, "The message must not be null here.");
        if      // caldron is created yet?
                (caldron_ !is null)
        {
            if      // processed by caldron?
                    (caldron_.msgProcessing(msg))
                continue;
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
        else    //no: check if we can create it
            if // is it the Tid of the client sent by Dispatcher?
                    (auto m = cast(immutable DispatcherSuppliesCircleWithClientTid)msg)
            {   //yes: create the attention circle object
                Tid clientTid = cast()m.sender_tid;
                caldron_ = new AttentionCircle(clientTid);
                assert(caldron_._iAmCircle);
                continue;
            }
            else {
                // TODO: check if we can create a regular caldron
            }

debug   {  // unrecognized message of type Msg. Log it.
            logit(format!"Unexpected message to the caldron thread: %s"(msg));
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

//---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

/// The attention circle/caldron object, thead local.
private Caldron caldron_;

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
