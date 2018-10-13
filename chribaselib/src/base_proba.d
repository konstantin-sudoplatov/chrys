module base_proba;
import std.stdio: writeln, writefln;

// Show in console that it is the unittest mode
version(unittest) {
    pragma(msg, "Unittest");
}

// We need an executable in debug and unittest modes in order to run the Gnu Debugger on.
debug void main() {
    import tools;
    //auto a = cast(A)_d_newclass(typeid(A));
    auto a = cast(A)typeid(A).factory("base_proba.A");
    a.foo;
    writeln(a.x);
}

class A {
    int x = 1;
    void foo() {
        writeln("here");
    }
}