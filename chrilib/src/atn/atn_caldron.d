module atn.atn_caldron;
import std.stdio, std.string;
import core.thread, std.concurrency, std.exception;

import proj_data, proj_funcs;

import chri_types;
import messages;
import cpt.abs.abs_concept, cpt.abs.abs_neuron;
import cpt.cpt_neurons, cpt.cpt_actions, cpt.cpt_premises;
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

    //---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%
    //
    //                               Private
    //
    //---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%---%%%

    /// Seed for this caldron.
    private Cid seedCid_;

    /// Caldrons, that were parented here.
    private Tid[] childCaldrons_;

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
                Concept cpt = this[cid];
                if      // is it a breed?
                        (auto breed = cast(Breed)cpt)
                {   //yes: spawn the new branch

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
            }

        }

    }
}

/// Fiber pool
import proj_types;
import chri_data;
synchronized class FiberPool {

    /**
            Get stacked or generate new fiber for the Caldron.reasoning_() delegate.
        Parameters:
            cld = caldron to setup the fiber to
        Returns: the fiber object.
    */
    Fiber pop(NewCaldron cld) {
        if      // is the stack empty?
                ((cast()fibers_).empty)
        {
            Fiber fbr = new Fiber(&cld.reasoning_);
            return fbr;
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
        if((cast()fibers_).length <= FIBER_POOL_SIZE) (cast()fibers_).push(fiber);
    }

    /// Stack of fibers.
    private Deque!(Fiber) fibers_;
}

shared struct CaldronThread {

    int i;

    this(int i) {this.i = i;}

    void threadFunc() {
        writefln("i = %s", i);
    }

    private Tid tid;
}

unittest {
    shared static CaldronThread ct1 = CaldronThread(1);
    shared static CaldronThread ct2 = CaldronThread(2);
    spawn(&ct1.threadFunc);
    spawn(&ct2.threadFunc);
}













