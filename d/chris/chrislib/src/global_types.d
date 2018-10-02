module global_types;

import std.format;

// Show in console_thread, that it is the unittest mode
version(unittest) {
    pragma(msg, "Unittest");
}

//---***---***---***---***---***--- types ---***---***---***---***---***---***

/// Concept identifier
alias Cid = uint;

/// Static cid range is from 1 to MAX_STATIC_CID;
enum MIN_STATIC_CID = Cid(1);
enum MAX_STATIC_CID = Cid(1_000_000);
enum MIN_DYNAMIC_CID = Cid(2_000_000);
enum MAX_DINAMIC_CID = Cid.max;
static assert(MIN_DYNAMIC_CID > MAX_STATIC_CID);
enum MIN_TEMP_CID = MAX_STATIC_CID + 1;
enum MAX_TEMP_CID = MIN_DYNAMIC_CID - 1;
static assert(MAX_TEMP_CID >= MIN_TEMP_CID);

/// Call types of the static concept (static concept is a function).
enum StatCallType: string {
    p0Cal = "void function(Caldron)",                              // void function(Caldron nameSpace)
    p0Calp1Cid = "void function(Caldron, Cid)",                         // void function(Caldron nameSpace, Cid operandCid)
    p0Calp1Cidp2Cid = "void function(Caldron, Cid, Cid)",                    // void function(Caldron nameSpace, Cid firstoperandCid, Cid secondOperandCid)
    p0Calp1Cidp2Int = "void function(Caldron, Cid, int)",                    // void function(Caldron nameSpace, Cid conceptCid, int intValue)
    p0Calp1Cidp2Float = "void function(Caldron, Cid, float)",                  // void function(Caldron nameSpace, Cid conceptCid, float floatValue)
    p0Calp1Cidp2Cidp3Float = "void function(Caldron, Cid, Cid, float)",             // void function(Caldron nameSpace, Cid branchBreedCid, Cid conceptCid, float floatValue)
}

/**
        Convert name of type represented as a string to real type. Don't forget, the type you a trying to instantiate must
    exist, i.e. be imported or be a basic type.
    Parameters:
        typeName = name of type
*/
template type(string typeName) {
    mixin("alias type = " ~ typeName ~ ";");
}

///
unittest {
    auto a = new type!"ClassA"(42);
    assert(a.x == 42);

    type!"int" i;
    assert(is(typeof(i) == int));
}

//---***---***---***---***---***--- data ---***---***---***---***---***--

//---***---***---***---***---***--- functions ---***---***---***---***---***--

/**
        Safe cast. It will throw an assertion if the object cannot be casted as opposed to silent returning null by the
    cast operator (for classes).
    Parameters:
        T = type to cast to
        o = object to cast
    Return: casted object or assert happens
*/
T scast(T, S)(S o)
    if ((is(T: Object) || is(T: shared Object) || is(T: immutable Object) || is(T == interface))
        && ((is(S: Object) || is(S: shared Object) || is(S: immutable Object))))
{
    assert(cast(T)o, format!"Object %s cannot be casted to class(interface) %s"(typeid(o), T.stringof));
    return cast(T)o;
}
///
unittest {
    debug {
        A a = new A;
        B b = new B;
        scast!A(b);
        scast!I(b);
        //scast!I(a);   // will throw an assert
        //scast!B(a);   // will throw an assert
    }
}
version(unittest) { // test classes for the previous unittest
    interface I {}
    class A {}
    class B: A, I {}
}

/**
            Clone an object.
        It makes a shallow copy of an object.
    Note, the runtime type of the object is used, not the compile (declared) type.
        Written by Burton Radons <burton-radons smocky.com>
        https://digitalmars.com/d/archives/digitalmars/D/learn/1625.html
        Tested against memory leaks in the garbage collecter both via copied object omission and omission of reference to other
    object in the its body.
    Parameters:
        srcObject = object to clone
    Returns: cloned object
*/
private extern (C) Object _d_newclass (ClassInfo info);
Object clone (Object srcObject)
{
    if (srcObject is null)
        return null;

    void *copy = cast(void*)_d_newclass(srcObject.classinfo);
    size_t size = srcObject.classinfo.initializer.length;
    copy [8 .. size] = (cast(void *)srcObject)[8 .. size];
    return cast(Object)copy;
}

/**
        Tool for pretty output of variables and expressions.
    Compile time function. Converts a list of expressions into a block of code, wich outputs those exps.

    Using: mixin("&ltcomma separated list of expressions&gt".w);
    Parameters:
       expLst = list of expressions separated by commas
    Returns: string, that contains a block of code, which outputs the expression titles and values. It is and intended to be mixed
        into the source code.
*/
string w(string expLst) {
    import std.string: split, strip;
    import std.format: format;
    string[] asExp = expLst.split(",");
    string sRes = "import std.stdio: writeln, stdout;\n";
    foreach(s; asExp) {
        s = s.strip;
        sRes ~= format(q{writeln("%s: ", typeid(typeof(%s)), " = ", %s);}, s, s, s) ~ "\n";
    }
    sRes ~= "stdout.flush;\n";

    return sRes;
}

unittest {
    const int i = 1;
    int j = 2;
    const int* p = &j;

    assert("i, *p".w == `import std.stdio: writeln, stdout;
writeln("i: ", typeid(typeof(i)), " = ", i);
writeln("*p: ", typeid(typeof(*p)), " = ", *p);
stdout.flush;
`
    );

    assert("i+(*p)".w, `import std.stdio: writeln, stdout;
writeln("i+(*p): ", typeid(typeof(i+(*p)), " = ", i+(*p));
stdout.flush;
`
    );
}
///
unittest {
    string s = "Purpose of life";
    int k = 42;
    int* p = &k;

/*
    mixin("`w example`, s, k, *p, typeid(s)".w);

    Prints:
        `w example`: immutable(char)[] = w example
        s: immutable(char)[] = Purpose of life
        k: int = 42
        *p: int = 42
        typeid(s): TypeInfo_Array = immutable(char)[]
*/
}

/// ANSI terminal colours
enum TermColor: string {
    none = null,
    black = "0;30",
    red = "1;31",
    green = "1;32",
    brown = "1;33",
    blue = "1;34",
    purple = "1;35",
    cyan = "1;36",
    gray = "1;30",
}

/**
    Logging facility.
Params:
    text = the text of the message
    color = the color to change the output to
*/
void logit(string text, TermColor color = null) {
    import std.stdio: write, writeln, stdout;

    if (color)
        write("\x1b[" ~ color ~ "m");      // make the colour green
    write(text);
    if (color) write("\x1b[0m");           // clear the terminal settings
    writeln;
    stdout.flush;
}

/// Adapter
void logit(const Object o, TermColor color = null) {
    logit((cast()o).toString, color);
}

//---***---***---***---***---***--- types ---***---***---***---***---***--

/// test class for various unittests
version(unittest) class ClassA {
    int x;
    this(int i) {
        x = i;
    }
}


//^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
//
//                            Public
//
//v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^


//===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
//
//                                  Package
//
//===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
package:
//---@@@---@@@---@@@---@@@---@@@--- data ---@@@---@@@---@@@---@@@---@@@---@@@---

//---@@@---@@@---@@@---@@@---@@@--- functions ---@@@---@@@---@@@---@@@---@@@---@@@---@@@-

//---@@@---@@@---@@@---@@@---@@@--- types ---@@@---@@@---@@@---@@@---@@@---@@@---@@@-

//~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
//
//                                 Protected
//
//~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
protected:
//---$$$---$$$---$$$---$$$---$$$--- data ---$$$---$$$---$$$---$$$---$$$--

//---$$$---$$$---$$$---$$$---$$$--- functions ---$$$---$$$---$$$---$$$---$$$---

//---$$$---$$$---$$$---$$$---$$$--- types ---$$$---$$$---$$$---$$$---$$$---

////
//                               Private
//
//private:
//---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
