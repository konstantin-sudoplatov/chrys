module atn.atn_circle_thread;
import std.concurrency, std.format;

import proj_data, proj_funcs;

import atn.atn_caldron;
import chri_types, chri_data;
import cpt.abs.abs_concept, cpt.abs.abs_neuron;
import cpt.cpt_neurons, cpt.cpt_premises, cpt.cpt_actions, cpt.cpt_interfaces;
import messages;

/// The attention circle/caldron object, thead local.
AttentionCircle circle;

/// The debug level switch, controlled from the conceptual level.
int dynDebug = 0;

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

/**
        Thread function the attention circle. Called from the attention dispatcher.
*/
void circleThreadFunc() {try{

    circle = new AttentionCircle();

    if (dynDebug >= 1) logit("starting the attention circle thread", TermColor.brown);

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
                    (circle._processMessage(msg))
                //yes: go for a new message
                continue ;
            else if // is it a request for the circle termination?
                    (cast(TerminateApp_msg)msg)
            {   //yes: terminate me and all my subthreads
                if (dynDebug >= 1)
                    logit("terminating caldron " ~ circle.cldName);
                circle.terminateChildren;

                // terminate itself
                goto FINISH_THREAD;
            }
            else
            {  // unrecognized message of type Msg. Log it.
                logit(format!"Unexpected message to the caldron %s: %s"
                        (circle.cldName, typeid(msg)), TermColor.brown);
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
                    (circle.cldName, var.toString), TermColor.brown);
            continue;
        }

    }
    FINISH_THREAD:
} catch(Throwable e) { ownerTid.send(cast(shared)e); } }

//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
