module tools_pile;
import std.stdio;
import std.format;

/// Private exception for the project.
//class Crash: Error {
//    @nogc @safe pure nothrow this(string file = __FILE__, size_t line = __LINE__, Throwable nextInChain = null)
//            { super("", file, line, nextInChain); }
//
//    @nogc @safe pure nothrow
//            this(string msg, string file = __FILE__, size_t line = __LINE__, Throwable nextInChain = null)
//            { super(msg, file, line, nextInChain); }
//}

/**
        Convert type represented by a string to real type. Don't forget, the type you a trying to instantiate must
    exist, i.e. to be imported or to be the basic type.
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
/// test class for the previous unittest
class ClassA {
    int x;
    this(int i) {
        x = i;
    }
}

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
debug { // test classes for the previous unittest
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
                Roll a list of Cid enums into a single anonymous enum of type Cid. CTFE.
        The result of this function is supposed to be mixined into a function that use those enums.
    Used simplification of writing the crank functions. Nested "with" statement can be as well use instead of that
    mixin, but this implementation seems to looke cleaner. Do not forget to use imports for the source enums.
    Parameters:
        enumList = list of enums of type Cid
    Returns: string, containing the resulting enum statement, ready to be mixed in the code.
*/
string dequalify_enums(enumList...)() {
    import global: CptDescriptor;
    string res = "enum : CptDescriptor {\n";
    static foreach (enuM; enumList)     // each enum en in the list of enums
    {
        static assert(is(enuM == enum) && is(enuM: CptDescriptor));
        static foreach(enEl; __traits(allMembers, enuM))         // each enum element
            res ~= "    " ~ enEl ~ " = " ~ enuM.stringof ~ "." ~ enEl ~ ",\n";
    }

    return res ~ "}\n";
}

unittest {
    import std.conv: asOriginalType;
    import global: CptDescriptor, cd, MAX_STATIC_CID;
    import cpt_neurons: SpSeed;
    import cpt_premises: SpBreed;
    enum CommonConcepts: CptDescriptor {
        chat_seed = cd!(SpSeed, 2_500_739_441),                  // this is the root branch of the chat
        do_not_know_what_it_is = cd!(SpSeed, 580_052_493),
    }

    enum Chat: CptDescriptor {
        console_breed = cd!(SpBreed, 4_021_308_401),
        console_seed = cd!(SpSeed, 1_771_384_341),
    }

    // Declare enums with the same members as in the CommonConcepts and Chat
    mixin(dequalify_enums!(CommonConcepts, Chat));
    assert(chat_seed == CommonConcepts.chat_seed);
    assert(do_not_know_what_it_is == CommonConcepts.do_not_know_what_it_is);
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
    import tools_pile: w;
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
    import std.stdio: writeln, stdout;

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

//---***---***---***---***---***--- types ---***---***---***---***---***---***

//---***---***---***---***---***--- data ---***---***---***---***---***--

/**
        Constructor
*/
//this(){}

//---***---***---***---***---***--- functions ---***---***---***---***---***--

//###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
//
//                               Package
//
//###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
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

//===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
//
//                                  Private
//
//===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
private:
//---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
