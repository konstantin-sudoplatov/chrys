module attn_circle_thread;
import core.thread, std.concurrency;
import std.format;

import global, tools;
import cpt_abstract, cpt_concrete;
import crank_pile;
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
            seedCid = either seed or breed concept
    */
    this(Cid seedCid) {
        assert(cast(Seed)cpt_(seedCid) || cast(Breed)cpt_(seedCid));
        this.headCid_ = seedCid;
        thisTid.send(new immutable StartReasoningMsg);
    }

    void terminate_children() {
        foreach(child; childCaldrons_)
            child.send(new immutable TerminateAppMsg);
        thread_joinAll;
    }

    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //                                 Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$--- data ---$$$---$$$---$$$---$$$---$$$--

    //---$$$---$$$---$$$---$$$---$$$--- functions ---$$$---$$$---$$$---$$$---$$$---

    /**
            Message processing for caldron. All of the caldron workflow starts here.
        Parameters:
            msg = message from another caldron or the outside world including messages from user, e.g. lines from console
                  and service messages, like TerminateAppMsg for termination.
        Returns: true if the message was recognized, else false.

    */
    protected bool _msgProcessing(immutable Msg msg) {

        if      // is it a request for starting reasoning?
                (cast(StartReasoningMsg)msg)
        {
            reasoning_;
            return true;
        }
        return false;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

    /// Caldrons, that were parented here.
    private Tid[] childCaldrons_;

    /// Live map. All holy concepts, that are ever addressed by the caldron are wrapped in corresponding live object and
    /// put in this map. So, caldron always works with its own instance of a concept.
    private Concept[Cid] lm_;

    /// The head of the branch. It is cid of seed neuron.
    private Cid headCid_;

    /// Stop and wait flag. Can be raised by actions.
    private bool stopAndWait_ = false;

    //---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

    /**
            Does the cycle of assessments. When the assessment chain cannot be continued, because it has to wait for something,
       for example results from other caldrons or a reaction of the chatter, this function returns and this loop goes to
       processing other events in the queue or waits if the event queue is empty.
     */
    private void reasoning_() {
        while(true) {
            Neuron head = cast(Neuron)cpt_(headCid_);
            if      // isn't the head neuron ready?
                    (head.is_down)
                //no: stop
                goto STOP_AND_WAIT;

            // Let the head be processed, determine effects and do the actions.
            auto effect = head.calculate_activation_and_get_effects;
            foreach(actCid; effect.actions) {
                Action act = cast(Action)cpt_(actCid);
                // TODO: do the action
            }

            // May be stop
            if      // was stop required by an action or is there no new head?
                    (stopAndWait_ || effect.branches.length == 0)
                goto STOP_AND_WAIT;

            // Set up new head and may be start new caldrons
            headCid_ = effect.branches[0];     // the first branch in the list is the new head
            int len = cast(int)effect.branches.length;
            foreach(cid; effect.branches[1..$]) {
                assert(cast(Seed)cpt_(cid) || cast(Breed)cpt_(cid));
                if      // is it a seed?
                        (cast(Seed)cpt_(cid))
                {   //yes: spawn an anonymous branch
                    childCaldrons_ ~= spawn(&caldron_thread_func, false, cid);
                }
                else
                {   //no: it is a breed. Spawn an identified branch
                    auto breed = cast(Breed)cpt_(cid);
                    Cid seedCid = breed.seed_cid;
                    assert(cast(Seed)cpt_(seedCid));
                    Tid tid = spawn(&caldron_thread_func, false, seedCid);
                    breed.tid = tid;        // save the Tid in the breed concept in this name space
                    childCaldrons_ ~= tid;
                }
            }
        }
    STOP_AND_WAIT:
    }

    /**
                Get concept by cid.
            If concept is present in the live map, get it from there, if not, generate it based on the holy concept and put it
        in the live map.
        Parameters:
            cid = cid of the concept or its correspondig enum.
        Returns: the live concept object
    */
    private Concept cpt_(Cid cid) {
        assert(cid in _hm_);
        if
                (auto p = cid in lm_)
            return *p;
        else
            return _hm_[cid].live_factory;
    }

    /// Ditto.
    private Concept cpt_(string name) {
        assert(name in _nm_ && _nm_[name] in _hm_);
        return cpt_(_nm_[name]);
    }
}

/// This class immeadiately works with the attention cilent. It creates a tree of caldrons as it works and it is theroot
/// of that tree.
class AttentionCircle: Caldron {

    /**
            Constructor.
        Parameters:
            clientTid = Tid of the client of this attention circle.
            seedCid = seed to start the circle
    */
    this(Cid seedCid) {
        super(seedCid);    // use standard seed for starting a chat
    }

    /// Ditto.
    protected override bool _msgProcessing(immutable Msg msg) {

        if      // is it a Tid of the client sent by Dispatcher?
                (auto m = cast(immutable DispatcherSuppliesCircleWithClientTid)msg)
        {   //yes: accept the Tid
            clientTid_ = cast()m.sender_tid;
            return true;
        }

        return false;
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
        Thread function for caldrons and the attention circle.
    Parameters:
        calledByDispatcher = the caller, true - the dispatcher, false - a caldron
        seed = seed or breed (only seed for the attention circle)
*/
void caldron_thread_func(bool calledByDispatcher, Cid seedCid) {try{

    // Create caldron
    if      //is dispatcher spawning an attention circle?
            (calledByDispatcher)
    {   //yes: create attention circle
        caldron_ = new AttentionCircle(seedCid);
        assert(cast(Seed)caldron_.cpt_(seedCid));
    }
    else//no: it was a caldron
    {
        caldron_ = new Caldron(seedCid);
        assert(cast(Seed)caldron_.cpt_(seedCid) || cast(Breed)caldron_.cpt_(seedCid));
    }

    // Receive messages in a cycle
    while(true) {
        import std.variant: Variant;

        immutable Msg msg;

        // When debugging, catch any mesages, which are not of the Msg type and log them
        debug
            Variant var;    // the catchall type

        // Receive new message
        debug
            receive(
                (immutable Msg m) {(cast()msg) = cast()m;},
                (Variant v) {var = v;}          // the catchall clause
            );
        else
            receive(
                (immutable Msg m) {(cast()msg) = cast()m;}
            );

        debug   // Log unexpected message
            if(var.hasValue) {  // unrecognized message of type Variant. Log it.
                logit(format!"Unexpected message to the caldron thread: %s"(var.toString));
                continue;
            }

        assert(msg, "The message must not be null here.");

        //Send the message to caldron
        if      // processed by caldron?
                (caldron_._msgProcessing(msg))
            //yes: go for a new message
            continue;
        else if // is it a request for the circle termination?
                (cast(TerminateAppMsg)msg)
        {   //yes: terminate me and all my subthreads
            caldron_.terminate_children;

            // terminate itself
            goto FINISH_THREAD;
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
