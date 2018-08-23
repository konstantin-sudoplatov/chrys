module messages;
import std.concurrency;

/// Common ancestor of all messages.
/// Object must be created in the sender's thread, so that sender's tid was correctly set up. Do not create message objects beforehand!
immutable abstract class Msg {

protected:
    Tid _senderTid;

public:
    /// Constructor. Uses the current Tid as sender's.
    this() {
        // We assume that the object is created in the sender's thread (probably, right in the call of the send() function.
        _senderTid = cast(immutable)thisTid;
    }

    /// Getter
    @property immutable(Tid) sender_tid() immutable {
        return _senderTid;
    }
}

/// Request for termination of application, usually from console_thread.
immutable class TerminateAppMsg: Msg {
    /// Constructor
    this(){
        super();
    }
}

/// Message to display on the console_thread, usually by an attention circle.
immutable class CircleSaysToConsoleMsg: Msg {

private:
    string text_;   /// text to display

public:
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
    @property string text() immutable {
        return text_;
    }
}

/// Request for a new line from console_thread, usually by an attention circle.
immutable class CircleListensToConsoleMsg: Msg {
    /// Constructor
    this(){
        super();
    }
}

/// Console sends a line of text to an attention circle
immutable class ConsoleSaysToCircleMsg: Msg {

private:
    string text_;   /// text to send

public:
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
    @property string text() immutable {
        return text_;
    }
}