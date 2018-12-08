module base_proba;
import proj_memoryerror;
import std.stdio: writeln, writefln, stdout;
import std.string, std.typecons, std.algorithm;
import std.concurrency, core.thread;

import base_proba2;

// Show in console that it is the unittest mode
version(unittest) {
    pragma(msg, "Unittest");
}

// We need an executable in debug and unittest modes in order to run the Gnu Debugger on.
debug void main() {
}

