module base_proba;
import std.stdio: writeln, writefln;

// Show in console that it is the unittest mode
version(unittest) {
    pragma(msg, "Unittest");
}

// We need an executable in debug and unittest modes in order to run the Gnu Debugger on.
debug void main() {
    import std.stdio, std.algorithm;

    double[char] bids = ['A': 37.50,
                         'B': 38.11,
                         'C': 36.12];

    bids.byValue.reduce!(min, max).writeln;
}
