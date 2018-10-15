module base_proba;
import std.stdio: writeln, writefln;

// Show in console that it is the unittest mode
version(unittest) {
    pragma(msg, "Unittest");
}

// We need an executable in debug and unittest modes in order to run the Gnu Debugger on.
debug void main() {
    string[int] a;
    a[1] = "one";
    string* p = 1 in a;
    writeln(*p);
}
