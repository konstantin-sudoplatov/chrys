module base_proba;
import std.stdio: writeln, writefln;

// Show in console that it is the unittest mode
version(unittest) {
    pragma(msg, "Unittest");
}

// We need an executable in debug and unittest modes in order to run the Gnu Debugger on.
debug void main() {
    import std.typecons;

    Tuple!(const int[], "fieldName") a = ([1, 2]);
    pragma(msg, typeof(a));

}
