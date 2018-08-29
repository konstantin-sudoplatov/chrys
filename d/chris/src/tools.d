module tools;
import std.stdio;

/// Private exception of the project.
class Crash: Exception {
    @nogc @safe pure nothrow this(string msg, string file = __FILE__, size_t line = __LINE__, Throwable nextInChain = null)
    {
        super(msg, file, line, nextInChain);
    }

    @nogc @safe pure nothrow this(string msg, Throwable nextInChain, string file = __FILE__, size_t line = __LINE__)
    {
        super(msg, file, line, nextInChain);
    }
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
            Cross-map.
    It is a pair of associated arrays FirstT[SecondT] and its reverse SecondT[firstT]. For example it may contain pairs of
    &ltconcept name&gt/&ltCid&gt, so that we can find any Cid by name and name by Cid. For any entry in the first AA there always is
    one corresponding entry in the second AA. By definition this cross is always symmetrical.
*/
pure nothrow struct CrossMap(FirstT, SecondT) {

    /**
                Check the length of the cross map.
            Returns: number of pairs in the map.
    */
    auto length() {
        return cids.length;
    }

    /**
            Check if the first key present in the cross. Analogous to the D "in" statement.
    */
    const(SecondT*) first_in(FirstT first) const {
        return first in names;
    }

    /**
            Check if the second key present in the cross. Analogous to the D "in" statement.
    */
    const(FirstT*) second_in(SecondT second) const {
        return second in cids;
    }

    /**
            Get the second key by the first one.
        Parameters:
            second = the second key.
        Returns: the first key.
        Throws: RangeError exception if the key not found.
    */
    const(FirstT) first(SecondT second) const {
        return cids[second];
    }

    /**
            Get the first key by the second one.
        Parameters:
            first = the first key
        Returns: the second key.
        Throws: RangeError exception if the key not found.
    */
    const(SecondT) second(FirstT first) const {
        return names[first];
    }

    /*
            Get the range of the first keys.
        Returns: range of the firsts.
    */
    auto firsts() {
        return names.byKey;
    }

    /*
            Get the range of the second keys.
        Returns: range of the seconds.
    */
    auto seconds() {
        return cids.byKey;
    }

    /**
            Add a pair &ltfirst key&gt/&ltsecond key&gt.
        Parameters:
            first = the first key
            second = the second key
    */
    void add(FirstT first, SecondT second) {
        assert(second !in cids && first !in names, "Keys are already in the map. We won't want to have assimetric maps.");     // if not, we risk having assimetric maps.
        cids[second] = first;
        names[first] = second;
        assert(second in cids && first in names);
    }

    /**
            Remove pair &ltfirst key&gt/&ltsecond key&gt. If there is no such pair, nothing happens.
        Parameters:
            first = the first key
            second = the second key
    */
    void remove(FirstT first, SecondT second) {
        cids.remove(second);
        names.remove(first);
        assert(second !in cids && first !in names);
    }

    invariant {
        assert(cids.length == names.length);
        foreach(first; names.byKey) {
            assert(cast(FirstT)cids[cast(SecondT)names[cast(FirstT)first]] == cast(FirstT)first);  // we need casts because invariant is the const attribute by default
        }
        foreach(second; cids.byKey) {
            assert(cast(SecondT)names[cast(FirstT)cids[cast(SecondT)second]] == cast(SecondT)second);  // we need casts because invariant is the const attribute by default
        }
    }

    private:
    FirstT[SecondT] cids;
    SecondT[FirstT] names;
}   // struct TidCross

///
unittest {
    CrossMap!(string, int) cm;
    cm.add("one", 1);
    assert(cm.length == 1);
    assert(cm.first_in("one"));
    assert(cm.second_in(1));

    import std.array: array;
    import std.algorithm.iteration: sum, joiner;
//  cm.add("two", 1);       // this will produce an error, because 1 is in the cross already. We won't want to end up with assimetric maps.
    cm.add("two", 2);
    assert(cm.first(2) == "two");
    assert(cm.second("two") == 2);
    assert(cm.seconds.sum == 3);
    assert(cm.firsts.joiner.array.length == 6);
//  writeln(cm.firsts);     // will produce ["two", "one"]

    // throws RangeError on non-existent key
    import core.exception: RangeError;
    try {
        int i = cm.second("three");
    } catch(RangeError e) {
        assert(e.msg == "Range violation");
    }

    cm.remove("one", 1);
    assert(cm.length == 1);
    cm.remove("three", 1);  // nothing happens
    assert(cm.length == 1);
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
    int i = 1;
    int j = 2;
    int* p = &j;

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
    import tools: w;
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

/**
    Logging facility.
Params:
    text = the text of the message
*/
void logit(string text) {
    import std.stdio: writeln, stdout;

    writeln(text);
    stdout.flush;
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
