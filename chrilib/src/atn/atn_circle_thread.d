module atn.atn_circle_thread;
import std.concurrency, std.format;

import proj_data, proj_funcs;

import chri_types, chri_data;
import cpt.abs.abs_concept, cpt.abs.abs_neuron;
import cpt.cpt_neurons, cpt.cpt_premises, cpt.cpt_actions, cpt.cpt_interfaces;
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
        thisTid.send(new immutable IbrStartReasoning_msg);
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
        assert(_sm_[cid], format!"Cid %s(%s) cannot be get from the spirit map."
                (cid, (cid in _nm_)? _nm_[cid]: "noname"));
        if
                (auto p = cid in lm_)
            return *p;
        else
            return lm_[cid] = _sm_[cid].live_factory;
    }

    /// Adapter
    final Concept opIndex(DcpDescriptor cd) {
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
            child.send(new immutable TerminateApp_msg);
    }

    // Caldron's name (based on the seed), if exist, else "noname".
    final string caldName() {
        import std.array: replace;

        if (auto nmp = seedCid_ in _nm_)
            return (*nmp).replace("_seed", "");
        else
            return "noname";
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
                (cast(IbrStartReasoning_msg)msg)
        {   // kick off the reasoning loop
            if (dynDebug >= 1)
                logit(format!"%s, message StartReasoningMsg has come"(caldName), TermColor.brown);

            reasoning_;
            return true;
        }
        else if // is it a request for setting activation?
                (auto m = cast(immutable IbrSetActivation_msg)msg)
        {
            if(dynDebug >= 1)
                logit(format!"%s, message IbrSetActivationMsg has come, %s.activation = %s"
                        (caldName, _nm_[m.destConceptCid], m.activation), TermColor.brown);

            if      // is it bin activation?
                    (auto cpt = cast(BinActivationIfc)this[m.destConceptCid])
                if(m.activation > 0)
                    cpt.activate;
                else
                    cpt.anactivate;
            else    //no: it is esquash
                (cast(EsquashActivationIfc)this[m.destConceptCid]).activation = m.activation;

            reasoning_;
            return true;
        }
        else if // A concept was posted by another caldron?
                (auto m = cast(immutable IbrSingleConceptPackage_msg)msg)
        {   //yes: it's already a clone, inject into the current name space (may be with overriding)
            if (dynDebug >= 1)
                logit(format!"%s, message SingleConceptPackageMsg has come, load: %s(%,?s)"
                        (caldName, _nm_[m.load.cid], '_', m.load.cid), TermColor.brown);

            this[] = cast()m.load;      // inject
            reasoning_;                 // kick off
            return true;
        }
        else if // new text line from user?
                (auto m = cast(immutable UserTalksToCircle_msg)msg)
        {   //yes: put the text into the userInput_strprem concept
            if (dynDebug >= 1)
                logit(format!"%s, message UserTalksToCircleMsg has come, text: %s"(caldName, m.line), TermColor.brown);

            auto cpt = scast!StringQueuePrem(this[HardCid.userInputBuffer_hardcid_strqprem]);
            cpt.push(m.line);
            cpt.activate;       // the premise is ready
            reasoning_;         // kick off
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
        if (dynDebug >= 1)
            logit(caldName ~ ", entering", TermColor.green);
        while(true) {
            // Put on the head
            if (dynDebug >= 1)
                logit(format!"%s, headCid_: %s(%,?s)"(caldName, _nm_[headCid_], '_', headCid_), TermColor.green);
            Neuron head = cast(Neuron)this[headCid_];

            // Let the head be processed, determine effects and do the actions.
            auto effect = head.calculate_activation_and_get_effects(this);
            foreach(actCid; effect.actions) {
                if (dynDebug >= 1)
                    logit(format!"%s, action: %s(%,?s)"(caldName, _nm_[actCid], '_', actCid), TermColor.green);
                A act = cast(A)this[actCid];
                act.run(this);
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
        if (dynDebug >= 1) {
            if (stopAndWait_)
                logit(caldName ~ ", leaving on stopAndWait_ flag", TermColor.green);
            else
                logit(caldName ~ ", leaving on empty branches array", TermColor.green);
        }
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
        Breed breed = cast(Breed)this[HardCid.chatBreed_hardcid_breed];
        breed.tid = thisTid;
        breed.activate;         // the breed is setup and ready
        super(breed.cid);
    }

    protected override bool _msgProcessing(immutable Msg msg) {

        if (super._msgProcessing(msg))
            return true;
        else if      // is it a Tid of the client sent by Dispatcher?
                (auto m = cast(immutable DispatcherSuppliesCircleWithUserTid_msg)msg)
        {   //yes: wind up the userThread_tidprem concept
            auto userThreadTidprem = (scast!(TidPrem)(this[HardCid.userThread_hardcid_tidprem]));
            userThreadTidprem.tid = cast()m.tid;
            userThreadTidprem.activate;
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

/// The debug level switch, controlled from the conceptual level.
int dynDebug = 0;

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

    if (dynDebug >= 1) {
        string s;
        if      // is it a circle thread?
                (calledByDispatcher)
            s = "starting the attention circle thread";
        else
            if      // is it a breeded branch?
                    (auto br = cast(SpBreed)_sm_[breedOrSeedCid])
                s = format!"starting breeded branch %s(%s)"(_nm_[br.seed], br.seed);
            else
                s = format!"starting seeded branch %s(%s)"(_nm_[breedOrSeedCid], breedOrSeedCid);

        logit(s, TermColor.brown);
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
                    (cast(TerminateApp_msg)msg)
            {   //yes: terminate me and all my subthreads
                if (dynDebug >= 1)
                    logit("terminating caldron " ~ caldron.caldName);
                caldron.terminateChildren;

                // terminate itself
                goto FINISH_THREAD;
            }
            else
            {  // unrecognized message of type Msg. Log it.
                logit(format!"Unexpected message to the caldron %s: %s"
                        (caldron.caldName, typeid(msg)), TermColor.brown);
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
            logit(format!"Unexpected message of type Variant to the caldron %s: %s"
                    (caldron.caldName, var.toString), TermColor.brown);
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
