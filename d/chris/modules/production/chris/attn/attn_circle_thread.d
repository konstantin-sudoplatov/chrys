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
            breedOrSeedCid = breed or seed concept
    */
    this(Cid breedOrSeedCid) {
        assert(cast(Seed)this[breedOrSeedCid] || cast(Breed)this[breedOrSeedCid],
                format!"Cid: %s, this concept must be of Seed or Breed type, not of %s."
                        (breedOrSeedCid, typeid(this[breedOrSeedCid])));

        if      // is it a breed?
                (auto breed = cast(Breed)this[breedOrSeedCid])
        {   //yes: setup the caldron's instance of breed and the caldron's head
            breed.tid = thisTid;
            breed.activate;         // the local instance of the breed is setup and ready
            headCid_ = seedCid_ = breed.seed;
        }
        else//no: it is a seed, take it
            headCid_ = seedCid_ = breedOrSeedCid;

        // Kick off the reasoning cycle
        thisTid.send(new immutable StartReasoningMsg);
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
                Get live concept by cid, overload of the [] operator
            If concept is present in the live map, get it from there, if not, generate it based on the holy concept and put it
        in the live map.
        Parameters:
            cid = cid of the concept or its correspondig enum.
        Returns: the live concept object
    */
    final Concept opIndex(Cid cid) {
        assert(cid in _hm_, format!"Cid %s(%s) is not in the holy map."(cid, _nm_.name(cid)));
        if
                (auto p = cid in lm_)
            return *p;
        else
            return lm_[cid] = _hm_[cid].live_factory;
    }

    /// Adapter
    final Concept opIndex(CptDescriptor cd) {
        return this[cd.cid];
    }

    /**
            Assign a concept to the local map. Overload for the index assignment operation. Used for injection of concepts,
        gotten via messages.
        Parameters:
            cpt = concept to assign
        Usage:
        ---
        caldron[] = cpt
        ---
    */
    final Concept opIndexAssign(Concept cpt) {
        return lm_[cpt.cid] = cpt;
    }

    /// Request exiting from the reasoning_() function. This flag is lower on coming next StartReasoningMsg message.
    final void requestStopAndWait() {
        stopAndWait_ = true;
    }

    /// Send children the termination signal and wait their termination.
    final void terminateChildren() {
        foreach (child; childCaldrons_)
            child.send(new immutable TerminateAppMsg);
    }

    // Debug return the caldron's name (based on the seed), if exist, else word "noname" will be returned.
    debug {
        final string caldName() {
            import std.array: replace;

            if (auto nmp = seedCid_ in _nm_)
                return (*nmp).replace("_seed", "");
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
        {   // kick off the reasoning loop
            reasoning_;
            return true;
        }
        else if // A concept was posted by another caldron?
                (auto m = cast(immutable SingleConceptPackageMsg)msg)
        {   //yes: clone it and inject into the current name space (may be with overriding)
            Concept cpt = m.cpt.clone;
            this[] = cpt;       // inject
            reasoning_;         // kick
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
        debug(2)    // print the branch(seed) name
            logit("entering " ~ caldName);
        while(true) {
            debug(2) logit(format!"%s, headCid_: %s, %s"(caldName, headCid_, _nm_[headCid_]));

            Neuron head = cast(Neuron)this[headCid_];

            // Let the head be processed, determine effects and do the actions.
            auto effect = head.calculate_activation_and_get_effects(this);
            foreach(actCid; effect.actions) {
                debug(2) {
                    if (auto ps = actCid in _nm_)
                        logit(format!"%s, action: %s"(caldName, _nm_[actCid]));
                    else
                        logit(format!"%s, action cid: %s, noname"(caldName, actCid));
                }
                Action act = cast(Action)this[actCid];
                act._do_(this);
            }

            // May be stop
            if      // was stop required by an action or is there no new head?
                    (stopAndWait_ || effect.branches.length == 0)
                goto STOP_AND_WAIT;

            // Set up the new head
            checkCid!Neuron(this, effect.branches[0]);
            headCid_ = effect.branches[0];     // the first branch in the list is the new head

            // May be start new caldrons
            foreach(cid; effect.branches[1..$]) {
                assert(cast(Seed)this[cid] || cast(Breed)this[cid],
                        format!"Cid: %s, this concept must be of Seed or Breed type, not of %s."
                                (cid, typeid(this[cid])));
                Tid tid = spawn(&caldron_thread_func, false, cid);
                childCaldrons_ ~= tid;

                // Mybe setup the host instance of breed
                if      //is it a breed?
                        (auto br = cast(Breed)this[cid])
                {
                    br.tid = tid;   // spawned Tid
                    br.activate;    // bread concept is setup and ready
                }
            }
        }

    STOP_AND_WAIT:
    debug(2)
        logit("leaving " ~ caldName);
    }

    /// Ditto.
    private Concept cpt_(string name) {
        assert(name in _nm_ && _nm_[name] in _hm_);
        return this[_nm_[name]];
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
        Breed breed = cast(Breed)this[Chat.chat_breed];
        breed.tid = thisTid;
        breed.activate;         // the breed is setup and ready
        super(breed.cid);
    }

    /// Ditto.
    protected override bool _msgProcessing(immutable Msg msg) {

        if (super._msgProcessing(msg))
            return true;
        else if      // is it a Tid of the client sent by Dispatcher?
                (auto m = cast(immutable DispatcherSuppliesCircleWithClientTid)msg)
        {   //yes: accept the Tid and move it to the concept level
            (scast!(TidPremise)(this[CommonConcepts.userThread_tidprem])).tid = cast()m.senderTid;
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

/// The attention circle/caldron object, thead local.
Caldron caldron;

/**
        Thread function for caldrons and the attention circle.
    Parameters:
        calledByDispatcher = the caller, true - the dispatcher, false - a caldron
        seedCid = seed, only for a caldron
*/
void caldron_thread_func(bool calledByDispatcher, Cid breedOrSeedCid = 0) {try{

    // Create caldron
    if      //is dispatcher spawning an attention circle?
            (calledByDispatcher)
    {   //yes: create attention circle
        caldron = new AttentionCircle();
        assert(breedOrSeedCid == 0);
    }
    else//no: it was a caldron
    {
        caldron = new Caldron(breedOrSeedCid);
    }

    debug(2) {
        string s;
        if      // is it a circle thread?
                (calledByDispatcher)
            s = "starting the attention circle thread";
        else
            if      // is it a breeded branch?
                    (auto br = cast(shared SpBreed)_hm_[breedOrSeedCid])
                s = format!"starting breeded branch %s(%s)"(br.seed, _nm_.name(br.seed));
            else
                s = format!"starting seeded branch %s(%s)"(breedOrSeedCid, _nm_.name(breedOrSeedCid));

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
                    (caldron._msgProcessing(msg))
                //yes: go for a new message
                continue ;
            else if // is it a request for the circle termination?
                    (cast(TerminateAppMsg)msg)
            {   //yes: terminate me and all my subthreads
                debug(2)
                    logit("terminating caldron " ~ caldron.caldName);
                caldron.terminateChildren;

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

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
