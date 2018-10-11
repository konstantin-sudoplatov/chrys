module base_proba;
import std.stdio;

// Show in console that it is the unittest mode
version(unittest) {
    pragma(msg, "Unittest");
}

// We need an executable in debug and unittest modes in order to run the Gnu Debugger on.
debug void main() {

    foreach(i; 0..3)
        writeln(aaa[i]().hello);
    writeln(aaa.ptr);
}

static immutable A function()[3] aaa = [()=> new A, ()=> new B, ()=> new C];


//@(1, aaa)
class A {
    string hello() {
        return "hello from A";
    }
}
class B: A {
    override string hello() {
        return "hello from B";
    }
}
class C: A {
    override string hello() {
        return "hello from C";
    }
}
