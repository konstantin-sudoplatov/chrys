module attn.attn_dispatcher_thread;
import std.concurrency;
import std.format;

import tools;
import global;
import messages;
import attn.attn_circle_thread;

/**
        Thread function for attention dispatcher.
*/
void attention_dispatcher_thread() {try {   // catchall try block for catching flying exceptions and forwarding them to the owner thread.
    import std.variant: Variant;

    // Receive messages in a cycle
    while(true) {
        Msg msg;                    // regular message
        Variant var;                // the catchall type

        receive(
            (immutable Msg m){msg = cast()m;},
            (Variant v){var = v;}
        );

        if      // is it a regular message?
                (msg)
        {   // process it
            if      // does client request circle's Tid?
            (auto m = cast(immutable ClientRequestsCircleTidFromDisp)msg)
            {   //yes: create new attention circle thread and send back its Tid
                Tid clientTid = cast()m.sender_tid();

                // the first time create the attention dispatcher object
                if
                        (!attnDisp_)
                    attnDisp_ = new AttentionDispatcher();

                // spawn and cross it
                Tid circleTid = attnDisp_.createCircleAttentionThread(clientTid);

                // give client and circle Tids of the correspondent
                (clientTid).send(new immutable DispatcherSuppliesClientWithCircleTid(circleTid));
                (circleTid).send(new immutable DispatcherSuppliesCircleWithClientTid(clientTid));

                continue;
            }
            else if // TerminateAppMsg message has come?
            (cast(TerminateAppMsg)msg) // || var.hasValue)
            {   //yes: terminate me and all my subthreads
                // TODO: terminate subthreads
                goto FINISH_THREAD;
            }
            else {  // unrecognized message of type Msg. Log it.
                logit(format!"Unexpected message to the attention dispatcher thread: %s"(msg));
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
} catch(Throwable e) { ownerTid.send(cast(shared)e); } }

//###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
//
//                               Private
//
//###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
private:
//---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

/// The attention dispatcher object
AttentionDispatcher attnDisp_;

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--

/**
        This class contain functionality on creation attention circle threads and linking them to the clients. All workflow starts
    in the thread function. Dispatcher doesn't have direct access of the circles. They are located in the thread-local memory
    of thir threads and cannot be accessed from the outside. All interconnections are through the message system.
*/
class AttentionDispatcher {

    //---***---***---***---***---***--- types ---***---***---***---***---***---***

    //---***---***---***---***---***--- data ---***---***---***---***---***--

    this(){}

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                            Public
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    private:
    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

    /// Cross map for client/user Tids.
    TidCross tidCross_;

    //---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

    /**
                    Create and start an attention circle thread if it doesn't exist yet and return its Tid.
        Parameters:
            clientTid = Tid of the client, that corresponds with that circle.

        Returns: circle's Tid.
    */
    Tid createCircleAttentionThread(Tid clientTid) {
        if      // is client in the cross already?
                (auto circleTid = tidCross_.client_in(clientTid))
        {   //yes: return the Tid
            return cast()*circleTid;
        }
        else {  //no: create the circle, tell him the client's Tid and put the pair in the cross
            Tid circleTid = spawn(&attn_circle_thread);
            circleTid.send(new immutable DispatcherSuppliesCircleWithClientTid(clientTid));
            tidCross_.add(clientTid, circleTid);

            return circleTid;
        }
    }

    //---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--

    /// Cross-map of the attention clients-circles.
    pure struct TidCross {
        Tid[Tid] circles_;      /// Map of attention clients threads: <client Tid>[<circle Tid>]
        Tid[Tid] clients_;      /// Map of attention circles threads: <circle Tid>[<client Tid>]

        /**
                Check the length of the cross map.
            Returns: number of pairs in the map.
        */
        ulong length() {
            return circles_.length;
        }

        /**
                Check if client is already present and return pointer to the circle Tid. Analogous to the D "in" statement.
        */
        const(Tid*) client_in(Tid client) const {
            return client in circles_;
        }

        /**
                Check if client is already present and return pointer to the circle Tid. Analogous to the D "in" statement.
        */
        const(Tid*) circle_in(Tid circle) const {
            return circle in clients_;
        }

        /**
                Getter.
            Parameters:
                client = Tid of attention client.
            Returns: Tid of attention circle.
            Throws: Range error exception if key is not found.
        */
        const(Tid) circle_(Tid client) const {
            return circles_[client];
        }

        /**
                Getter.
            Parameters:
                circle = Tid of attention circle.
            Returns: Tid of attention client.
            Throws: Range error exception if key is not found.
        */
        const(Tid) client_(Tid circle) const {
            return clients_[circle];
        }

        /**
                Add a pair <client Tid>/<circle Tid>.
            Parameters:
                client = Tid of attention client.
                circle = Tid of attention circle.
        */
        void add(Tid client, Tid circle) {
            assert(client !in circles_ && circle !in clients_);
            circles_[client] = circle;
            clients_[circle] = client;
            assert(client in circles_ && circle in clients_);
        }

        /**
                Remove pair <client Tid>/<circle Tid>.
            Parameters:
                client = Tid of attention client.
                circle = Tid of attention circle.
        */
        void remove(Tid client, Tid circle) {
            assert(client in circles_ && circle in clients_);
            circles_.remove(client);
            clients_.remove(circle);
            assert(client !in circles_ && circle !in clients_);
        }

        invariant {
            assert(circles_.length == clients_.length);
            foreach(cir, cl; clients_) {
                assert(circles_[cast()clients_[cast()cir]] == cast(const)cir);  // we need casts because invariant is the const attribute by default
            }
            foreach(cl, cir; circles_) {
                assert(clients_[cast()circles_[cast()cl]] == cast(const)cl);  // we need casts because invariant is the const attribute by default
            }
        }
    }   // struct TidCross
}
