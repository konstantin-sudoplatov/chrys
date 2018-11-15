module atn.atn_caldron;
import std.stdio, std.string;
import std.algorithm;
import core.thread, std.concurrency, std.exception;

import proj_data, proj_funcs;

import chri_types;
import messages;
import cpt.abs.abs_concept, cpt.abs.abs_neuron;
import cpt.cpt_interfaces, cpt.cpt_neurons, cpt.cpt_actions, cpt.cpt_premises;

/**
        Workspace for a reasoning branch. It contains its own set of live concepts, can process messages coming to it
    from user or other caldrons. The processing is done in the reasoning_() function, that proceses the head concept of
    the branch. The head concept can produce another head and a set of grafts, which are procesed in the current caldron
    as fibers and/or breeds, that spawn new caldrons and are processed in their own spaces. If there is no new head or
    the stop_ flag is raised by an action concept, the caldron finishes its work, except when the caldron is an attention
    circle, which cannot be finished that way. If the wait_ flag is raised, caldron pauses its work until a new message
    comes and the reasoning_() function is called again. $(Br)
        To speed up the work pools of fibers and threads are provided.
*/
class Caldron {
    debug {
        /// The debug level switch, controlled from the conceptual level.
        int dynDebug = 0;

        /// Raised by the checkup action and reset at the beginning of the next reasoning cycle
        bool checkPt;
    }

    /**
            Constructor.
        Parameters:
            parent = parent caldron thread
            breedCid = breed to start with
            inPars = input parameter cancepts (those from the breed). They are injected into the caldron.
    */
    this(CaldronThread parent, Cid breedCid, Concept[] inPars) {
        import std.algorithm: map, each, canFind;

        parentThread_ = parent;

        checkCid!SpBreed(breedCid);
        breedCid_ = breedCid;
        auto breed = scast!Breed(this[breedCid]);

        debug {
            if (inPars) {
                assert(breed.inPars.length == inPars.length);
                inPars.map!(cpt => cpt.cid).each!(cid => assert(canFind(breed.inPars, cid),
                        "Different set of Cids in the parameter and breed inPars."));
            }
        }
        foreach(cpt; inPars) this[] = cpt;

        checkCid!SpiritNeuron(breed.seed);
        headCid_ = breed.seed;
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
        assert(cid in _sm_, "Cid %s(%,?s) is not in the spirit map.".format(cptName(cid), '_', cid));
        if
                (auto p = cid in lm_)
            return *p;
        else
            return lm_[cid] = _sm_[cid].live_factory;
    }

    /// Adapter
    final Concept opIndex(DcpDsc cd) {
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

    /// Request for leaving the reasoning_() function and receiving next message. This flag is lower at the start of the reasoning cycle.
    final void requestWait() {
        wait_ = true;
    }

    /// Request for finishing a branch. This flag is lower at the start of the reasoning cycle.
    final void requestStop() {
        stop_ = true;
    }

    @property void myThread(CaldronThread thread) { myThread_ = thread; }

    @property CaldronThread parentThread() { return parentThread_; }

    /// Send children the termination signal and wait their termination.
    final void terminateChildren() {
        foreach (child; childThreads_.byValue)
            try {
                if(!child.isFinished) {
                    child.tid.send(new immutable TerminateApp_msg);
                    while(!child.isFinished) Thread.sleep(10.msecs);
                }
            } catch(Throwable){
                Caldron cld = child.caldron;
                logit("Error happened while terminating thread %s".format(cld? cld.cldName: "???"), TermColor.red);
            }
        childThreads_ = null;
    }

    /// Caldron's name (based on the seed), if exist, else "noname".
    final string cldName() {
        import std.string: indexOf;

        string cldNm;
        if (auto nmp = breedCid_ in _nm_)
            cldNm = (*nmp)[0..(*nmp).indexOf("_breed")];
        else
            cldNm = "noname";

        debug
            if        // not in fiber?
                        (recurDepth_ == 0)
                return cldNm;
            else {
                string s = cldNm;
                if
                (auto nmp = currentGraftCid_ in _nm_)
                    return s ~= " %s %s".format((*nmp)[0..(*nmp).indexOf("_graft")], recurDepth_);
                else
                    return s ~= " noname %s".format(recurDepth_);
            }
        else
            return cldNm;
    }

    @property Cid breedCid() { return breedCid_; }

    /// Get breed of the caldron
    final Breed breed() {
        return scast!Breed(this[breedCid_]);
    }

    /// Raise the checkPt_ flag. It is raised by the checkup action, checked in the reasoning cycle and then immediately
    /// reset. Designed as a condition on which the debugger break point could be set.
    final void checkUp() {
        debug checkPt = true;
    }

    /// Is it doing a fiber call right now?
    final bool isInFiber() {
        return recurDepth_ != 0;
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
    protected bool _processMessage(immutable Msg msg) {

        if      // is it a request for starting reasoning?
                (cast(IbrStartReasoning_msg)msg)
        {   // kick off the reasoning loop
            debug if (dynDebug >= 1)
                logit("%s: message IbrStartReasoning_msg has come".format(cldName), TermColor.brown);

            reasoning_;
            return true;
        }
        else if // is it a request for setting activation?
                (auto m = cast(immutable IbrSetActivation_msg) msg)
        {
            debug if(dynDebug >= 1)
                logit("%s: message IbrSetActivation_msg has come, %s.activation = %s".format(cldName,
                        cptName(m.destConceptCid), m.activation), TermColor.brown);

            if      // is it bin activation?
                    (auto cpt = cast(BinActivationIfc)this[m.destConceptCid])
                if(m.activation > 0)
                    cpt.activate;
                else
                    cpt.anactivate;
            else    //no: it is esquash
                (cast(EsquashActivationIfc)this[m.destConceptCid]).activation = m.activation;

            scast!TidPrem(this[HardCids.callerTid_tidprem_hcid]).tid = msg.senderTid;    // caller's tid. must be used durig this call of the reasoning_()
            reasoning_;

            return true;
        }
        else if // A concept was posted by another caldron?
                (auto m = cast(immutable IbrSingleConceptPackage_msg) msg)
        {   //yes: it's already a clone, inject into the current name space (may be with overriding)
            debug if (dynDebug >= 1)
                logit("%s: message IbrSingleConceptPackage_msg has come from %s, load: %s(%,?s)".format(cldName,
                        msg.senderTid, cptName(m.load.cid), '_', m.load.cid), TermColor.brown);

            this[] = cast()m.load;      // inject load
            scast!TidPrem(this[HardCids.callerTid_tidprem_hcid]).tid = msg.senderTid;    // caller's tid. must be used durig this call of the reasoning_()
            reasoning_;                 // kick off

            return true;
        }
        else if // a branch sent its devise?
                (auto m = cast(immutable IbrBranchDevise_msg) msg)
        {   //yes: take outPars and anactivate its breed
            debug if (dynDebug >= 1)
                logit("%s: message IbrBranchDevise_msg has come from %s.".format(cldName, msg.senderTid),
                        TermColor.brown);
//writefln("m.breed = %s(%,?s), childThreads_ = %s", cptName(m.breedCid), '_', m.breedCid, '_', childThreads_);
            CaldronThread thread = childThreads_[m.breedCid];
            Caldron cld = thread.caldron;
            Breed breed = cld.breed;
            assert(breed.cid == m.breedCid);

            // Take outPars and anactivate breed
            foreach(p; breed.outPars)
                this[] = cld[p];     // inject
            scast!Breed(this[breed.cid]).anactivate;

            // Remove the thread from caldron's children and put it in the pool
            childThreads_.remove(m.breedCid);
            _threadPool_.push(thread);

            reasoning_;                 // kick off

            return true;
        }
        else if // new text line from user?
                (auto m = cast(immutable UserTellsCircle_msg) msg)
        {   //yes: put the text into the userInput_strprem concept
            debug if (dynDebug >= 1)
                logit("%s: message UserTellsCircle_msg has come, text: %s".format(cldName, m.line), TermColor.brown);

            auto cpt = scast!StringQueuePrem(this[HardCids.userInputBuffer_strqprem_hcid]);
            cpt.pushBack(m.line);
            cpt.activate;       // the premise is ready
            reasoning_;         // kick off

            return true;
        }
        return false;
    }

    //---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%
    //
    //                               Private
    //
    //---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%

    /// My thread.
    private CaldronThread myThread_;

    /// Parent thread.
    private CaldronThread parentThread_;

    /// Breed for this caldron.
    private Cid breedCid_;

    /// Caldrons, that were parented here. Key is a breed.
    private CaldronThread[Cid] childThreads_;

    /// Live map. All holy concepts, that are ever addressed by the caldron are wrapped in corresponding live object and
    /// put in this map. So, caldron always works with its own instance of a concept.
    private Concept[Cid] lm_;

    /// Cid of the head concept of the caldron. It would always be a neuron.
    private Cid headCid_;

    /// The wait flag. Raised by the wait_stat() static concept.
    private bool wait_;

    /// The stop flag. Raised by the stop_stat() static concept.
    private bool stop_;

    /// Level of recursion of the reasoning_() function. 0 - the lowest
    private int recurDepth_;

    /// Graft registry: caldron's root grafts.
    private Fiber[Cid] grafts_;

    /// In debug time we should know what graft (fiber) currently we are in
    debug private Cid currentGraftCid_;

    //---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

    private void reasoning_() {

        // Create environment for recursive calls of fibers
        Fiber[Cid] grafts;
        if      // is it zero (not fiber) level?
                (recurDepth_ == 0)
        {   // yes: use root grafts as the graft registry
            grafts = grafts_;
        }
        debug if (dynDebug >= 1) logit("%s: entering".format(cldName), TermColor.blue);

        // Save initial state for the next call
        Cid lastHeadCid;     // last used head
        scope(exit) {
            debug if (dynDebug >= 1) logit("%s: leaving %s".format(cldName, stop_? "on stop": ""), TermColor.blue);
            if      // not in fiber?
                    (recurDepth_ == 0)
            {   // no: save the last head cid and the grafts
                headCid_ = lastHeadCid;
                grafts_ = grafts;
            }
        }

        // Main reasoning cycle
        wait_ = stop_ = false;
        debug checkPt = false;
        lastHeadCid = headCid_;     // last used head
        Neuron head = scast!Neuron(this[headCid_]);
        while(true) {
            // Put on the head
            debug if (dynDebug >= 1)
                    logit("%s: head %s(%,?s)".format(cldName, cptName(head.cid), '_', head.cid), TermColor.blue);

            // Resume waiting fibers, order is unpredictable; remove finished ones from grafts
            foreach(cid, fiber; grafts) {
                debug currentGraftCid_ = cid;
                recurDepth_++;
                debug if (dynDebug >= 1) logit("%s: reentering".format(cldName), TermColor.blue);
                fiber.call;
                recurDepth_--;
                debug currentGraftCid_ = 0;
                if      // is the fiber terminated?
                        (fiber.state == Fiber.State.TERM)
                {   //yes: remove it from the grafts and return to the pool
                    grafts.remove(cid);
                    _fiberPool_.push(fiber);
                }
            }

            // Process the head, determine effects and do the actions.
            auto effect = head.calculate_activation_and_get_effects(this);
            foreach(actCid; effect.actions) {
                debug if (dynDebug >= 1)
                    logit("%s: action, %s(%,?s)".format(cldName, cptName(actCid), '_', actCid), TermColor.blue);
                A act = scast!A(this[actCid]);
                act.run(this);

                // Check the checkPt_ flag, that could be raised by an action
                debug if(checkPt) {
                    int dummy;      // point for debugger to break
                }
            }

            // Do branching and extract the grafts and the new head neuron.
            typeof(head) savedHead = head;      // in case there would be specified no explicit head
            head = null;
            foreach(cid; effect.branches) {
                const Concept cpt = this[cid];
                if      // is it a breed?
                        (auto breed = cast(Breed)cpt)
                {   //yes: spawn the new branch
                    assert(cid !in childThreads_,
                            "%s(%,?s) attempt to spawn breed that is already runnig".format(cptName(cid), '_', cid));

                    debug if(dynDebug >= 1) logit("%s: spawning %s(%,?s)".format(cldName, cptName(cid), '_', cid),
                            TermColor.blue);

                    // Prepare input concepts to inject
                    Concept[] inPars;
                        foreach(c; breed.inPars) inPars ~= this[c].clone;

                    // and start it
                    Caldron cld = new Caldron(
                        this.myThread_,         // parent
                        cid,                    // breed
                        inPars                  // input concepts
                    );
                    CaldronThread thread = _threadPool_.pop(cld);
                    cld.myThread = thread;

                    // register a child
                    childThreads_[cid] = thread;

                    breed.tid = thread.tid;     // wind up our instance
                    breed.activate;             // of the breed

                    // only after all is set up, kick off the thread
                    thread.tid.send(new immutable IbrStartReasoning_msg);
                }
                else if // is it a graft?
                        (auto graft = cast(Graft)cpt)
                {   // create a fiber
                    assert(cid !in grafts, "%s: attempt to create fiber that already exists %s(%,?s)".format(cldName,
                            cptName(cid), '_', cid));

                    Cid savedHeadCid = headCid_;
                    headCid_ = scast!Graft(this[cid]).seed;     // make the seed provided by graft the new head for the fiber
                    Fiber fiber = _fiberPool_.pop(this);
                    debug if(dynDebug >= 1) logit("%s: grafting fiber %s(%,?s)".format(cldName, cptName(cid), '_', cid),
                            TermColor.blue);
                    debug currentGraftCid_ = cid;
                    recurDepth_++;
                    fiber.call;
                    recurDepth_--;
                    debug currentGraftCid_ = 0;
                    if      // is the fiber not finished after the call?
                            (fiber.state == Fiber.State.HOLD)
                    {   // no: add it to the fiber registry
                        grafts[cid] = fiber;        // put it into the set of grafts
                    }
                    headCid_ = savedHeadCid;    // restore the head
                }
                else if
                        (auto nrn = cast(Neuron)cpt)
                {
                    enforce(head is null, "Neuron %s(%,?s) is the second neuron and there can " ~
                            "only be one. effect.branches = %s".format(cptName(cid), '_', cid, effect.branches));
                    debug if(dynDebug >= 2) logit("%s: assigned new head %s(%,?s)".format(cldName, cptName(cid), '_', cid),
                            TermColor.green);
                    head = nrn;         // new head
                    lastHeadCid = cid;
                }
                else {
                    logit("%s: unexpected concept %s(%,?s) %s".format(cldName, cptName(cid), '_', cid,
                            cid in _sm_? _sm_[cid].toString: "not in _sm_"), TermColor.red);
                }
            }

            // May be the actions raised the stop_ or wait_ flags.
            if (wait_ || head is null) {
                if      // is it a zero level?
                        (recurDepth_ == 0)
                    // yes: leave
                    return;
                else {  //no: it is a fiber, yield it
                    assert(Fiber.getThis, "An attempt to leave a fiber while being not in a fiber.");
                    if      // the head was not changed?
                            (!head)
                        head = savedHead;
                    debug if (dynDebug >= 1) logit("%s: yielding".format(cldName),
                            TermColor.blue);
                    Fiber.yield;
                }
            }
            else if (stop_) {
                assert(!cast(AttentionCircle)this, "Attention circle cannot be stopped by the stop_ flag.");
                return;
            }
        }
    }
}

/// This class works directly with the cilent. It creates a tree of caldrons along the way and remains its root.
/// The client can be a person, or maybe a book or a scripted task. It is something that the circle get the informational
/// input from. It is created and deleted by the attention circle dispatcher.
final class AttentionCircle: Caldron {

    /**
            Constructor.
    */
    this() {
        super(
            null,       // parent
            HardCids.chat_breed_hcid.cid,   // breed
            null        // inPars
        );
    }

    override bool _processMessage(immutable Msg msg) {

        if (super._processMessage(msg))
            return true;
        else if      // is it a Tid of the client sent by Dispatcher?
                (auto m = cast(immutable DispatcherProvidesCircleWithUserTid_msg)msg)
        {   //yes: wind up the userThread_tidprem concept
            debug if(dynDebug >= 1)
                    logit("%s: message DispatcherProvidesCircleWithUserTid_msg has come, %s".format(cldName,
                    m.tid), TermColor.brown);
            auto userThreadTidprem = (scast!TidPrem(this[HardCids.userTid_tidprem_hcid]));
            userThreadTidprem.tid = cast()m.tid;
            userThreadTidprem.activate;
            reasoning_;
            return true;
        }
        else
            return false;
    }
}

/// Fiber pool
import proj_types;
import chri_data;
synchronized class CaldronFiberPool {

    /**
            Get stacked or generate new fiber for the Caldron.reasoning_() delegate.
        Parameters:
            cld = caldron to setup the fiber for
        Returns: the Fiber object.
    */
    Fiber pop(Caldron cld) {
        if      // is the stack empty?
                ((cast()fibers_).empty)
        {
            return new Fiber(&cld.reasoning_);
        }
        else { //no: reset and return a fiber
            auto fiber = cast(Fiber)(cast()fibers_).pop;
            fiber.reset(&cld.reasoning_);

            return fiber;
        }
    }

    /**
            Get a fiber stacked if there is space in the stack.
        Parameters:
            fiber = fiber to stack
    */
    void push(Fiber fiber) {
        if((cast()fibers_).length <= CALDRON_FIBER_POOL_SIZE) (cast()fibers_).push(fiber);
    }

    /// Stack of fibers.
    private Deque!(Fiber) fibers_;
}

synchronized class CaldronThreadPool {

    /**
            Get a stacked or generate new thread for the Caldron._processMessage() delegate.
        Parameters:
            cld = caldron to setup the thread for. This caldron is associated with the thread and from now on is used
                  by the thread to call the _processMessage() function on new messages coming.
        Returns: the CaldronThread object.
    */
    CaldronThread pop(Caldron cld) {
        if      // is the stack empty?
                ((cast()threads_).empty)
        {
            return new CaldronThread(cld);
        }
        else { //no: reset and return a fiber
            auto cldThread = cast(CaldronThread)(cast()threads_).pop;
            cldThread.reset(cld);

            return cldThread;
        }
    }

    /**
            Get a caldron thread stacked if there is space in the stack. The caldron object is disassociated from the thread.
        The thread meanwhile is not terminated, it waits on new messages.
        Parameters:
            thread = caldron thread to stack
    */
    void push(CaldronThread thread) {
        if      // is pool able of storing the thread?
                ((cast()threads_).length <= CALDRON_THREAD_POOL_SIZE)
        {   //yes: disassociate it from the caldron and save it
            thread.mothball;
            (cast()threads_).push(thread);
        }
        else { //no: terminate the thread
            thread.destroy;
        }
    }

    /// Request terminating all canned threads.
    void terminate() {
        while(!(cast()threads_).empty) {
            CaldronThread thread = scast!CaldronThread((cast()threads_).pop);
            send(thread.tid, cast(shared) new CaldronThreadTerminationRequest);
                while(thread.isFinished)
                    Thread.sleep(10.msecs);
        }
    }

    //---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%
    //
    //                               Private
    //
    //---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%

    /// Stack of threads.
    private Deque!(CaldronThread) threads_;
}

class CaldronThread {

    /**
            Constructor.
        Parameters:
            cld = caldron to associate with the thread
    */
    this(Caldron cld) {
        assert(cld !is null);
        caldron_ = cld;
        myTid_ = spawn(&(cast(shared)this).caldronThreadFunc);
        Breed cldBreed = cld.breed;     // it is legal, since we setup
        cldBreed.tid = myTid_;          // the new caldrons's breed prior to kicking off the reasoning.
        cldBreed.activate;         // the local instance of the breed is setup and ready
        myTid_.send(new immutable IbrStartReasoning_msg);   // kick off the reasoning cycle
    }

    /**
            Reassociate thread with the new caldron.
        Parameters:
            cld = caldron to associate the thread with.
    */
    void reset(Caldron cld) {
        assert(cld);
        assert(caldron_ is null);
        cld.breed.tid = myTid_;
        cld.breed.activate;
        caldron_ = cld;
    }

    /// Get caldron.
    Caldron caldron() { return caldron_; }

    /**
            Cann the thread. Thread is not finished, it is sleeping on the receive function and may be reused by
        assigning new caldron in the reset() func.
    */
    void mothball() {
        caldron_ = null;
    }

    /// Get Tid.
    @property Tid tid() { return myTid_; }

    /// Up if the caldronThreadFunc() function exited normally or on exception.
    bool isFinished() { return isFinished_; }

    /// Shows if the thread was canned (see the mothBall() function).
    bool isCanned() { return caldron is null; }

    //---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%
    //
    //                               Private
    //
    //---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%

    /// Own Tid.
    private Tid myTid_;

    /// Caldron instance to call the Caldron._processMessage() function.
    private Caldron caldron_;

    /// This flag is raised if the caldronThreadFunc() function exited normally or on exception.
    private bool isFinished_;

    //---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

    /**
            Main thread function. Basically it is receiving and processing messages that come to the thread.
    */
    private shared void caldronThreadFunc() { try {

        scope(exit) {
            isFinished_ = true;
        }

        // Receive messages in a cycle
        while(true) {
            import std.variant: Variant;

            immutable Msg msg;
            Throwable ex;
            CaldronThreadTerminationRequest term;
            Variant var;    // the catchall type

            receive(
                (immutable Msg m) { (cast()msg) = cast()m; },
                (shared CaldronThreadTerminationRequest t) { term = cast()t; },
                (shared Throwable e) { ex = cast()e; },
                (Variant v) { var = v; }          // the catchall clause
            );

            // Recognize and process the message
            if      // is it a regular message?
                    (msg)
            {   // yes: send the message to caldron
                if      // is caldron canned?
                        (!caldron_)
                {
                    logit( "Warning. Message %s has come to a canned caldron.".format(typeid(msg)), TermColor.red);
                    continue;
                }

                if      // recognized and processed by caldron?
                        ((cast()caldron_)._processMessage(msg))
                    //yes: go for a new message
                    continue ;
                else if // is it a request for the circle termination?
                        (cast(TerminateApp_msg)msg)
                {   //yes: terminate me and all my subthreads
                    debug if (caldron_.dynDebug >= 1)
                            logit("%s: message TerminateApp_msg has come, terminating caldron".format(
                            (cast()caldron_).cldName), TermColor.brown);
                    (cast()caldron_).terminateChildren;
                    // terminate itself
                    goto FINISH_THREAD;
                }
                else
                {  // unrecognized message of type Msg. Log it.
                    logit("Unexpected message to the caldron %s: %s".format((cast()caldron_).cldName, typeid(msg)),
                            TermColor.brown);
                    continue ;
                }
            }
            else if // is it a request for the thread termination?
                    (cast(CaldronThreadTerminationRequest)term)
            {   //yes: terminate itself
                goto FINISH_THREAD;
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
                            ((cast()caldron_).cldName, var.toString), TermColor.brown);
                continue;
            }
        }

        FINISH_THREAD:
    } catch(Throwable e) {
        (cast()caldron_).terminateChildren;
        send(ownerTid, cast(shared)e);
    }}

    //---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
}

/// Message to terminate caldron thread.
private class CaldronThreadTerminationRequest {}













