module attn_circle_thread;
import core.thread, std.concurrency;
import std.conv;
import std.format;

import global, tools;
import cpt_abstract, cpt_pile, cpt_neurons, cpt_premises, cpt_actions;
import crank_pile;
import messages;
debug = 2;

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

        // Move own tid to the conceptual level
        (cast(TidPrimitive)_cpt_(CommonConcepts.myTid_tidprim))._tid_ = thisTid;

        if(parentBreedCid) {
            debug _checkCid_!SpBreed(parentBreedCid);
            (cast(Breed)_cpt_(parentBreedCid))._tid_ = ownerTid;     // finish setting up the parent breed
            debug parentBreedCid_ = parentBreedCid;
        }
        debug _checkCid_!SpSeed(seedCid);

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
    final Concept _cpt_(Cid cid) {
        assert(cid in _hm_);
        if
                (auto p = cid in lm_)
            return *p;
        else
            return lm_[cid] = _hm_[cid].live_factory;
    }

    /// Adapter
    final Concept _cpt_(CptDescriptor cd) {
        return _cpt_(cd.cid);
    }

    /// Request exiting from the reasoning_() function. This flag is lower on coming next StartReasoningMsg message.
    final void _requestStopAndWait_() {
        stopAndWait_ = true;
    }

    /// Send children the termination signal and wait their termination.
    final void _terminateChildren_() {
        foreach (child; childCaldrons_)
            child.send(new immutable TerminateAppMsg);
    }

    // Debug return the caldron seed's name, if exist, else word "noname" will be returned.
    debug {
        final string _seedName_() {
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

        if (cast(StartReasoningMsg)msg) {    // is it a request for starting reasoning?
            // kick off the reasoning loop
            reasoning_;
            return true;
        }
        else if (cast(UserSaysToCircleMsg)msg) {

        }
        return false;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

    /// Seed for this caldron.
    private Cid seedCid_;

    /// Parent breed, usually used to send the parent messages
    debug
        private Cid parentBreedCid_;

    /// Caldrons, that were parented here.
    private Tid[] childCaldrons_;

    /// Live map. All holy concepts, that are ever addressed by the caldron are wrapped in corresponding live object and
    /// put in this map. So, caldron always works with its own instance of a concept.
    private Concept[Cid] lm_;

    /// The head of the branch. It is cid of the seed neuron.
    private Cid headCid_;

    /// Stop and wait flag. Can be raised by actions. It is lowered on entering the reasoning_() function, i.e. the StartReasoningMsg
    /// will always lower it.
    private bool stopAndWait_ = false;

    //---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

    /**
            Does the cycle of assessments. When the assessment chain cannot be continued, because it has to wait for something,
       for example results from other caldrons or a reaction of the chatter, this function returns and this loop goes to
       processing other events in the queue or waits if the event queue is empty.
     */
    private void reasoning_() {
        stopAndWait_ = false;
        debug(2)
            logit(_seedName_ ~ ":");
        while(true) {
            debug(3){
                SpiritConcept cpt = cast()_hm_[headCid_];
                logit(format!"    headCid_: %s, %s: %s"(headCid_, to!string(typeid(cpt)), _nm_[headCid_]));
            }
            else
                debug(2) logit(format!"    headCid_: %s, %s"(headCid_, _nm_[headCid_]));

            Neuron head = cast(Neuron)_cpt_(headCid_);

            // Let the head be processed, determine effects and do the actions.
            auto effect = head.calculate_activation_and_get_effects;
            foreach(actCid; effect.actions) {
                debug(2) {
                    if (auto ps = actCid in _nm_)
                        logit(format!"    action: %s"(_nm_[actCid]));
                    else
                        logit(format!"    action cid: %s, noname"(actCid));
                }
                Action act = cast(Action)_cpt_(actCid);
                act._do_(this);
            }

            // May be stop
            if      // was stop required by an action or is there no new head?
                    (stopAndWait_ || effect.branches.length == 0)
                goto STOP_AND_WAIT;

            // Set up the new head
            _checkCid_!Neuron(this, effect.branches[0]);
            headCid_ = effect.branches[0];     // the first branch in the list is the new head

            // May be start new caldrons
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
                {   //no: it is a breed. Spawn a breeded branch
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

        super(breed._seed_, 0);    // no parent
    }

    /// Ditto.
    protected override bool _msgProcessing(immutable Msg msg) {

        if (super._msgProcessing(msg))
            return true;
        else if      // is it a Tid of the client sent by Dispatcher?
                (auto m = cast(immutable DispatcherSuppliesCircleWithClientTid)msg)
        {   //yes: accept the Tid and move it to the concept level
            (cast(TidPrimitive)_cpt_(CommonConcepts.userThread_tidprim))._tid_ = cast()m._senderTid_;
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

    debug(2) {
        string s;
        if      // is it a parentless branch?
                (caldron_.parentBreedCid_ == 0)
            s = format!"starting caldron %s as a parentless branch"(caldron_._seedName_);
        else    //no: it is a breeded branch
            if      // is it a named branch (the breed concept has a name)?
                    (auto ps = caldron_.parentBreedCid_ in _nm_)
            {   //yes: it is a breeded named branch
                s = format!"starting caldron %s, parent breed: %s"
                        (caldron_._seedName_, *ps);
            }
            else    //no: it is a breeded noname branch (the breed concept doesn't have a name)
                s = format!"starting caldron %s, noname parent breed, parent breed cid: %s"
                        (caldron_._seedName_, caldron_.parentBreedCid_);
        logit(s);
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
                    logit("terminating caldron " ~ caldron_._seedName_);
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
