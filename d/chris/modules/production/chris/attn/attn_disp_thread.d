module attn.disp_thread;
import std.concurrency;
import std.variant;
import std.format;

import tools;
import global;
import messages;

/// Cross-map of the attention clients-circles.
private struct TidCross {
    Tid[Tid] circles_;    /// Map of attention circles threads: <circle Tid>[<client Tid>]
    Tid[Tid] clients_;    /// Map of attention clients threads: <client Tid>[<circle Tid>]

    /**
            Getter.
        Parameters:
            client = Tid of attention client.
        Returns: Tid of attention circle.
        Throws: Range error exception if key is not found.
    */
    Tid circle_(Tid client) {
        return circles_[client];
    }

    /**
            Getter.
        Parameters:
            circle = Tid of attention circle.
        Returns: Tid of attention client.
        Throws: Range error exception if key is not found.
    */
    Tid client_(Tid circle) {
        return clients_[circle];
    }

    /**
            Setter.
        Parameters:
            circle = Tid of attention circle.
            client = Tid of attention client.
    */
    void circle_(Tid circle, Tid client) {
        circles_[client] = circle;
        clients_[circle] = client;
    }
}

/// Cross map for client/user Tids.
TidCross tidCross;

/**
    Attention dispatcher controls creation and delition of attention circles. One, for example, must be created when the first
    message comes from the console.
*/
void attn_dispatcher() {try {   // catchall try block for catching flying exceptions and forwarding them to the owner thread.

    // Receive messages in a cycle
    while(true) {
        immutable Msg msg;                    // regular message
        Variant var;                // the catchall type

        receive(
            (immutable Msg m){cast()msg = cast() m;},
            (Variant v){var = v;}
        );

        if      // is it a regular message?
                (msg)
        {   // process it
            if      // does client request circle's Tid?
                    (cast(ClientRequestsCircleTidFromDisp)msg)
            {   //yes: TODO: write function creating(if not exists) circle and send back its Tid
                continue;
            }
            else if // TerminateAppMsg message has come?
                    (cast(TerminateAppMsg)msg) // || var.hasValue)
            {   //yes: terminate me and all my subthreads
                // TODO: terminate subthreads
                goto FINISH_THREAD;
            }

        }
        else if // has come an unexpected message?
            (var.hasValue)
        {   // log it
            logit(format!"Unexpected message to the attention dispatcher thread: %s"(var.toString));
            continue;
        }
    }



FINISH_THREAD:
} catch(Throwable e) {
    (cast()_mainTid_).send(cast(shared)e);
}
}