module proj_funcs;
import std.stdio;
import std.format, std.typecons;

import proj_data;

/// External runtime function, that creates a new object by its ClassInfo. No constructors are called, though static
/// initialisation is done. Very fast. Much faster than manually allocate an object on the heap as new buf[], as ehe emplace
/// function does. Used in the SpiritConcept.clone() method and when restoring serialized classes from DB.
extern (C) Object _d_newclass (ClassInfo info);

//---***---***---***---***---***--- functions ---***---***---***---***---***--

/**
        Safe cast. It will throw an assertion if the object cannot be casted as opposed to silent returning null by the
    cast operator (for classes). Unlike the "type" template, you don't need to worry about having proper types declared around
    this template. Types that a passed as parameters to template are self sufficient. They appear in the template as if
    they were declared in the parameter line. (In the type template parameter is string, so it cannot declare a type);
    Parameters:
        T = type to cast to
        o = object to cast
    Return: casted object or an assert happens if the object cannot be casted
*/
T scast(T, S)(S o)
    if ((is(T: Object) || is(T: shared Object) || is(T: immutable Object) || is(T: const Object)
            || is(T: const shared Object) || is(T == interface))
            && (is(S: Object) || is(S: shared Object) || is(S: immutable Object) || is(S: const Object)
            || is(S: const shared Object)))
{
    assert(cast(T)o, format!"Object %s cannot be casted to class(interface) %s"(typeid(o), T.stringof));
    return cast(T)o;
}
///
unittest {
    debug {
        //A a = new A;
        ClB b = new ClB;
        scast!ClA(b);
        scast!I(b);
        //scast!I(a);   // will throw an assert
        //scast!B(a);   // will throw an assert
    }
}
version(unittest) { // test classes for the previous unittest
    interface I {}
    class ClA {}
    class ClB: ClA, I {}
}

/**
        Convert name of type represented as a string to real type. Don't forget, the type you a trying to instantiate must
    exist, i.e. be defined or imported or to be a basic type. So, sometimes you will need to copy-paste this template in
    your module.
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

    assert(is(type!"int" == int));
}

/**
        Test for a given type.
    Parameters:
        S = type to test
        T = type to test against
*/
enum bool isOf(S, T) = is(S == T);
///
unittest {
    assert(isOf!(shared int, shared int));
    assert(isOf!(int[], int[]));
}

/**
        Test for an array of a given type.
    Parameters:
        S = type to test
        T = type of array element
*/
enum bool isArrayOf(S, T) = is(S : T[]);
///
unittest {
    assert(isArrayOf!(int[], int));
    assert(!isArrayOf!(int[], long));
}

/**
        Tool for pretty output of variables and expressions. It is a CTFE function, used for creating a string, that is
    going to be mixined into the code. Converts a list of expressions into a block of code, which writelns those exps.

    Using: mixin("&ltcomma separated list of expressions&gt".w);
    Parameters:
       expLst = list of expressions separated by commas
    Returns: string, that contains a block of code, which outputs the expression titles and values. It is supposed to be
        mixed into the source code.
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

/**
        This function illustrates cloning a D object. It makes a shallow binary copy.
    Written by Burton Radons <burton-radons smocky.com> https://digitalmars.com/d/archives/digitalmars/D/learn/1625.html
    Tested against memory leaks in the garbage collector both if a reference to the object is dropped and if in its body
    was a reference to other object. For creating a new instance on the runtime info is used the D runtime function
    _d_newclass(). This method is significantly faster than using the implace template (which eventually calls the
    _d_newclass() function anyway). Well done, Barton!
    Parameters:
        srcObject = object to clone
    Returns: cloned object
*/
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
        Serialize an arbitrary array into a byte buffer. Works in pair with the deserializeArray() function.
    Note: null or empty arrays after serialization/deserialization becomes null.
    Parameters:
        ar = array to serialize
    Returns: byte array with first Cind as the length and the rest as the elements of the array.
*/
pure nothrow const(byte[]) serializeArray(T)(const T[] ar) {
    byte[] rs;

    size_t len = Cind.sizeof + ar.length*T.sizeof;    // calculate required space
    rs.length = len;        // allocate
    Cind ofs;

    // Put into buffer length of the source array as Cind
    *cast(Cind*)&rs[ofs] = cast(Cind)ar.length;
    ofs += Cind.sizeof;

    // Put array content
    foreach(i, e; ar) {
        *cast(T*)&rs[ofs] = e;
        ofs += T.sizeof;
    }

    return rs;
}

/**
        Deserialize a byte buffer into an array of given type. Works in pair with the serializeArray() function.
    Note: null or empty arrays after serialization/deserialization becomes null.
    Parameters:
        T = type of the array elements.
        buf = byte buffer with the serialization data. Length (Cind) of the array goes first and then its elements.
    Returns: Tuple, the first element of which is the deserialized array and the second one is the unconsumed rest of the
        byte buffer.
*/
pure Tuple!(T[], "array", const byte[], "restOfBuffer") deserializeArray(T: T[])(const byte[] buf) {
    assert(buf.length >= Cind.sizeof, format!"Buffer must be at least %s bytes and it is %s"(Cind.sizeof, buf.length));

    Cind ofs;
    size_t len = *cast(T*)&buf[ofs];        // length of the array
    ofs += Cind.sizeof;

    // Check for emptiness. If empty return null array and consumed by Cind.sizeof buffer
    if(len == 0) return tuple!(T[], "array", const byte[], "restOfBuffer")(null, buf[ofs..$]);

    assert(Cind.sizeof + len*T.sizeof <= buf.length,
            format!"The buf of length %s is not enough to contain %s elements of type %s"
            (buf.length, len, T.stringof));

    T[] rs;
    rs.length = len;    // allocate
    foreach(i; 0..len) {
        rs[i] = *cast(T*)&buf[ofs];
        ofs += T.sizeof;
    }

    return tuple!(T[], "array", const byte[], "restOfBuffer")(rs, buf[ofs..$]);
}

unittest{
    Cid[] a = [1, 2, 3];
    const byte[] ser = serializeArray(a);
    auto dser = deserializeArray!(Cid[])(ser);
    assert(a == dser.array);

    a = null;   // null goes to null
    assert(deserializeArray!(Cid[])(serializeArray(a)).array is null);

    a = [];   // empty goes to null
    assert(deserializeArray!(Cid[])(serializeArray(a)).array is null);
}

/// test class for unittests
version(unittest) {
    private class ClassA {
        int x;
        this(int i) {
            x = i;
        }
    }
}
