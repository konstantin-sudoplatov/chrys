module atn.atn_caldron;
import std.stdio, std.string;
import core.thread, std.concurrency, std.exception;

import proj_data, proj_funcs;

import chri_types;
import messages;
import cpt.abs.abs_concept, cpt.abs.abs_neuron;
import cpt.cpt_interfaces, cpt.cpt_neurons, cpt.cpt_actions, cpt.cpt_premises;

/// The debug level switch, controlled from the conceptual level.
int dynDebug = 0;

/**
        Workspace for a reasoning branch. It contains its own set of live concepts, can process messages coming to it
    from user or other caldrons. The processing is done in the reasoning_() function, that proceses the head concept of
    the branch. The head concept can produce another head and a set of grafts, which are procesed in the current caldron
    as fibers and/or breeds, that spawn new caldrons and are processed in their own spaces. If there is no new head or
    the stop_ flag is raised by an action concept, the caldron finishes its work, except when the caldron is an attention
    circle, which cannot be finished that way. If the wait_ flag is raised, caldron pauses its work until a new message
    comes and the reasoning_() function is called again. Br
        To speed up the work pools of fibers and threads are provided.
*/
class Caldron {

    /**
            Constructor.
        Parameters:
            breedCid = breed to start with
    */
    this(Cid breedCid) {
        checkCid!SpBreed(breedCid);

        auto breed = cast(Breed)this[breedCid];
        breedCid_ = breed.cid;
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
        assert(cid in _sm_, "Cid %s(%s) is not there in the spirit map.".format(cid, cptName(cid)));
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

    /// Request for leaving the reasoning_() function and receiving next message. This flag is lower at the start of the reasoning cycle.
    final void requestWait() {
        wait_ = true;
    }

    /// Request for finishing a branch. This flag is lower at the start of the reasoning cycle.
    final void requestStop() {
        stop_ = true;
    }

    /// Send children the termination signal and wait their termination.
    final void terminateChildren() {
        foreach (child; childCaldrons_.byKey)
            try {
                child.send(new immutable TerminateApp_msg);
            } catch(Throwable){}
        childCaldrons_ = null;
    }

    /// Caldron's name (based on the seed), if exist, else "noname".
    final string cldName() {
        import std.array: replace;

        if (auto nmp = breed.seed in _nm_)
            return (*nmp).replace("_seed", "");
        else
            return "noname";
    }

    /// Get breed of the caldron
    final Breed breed() {
        return scast!Breed(this[breedCid_]);
    }

    /// Raise the checkPt_ flag. It is raised by the checkup action, checked in the reasoning cycle and then immediately
    /// reset. Designed as a condition on which the debugger break point could be set.
    final void checkUp() {
        debug checkPt_ = true;
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
            if (dynDebug >= 1)
                logit("%s, message StartReasoningMsg has come".format(cldName), TermColor.brown);

            reasoning_;
            return true;
        }
        else if // is it a request for setting activation?
                (auto m = cast(immutable IbrSetActivation_msg)msg)
        {
            if(dynDebug >= 1)
                logit("%s, message IbrSetActivationMsg has come, %s.activation = %s".format(cldName,
                        cptName(m.destConceptCid), m.activation), TermColor.brown);

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
                logit("%s, message SingleConceptPackageMsg has come, load: %s(%,?s)".format(cldName,
                        cptName(m.load.cid), '_', m.load.cid), TermColor.brown);

            this[] = cast()m.load;      // inject
            reasoning_;                 // kick off
            return true;
        }
        else if // new text line from user?
                (auto m = cast(immutable UserTalksToCircle_msg)msg)
        {   //yes: put the text into the userInput_strprem concept
            if (dynDebug >= 1)
                logit("%s, message UserTalksToCircleMsg has come, text: %s".format(cldName, m.line), TermColor.brown);

            auto cpt = scast!StringQueuePrem(this[HardCid.userInputBuffer_strqprem_hcid]);
            cpt.push(m.line);
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

    /// Breed for this caldron.
    private Cid breedCid_;

    /// Caldrons, that were parented here.
    private CaldronThread[Tid] childCaldrons_;

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
    private int depth_;

    debug
        /// Raised by the checkup action and reset at the beginning of the next reasoning cycle
        private bool checkPt_;

    //---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

    private void reasoning_() {
//int i; if(i == 0) return;
        if (dynDebug >= 1) logit("%s, entering".format(cldName), TermColor.blue);
        //if (dynDebug >= 1) logit("%s, entering level %s".format(cldName, depth_), TermColor.blue);
//        depth_++;
        wait_ = stop_ = false;
        debug checkPt_ = false;

        Neuron head = scast!Neuron(this[headCid_]);
        while(true) {
            // Put on the head
            if (dynDebug >= 1)
                    logit("%s, headCid_: %s(%,?s)".format(cldName, cptName(headCid_), '_', headCid_), TermColor.blue);

            // Process the head, determine effects and do the actions.
            auto effect = head.calculate_activation_and_get_effects(this);
            foreach(actCid; effect.actions) {
                if (dynDebug >= 1)
                    logit("%s, action: %s(%,?s)".format(cldName, cptName(actCid), '_', actCid), TermColor.blue);
                A act = scast!A(this[actCid]);
                act.run(this);

                // Check the checkPt_ flag, that could be raised by an action
                debug if(checkPt_) {
                    int dummy;      // point for debugger to break
                }
            }

            // Do branching and extract the grafts and the new head neuron.
            Graft[] grafts;
            head = null;
            foreach(cid; effect.branches) {
                const Concept cpt = this[cid];
                if      // is it a breed?
                        (auto breed = cast(Breed)cpt)
                {   //yes: spawn the new branch
                    if(dynDebug >= 1) logit("%s, spawning %s(%,?s)".format(cldName, cptName(cid), '_', cid),
                            TermColor.blue);
                    CaldronThread thread = _threadPool_.pop(new Caldron(cid));
                    breed.tid = thread.tid;     // wind up our instance
                    breed.activate;             // of the breed
                    childCaldrons_[thread.tid] = thread;
                }
                else if
                        (auto graft = cast(Graft)cpt)
                    grafts ~= graft;
                else if
                        (auto nrn = cast(Neuron)cpt)
                {
                    enforce(head is null, "Neuron %s(%,?s) is the second neuron in branches, and there can" ~
                            "only be one. effec.branches = %s".format(cptName(cid), '_', cid, effect.branches));
                    if(dynDebug >= 2) logit("%s, assigned new head %s(%,?s)".format(cldName, cptName(cid), '_', cid),
                            TermColor.green);
                    head = nrn;         // new head
                    headCid_ = cid;
                }
                else {
                    logit("%s, unexpected concept %s(%,?s) %s".format(cldName, cptName(cid), '_', cid,
                            cid in _sm_? _sm_[cid].toString: "not in _sm_"), TermColor.red);
                }
            }

            // May be the actions raised the stop_ or wait_ flags.
            if (wait_ || head is null) {
                if (dynDebug >= 1) logit("%s, leaving".format(cldName), TermColor.blue);
                return;
                //if (dynDebug >= 1) logit("%s, yielding level %s on wait".format(cldName, depth_),
                //        TermColor.blue);
                //depth_--;
                //Fiber.yield;
            }
            //else if (stop_) {
            //    assert(!cast(AttentionCircle)this, "Attention circle cannot be stopped by the stop_ flag.");
            //    if (dynDebug >= 1) logit("%s, leaving level %s on stop".format(cldName, depth_),
            //            TermColor.blue);
            //    depth_--;
            //    return;
            //}

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
    this() { super(HardCid.chatBreed_breed_hcid.cid); }

    override bool _processMessage(immutable Msg msg) {

        if (super._processMessage(msg))
            return true;
        else if      // is it a Tid of the client sent by Dispatcher?
                (auto m = cast(immutable DispatcherProvidesCircleWithUserTid_msg)msg)
        {   //yes: wind up the userThread_tidprem concept
            if(dynDebug >= 1)
                    logit("%s, message DispatcherProvidesCircleWithUserTid_msg has come, %s".format(cldName,
                    m.tid), TermColor.brown);
            auto userThreadTidprem = (scast!TidPrem(this[HardCid.userTid_tidprem_hcid]));
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
            auto fiber = cast(Fiber)(cast()fibers_).pull;
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
            auto cldThread = cast(CaldronThread)(cast()threads_).pull;
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
        Breed cldBreed = cld.breed;
        cldBreed.tid = myTid_;
        cldBreed.activate;         // the local instance of the breed is setup and ready
        myTid_.send(new immutable IbrStartReasoning_msg);   // kick off the reasoning cycle
    }

    /// Destructor terminates thread. To terminate it explicitely call destroy(this).
    ~this() {
        send(myTid_, cast(shared) new CaldronThreadTerminationRequest);
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
        myTid_.send(new immutable IbrStartReasoning_msg);
    }

    /**
            Deactivate Thread.
    */
    void mothball() {
        caldron_ = null;
    }

    /// Get Tid.
    Tid tid() {
        return myTid_;
    }

    /**
            Main thread function. Basically it is receiving and processing messages that come to the thread.
    */
    shared void caldronThreadFunc() { try {
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
                if      // recognized and processed by caldron?
                        ((cast()caldron_)._processMessage(msg))
                    //yes: go for a new message
                    continue ;
                else if // is it a request for the circle termination?
                        (cast(TerminateApp_msg)msg)
                {   //yes: terminate me and all my subthreads
                    if (dynDebug >= 1)
                            logit("%s, message TerminateApp_msg has come, terminating caldron".format(
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
    }
}

    //---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%
    //
    //                               Private
    //
    //---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%

    /// Own Tid.
    private Tid myTid_;

    /// Caldron instance to call to call the Caldron._processMessage() function.
    private Caldron caldron_;

    /// Message to itself to terminate.
    private class CaldronThreadTerminationRequest {}
}













