module messages;
import std.concurrency;

/// Common ancestor of all messages.
/// Object must be created in the sender's thread, so that sender's tid was correctly set up. Do not create message objects beforehand!
immutable abstract class Msg {

    /// Constructor. Uses the current Tid as sender's.
    this() {
        // We assume that the object is created in the sender's thread (probably, right in the call of the send() function.
        _senderTid = cast(immutable)thisTid;
    }

    /// Getter
    @property immutable(Tid) _senderTid_() immutable {
        return _senderTid;
    }

protected:
    Tid _senderTid;
}

/// Request for termination of application, usually from console_thread, after that from the main thread over all the structure.
immutable class TerminateAppMsg: Msg {
    /// Constructor
    this() {super();}
}

/// Request for the attention dispatcher start an attention circle thread and send back its Tid.
immutable class ClientRequestsCircleTidFromDisp: Msg {
    /// Constructor
    this() {super();}
}

/// In response to the client request dispatcher creates an attention circle thread and gives back its Tid.
/// If the circle exists already, it just returns its Tid.
immutable class DispatcherSuppliesClientWithCircleTid: Msg {

    /**
        Constructor.
        Parameters:
            tid - circle's Tid
    */
    this(Tid tid) {
        super();
        tid_ = cast(immutable)tid;
    }

    /// Getter
    @property Tid _tid_() immutable {
        return cast()tid_;
    }

private:
    Tid tid_;       // circle's Tid
}

/// In response to the client request dispatcher creates an attention circle thread and sends it the client Tid.
immutable class DispatcherSuppliesCircleWithClientTid: Msg {

    /**
        Constructor.
        Parameters:
            tid - client's Tid
    */
    this(Tid tid) {
        super();
        tid_ = cast(immutable)tid;
    }

    /// Getter
    @property Tid _tid_() immutable {
        return cast()tid_;
    }

private:
    Tid tid_;       // circle's Tid
}

/// Message to display on the console_thread, usually by an attention circle.
immutable class CircleSaysToUserMsg: Msg {

    /**
        Constructor.
        Parameters:
            text - line of text to send
    */
    this(string text) {
        super();
        text_ = text;
    }

    /// Getter
    @property string _text_() immutable {
        return text_;
    }

private:
    string text_;   // text to display
}

/// Request for a new line from console_thread, usually by an attention circle.
immutable class CircleListensToUserLineMsg: Msg {
    /// Constructor
    this() {super();}
}

/// Console sends a line of text to an attention circle
immutable class UserSaysToCircleMsg: Msg {

    /**
        Constructor.
        Parameters:
            text - line of text to send
    */
    this(string text) {
        super();
        text_ = text;
    }

    /// Getter
    @property string _text_() immutable {
        return text_;
    }

private:
    string text_;   // text to send
}

/// Request to caldron to start reasoning. May come from another caldron and sometimes from itself.
immutable class StartReasoningMsg: Msg {
    this() {super();}
}