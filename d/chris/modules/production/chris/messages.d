module messages;

/// Common ancestor of all messages.
abstract class Msg {}

/// Request for termination of application, usually from console.
class TerminateAppMsg: Msg {}

/// Message to display on the console, usually by an attention circle.
class SayConsoleMsg: Msg {

    /// Getter
    @property string text() {
        return _text;
    }

    /// Getter
    @property string text(string text) {
        return _text = text;
    }

private:
    string _text;   /// text to display
}

/// Request for a new line from console, usually by an attention circle.
class AskConsoleMsg: Msg {}
