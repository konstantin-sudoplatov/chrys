module base_proba;
import proj_memoryerror;
import std.stdio: writeln, writefln, stdout;
import std.string, std.typecons, std.algorithm;

import base_proba2;

// Show in console that it is the unittest mode
version(unittest) {
    pragma(msg, "Unittest");
}

// We need an executable in debug and unittest modes in order to run the Gnu Debugger on.
debug void main() {
 [new A(1), new A(2), new A(3), new A(4)].canFind!(cid => cast(B)cid is null).writeln;
}

class A {
    int k;
    this(int k) {
        this.k = k;
    }
}

class B {

}