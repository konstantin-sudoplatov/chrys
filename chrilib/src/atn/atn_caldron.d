module atn.atn_caldron;
import std.stdio, std.string;
import core.thread, std.concurrency, std.exception;

import proj_data, proj_funcs;

import chri_types;
import messages;
import cpt.abs.abs_concept, cpt.abs.abs_neuron;
import cpt.cpt_interfaces, cpt.cpt_neurons, cpt.cpt_actions, cpt.cpt_premises;
import atn.atn_circle_thread;

/// The debug level switch, controlled from the conceptual level.
int dynDebug = 2;

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
class NewCaldron {

    /**
            Constructor.
        Parameters:
            breedCid = breed to start with
    */
    this(Cid breedCid) {

        assert(cast(Breed)this[breedCid],
                format!"Cid: %s, this concept must be of Seed or Breed type, not of %s."
                        (breedCid, typeid(this[breedCid])));

        auto breed = cast(Breed)this[breedCid];
        breed.tid = thisTid;
        breed.activate;         // the local instance of the breed is setup and ready
        headCid_ = seedCid_ = breed.seed;

        // Kick off the reasoning cycle
//        thisTid.send(new immutable IbrStartReasoning_msg);
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
        assert(cid in _sm_, "Cid %s(%s) is not there in the spirit map.".format(cid,
                (cid in _nm_)? _nm_[cid]: "noname"));
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
    final void requestWait() {
        wait_ = true;
    }

    /// Send children the termination signal and wait their termination.
    final void terminateChildren() {
        foreach (child; childCaldrons_.byKey)
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
    protected bool _processMessage(immutable Msg msg) {

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

    //---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%
    //
    //                               Private
    //
    //---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%

    /// Seed for this caldron.
    private Cid seedCid_;

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

    //---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

    private void reasoning_() {
        if (dynDebug >= 1) logit("Caldron %s, entering level %s.".format(caldName, depth_), TermColor.green);
        depth_++;
        const Cid headCid = headCid_;
        wait_ = stop_ = false;

        while(true) {
            Neuron head = scast!Neuron(this[headCid_]);

            // Process the head, determine effects and do the actions.
            auto effect = head.calculate_activation_and_get_effects(this);
            foreach(actCid; effect.actions) {
                if (dynDebug >= 1)
                    logit("%s, action: %s(%,?s)".format(caldName, _nm_[actCid], '_', actCid), TermColor.green);
                A act = scast!A(this[actCid]);
                act.run(this);
            }

            // May be the actions raised the stop_ or wait_ flags.
            if (wait_) {
                if (dynDebug >= 1) logit("Caldron %s, yielding level %s on wait.".format(caldName, depth_),
                        TermColor.green);
                depth_--;
                Fiber.yield;
            }
            else if (stop_) {
                assert(!cast(AttentionCircle)this, "Attention circle cannot be stopped by the stop_ flag.");
                if (dynDebug >= 1) logit("Caldron %s, leaving level %s on stop.".format(caldName, depth_),
                        TermColor.green);
                depth_--;
                return;
            }

            // Do branching and extract the grafts and the new head neuron.
            Graft[] grafts;
            Neuron newHead;
            foreach(cid; effect.branches) {
                const Concept cpt = this[cid];
                if      // is it a breed?
                        (auto breed = cast(Breed)cpt)
                {   //yes: spawn the new branch
                    CaldronThread thread = _threadPool_.pop(new NewCaldron(breed.cid));
                    childCaldrons_[thread.tid] = thread;
                }
                else if
                        (auto graft = cast(Graft)cpt)
                    grafts ~= graft;
                else if
                        (auto nrn = cast(Neuron)cpt)
                {
                    enforce(newHead is null, "Neuron %s(%s) is the second neuron in branches, and there can" ~
                            "only be one. effec.branches = %s".format(cid, (cid in _nm_? _nm_[cid]: "noname"),
                            effect.branches));
                    newHead = nrn;
                }
                else {
                    logit("Unexpected concept %s(%s) %s".format(cid, (cid in _nm_? _nm_[cid]: "noname"),
                            cid in _sm_? _sm_[cid].toString: "not in _sm_"), TermColor.red);
                }
            }

        }

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
    Fiber pop(NewCaldron cld) {
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
    CaldronThread pop(NewCaldron cld) {
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
            thread.reset;
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
    this(NewCaldron cld) {
        assert(cld !is null);
        caldron_ = cld;
        myTid_ = spawn(&(cast(shared)this).caldronThreadFunc);
        myTid_.send(new immutable IbrStartReasoning_msg);
    }

    /// Destructor terminates thread. To terminate it explicitely call destroy(this).
    ~this() {
        send(myTid_, cast(shared) new TerminationRequest);
    }

    /**
            Disassociate current caldron from the thread or reassociate thread with the new caldron.
        Parameters:
            cld = caldron to associate the thread with. null for disassociation.
    */
    void reset(NewCaldron cld = null) {
        caldron_ = cld;
        if(cld) myTid_.send(new immutable IbrStartReasoning_msg);
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
            TerminationRequest term;
            Variant var;    // the catchall type

            receive(
                (immutable Msg m) { (cast()msg) = cast()m; },
                (shared TerminationRequest t) { term = cast()t; },
                (shared Throwable e) { ex = cast()e; },
                (Variant v) { var = v; }          // the catchall clause
            );

            // Recognize and process the message
            if      // is it a regular message?
                    (msg)
            {   // process it
                //Send the message to caldron
                if // processed by caldron?
                        ((cast()caldron_)._processMessage(msg))
                    //yes: go for a new message
                    continue ;
                else
                {  // unrecognized message of type Msg. Log it.
                    logit("Unexpected message to the caldron %s: %s".format(caldron.caldName, typeid(msg)),
                            TermColor.brown);
                    continue ;
                }
            }
            else if // is it a request for the thread termination?
                    (cast(TerminationRequest)term)
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
                        (caldron.caldName, var.toString), TermColor.brown);
                continue;
            }

        }
        FINISH_THREAD:
    } catch(Throwable e) { send(ownerTid, cast(shared)e); }}

    //---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%
    //
    //                               Private
    //
    //---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%

    /// Own Tid.
    private Tid myTid_;

    /// Caldron instance to call to call the Caldron._processMessage() function.
    private NewCaldron caldron_;

    /// Message to itself to terminate.
    private class TerminationRequest {}
}













