module attn_circle_thread;
import core.thread, std.concurrency;
import std.conv;
import std.format;

import global, tools;
import cpt_abstract, cpt_concrete;
import crank_pile;
import messages;
debug = 1;

/**
            Main work horse of the system. It provides the room for doing reasoning on some branch.
    This class, as well as it successors, must be shared, i.e. used for creating of shared objects. It must be a shared object
    to be able to provide threads a thread function (entry point for them).
*/
class Caldron {

    /**
            Constructor.
        Parameters:
            seedCid = seed
            parentBreedCid = cid of the parent's breed concept, only for a caldron. If supplied, its incarnation in this caldron's
                             space name will be set up in the caldron constructor, so we will be able to address the parent caldron
                             on the conceptual level.
    */
    this(Cid seedCid, Cid parentBreedCid = 0) {
        if(parentBreedCid) {
            debug _checkCid_!HolyBreed(parentBreedCid);
            (cast(Breed)_cpt_(parentBreedCid))._tid_ = ownerTid;     // finish setting up the parent breed
        }
        debug _checkCid_!HolySeed(seedCid);

        headCid_ = seedCid_ = seedCid;
        thisTid.send(new immutable StartReasoningMsg);
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
                Get concept by cid.
            If concept is present in the live map, get it from there, if not, generate it based on the holy concept and put it
        in the live map.
        Parameters:
            cid = cid of the concept or its correspondig enum.
        Returns: the live concept object
    */
    Concept _cpt_(Cid cid) {
        assert(cid in _hm_);
        if
                (auto p = cid in lm_)
            return *p;
        else
            return lm_[cid] = _hm_[cid].live_factory;
    }

    /// Adapter
    Concept _cpt_(CptDescriptor cd) {
        return _cpt_(cd.cid);
    }

    /// Send children the termination signal and wait their termination.
    void _terminateChildren_() {
        foreach (child; childCaldrons_)
            child.send(new immutable TerminateAppMsg);
    }

    // Debug return the caldron seed's name, if exist, else word "noname" will be returned.
    debug {
        string _seedName_() {
            if (auto nmp = seedCid_ in _nm_)
                return *nmp;
            else
                return "noname";
        }
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

    private Cid seedCid_;

    /// Caldrons, that were parented here.
    private Tid[] childCaldrons_;

    /// Live map. All holy concepts, that are ever addressed by the caldron are wrapped in corresponding live object and
    /// put in this map. So, caldron always works with its own instance of a concept.
    private Concept[Cid] lm_;

    /// The head of the branch. It is cid of the seed neuron.
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
        debug(2)
            logit(_seedName_ ~ ":");
        while(true) {
            debug(3){
                HolyConcept cpt = cast()_hm_[headCid_];
                logit(format!"    headCid_: %s, %s: %s"(headCid_, to!string(typeid(cpt)), _nm_[headCid_]));
            }
            else
                debug(2) logit(format!"    headCid_: %s, %s"(headCid_, _nm_[headCid_]));

            Neuron head = cast(Neuron)_cpt_(headCid_);

            // Let the head be processed, determine effects and do the actions.
            auto effect = head.calculate_activation_and_get_effects;
            foreach(actCid; effect.actions) {
                Action act = cast(Action)_cpt_(actCid);
                // TODO: do the action
            }

            // May be stop
            if      // was stop required by an action or is there no new head?
            (stopAndWait_ || effect.branches.length == 0)
                goto STOP_AND_WAIT;

            // Set up new head and may be start new caldrons
            assert(cast(Neuron)_cpt_(effect.branches[0]), "Cid: " ~ to!string(effect.branches[0]) ~
            " new head must be a Neuron and it is " ~ to!string(typeid(_cpt_(effect.branches[0]))));
            headCid_ = effect.branches[0];     // the first branch in the list is the new head
            foreach(cid; effect.branches[1..$]) {
                assert(cast(Seed)_cpt_(cid) || cast(Breed)_cpt_(cid),
                        format!"Cid: %s, this concept must be of Seed or Breed type, not of %s."
                                (cid, typeid(_cpt_(cid))));
                if      // is it a seed?
                (cast(Seed)_cpt_(cid))
                {   //yes: spawn an anonymous branch
                    childCaldrons_ ~= spawn(&caldron_thread_func, false, cid, 0);
                }
                else
                {   //no: it is a breed. Spawn an identified branch
                    auto breed = cast(Breed)_cpt_(cid);
                    Cid seedCid = breed._seed_;
                    assert(cast(Seed)_cpt_(seedCid));
                    Tid tid = spawn(&caldron_thread_func, false, seedCid, breed._parentBreed_);
                    breed._tid_ = tid;        // save the Tid in the breed concept in this name space
                    childCaldrons_ ~= tid;
                }
            }
        }

    STOP_AND_WAIT:
    debug(2)
        logit("    stop and wait on " ~ _seedName_);
    }

    /// Ditto.
    private Concept cpt_(string name) {
        assert(name in _nm_ && _nm_[name] in _hm_);
        return _cpt_(_nm_[name]);
    }
}

/// This class immeadiately works with the attention cilent. It creates a tree of caldrons as it works and it is theroot
/// of that tree.
class AttentionCircle: Caldron {

    /**
            Constructor.
    */
    this() {

        // Setup chat_breed
        Breed breed = cast(Breed)_cpt_(Chat.chat_breed);
        breed._tid_ = thisTid;

        super(breed._seed_);    // use standard seed for starting a chat
    }

    /// Ditto.
    protected override bool _msgProcessing(immutable Msg msg) {

        if (super._msgProcessing(msg))
            return true;
        else if      // is it a Tid of the client sent by Dispatcher?
                (auto m = cast(immutable DispatcherSuppliesCircleWithClientTid)msg)
        {   //yes: accept the Tid
            clientTid_ = cast()m.sender_tid;
            return true;
        }
        else
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
        seedCid = seed, only for a caldron
        parentBreedCid = cid of the parent's breed concept, only for a caldron.
*/
void caldron_thread_func(bool calledByDispatcher, Cid seedCid = 0, Cid parentBreedCid = 0) {try{

    // Create caldron
    if      //is dispatcher spawning an attention circle?
            (calledByDispatcher)
    {   //yes: create attention circle
        caldron_ = new AttentionCircle();
        assert(seedCid == 0);
    }
    else//no: it was a caldron
    {
        caldron_ = new Caldron(seedCid, parentBreedCid);
    }

    // Receive messages in a cycle
    while(true) {
        import std.variant: Variant;

        immutable Msg msg;
        Throwable ex;
        Variant var;    // the catchall type

        receive(
            (immutable Msg m) {(cast()msg) = cast()m;},
            (shared Throwable e){ex = cast()e;},
            (Variant v) {var = v;}          // the catchall clause
        );

        // Recognize and process the message
        if      // is it a regular message?
                (msg)
        {   // process it
            //Send the message to caldron
            if // processed by caldron?
            (caldron_._msgProcessing(msg))
            //yes: go for a new message
                continue ;
            else if // is it a request for the circle termination?
            (cast(TerminateAppMsg)msg)
            {   //yes: terminate me and all my subthreads
                debug(2)
                    logit("terminating " ~ caldron_._seedName_);
                caldron_._terminateChildren_;

                // terminate itself
                goto FINISH_THREAD;
            }
            else
            {  // unrecognized message of type Msg. Log it.
                logit(format!"Unexpected message to the caldron thread: %s"(typeid(msg)));
                continue ;
            }
        }
        else if // exception message?
                (ex)
        {   // rethrow exception
            throw ex;
        }
        else if
                (var.hasValue)
        {  // unrecognized message of type Variant. Log it.
            logit(format!"Unexpected message of type Variant to the caldron thread: %s"(var.toString));
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
