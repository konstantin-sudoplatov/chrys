module tools;
import std.stdio;
import std.format;

/// Private exception of the project.
class Crash: Exception {
    @nogc @safe pure nothrow
            this(string msg, string file = __FILE__, size_t line = __LINE__, Throwable nextInChain = null)
    {
        super(msg, file, line, nextInChain);
    }

    @nogc @safe pure nothrow this(string msg, Throwable nextInChain, string file = __FILE__, size_t line = __LINE__)
    {
        super(msg, file, line, nextInChain);
    }
}

/**
        Convert name of type represented as a string to real type. Don't forget, the type you a trying to instantiate must
    exist, i.e. to be imported or to be a basic type.
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
/// test class for previous unittest
class ClassA {
    int x;
    this(int i) {
        x = i;
    }
}

/**
        Safe cast. It will throw assert if the object cannot be casted.
    Parameters:
        T = type to cast to
        o = object to cast
    Return: casted object or an assert will happen
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
        return firsts.length;
    }

    /**
            Check if the first key present in the cross. Analogous to the D "in" statement.
    */
    const(SecondT*) opBinaryRight(string op)(FirstT first) const {
        return first in seconds;
    }

    /**
            Check if the second key present in the cross. Analogous to the D "in" statement.
    */
    const(FirstT*) opBinaryRight(string op)(SecondT second) const {
        return second in firsts;
    }

    /**
            Get the second key by the first one.
        Parameters:
            second = the second key.
        Returns: the first key.
        Throws: RangeError exception if the key not found.
    */
    const(FirstT) opIndex(SecondT second) const {
        return firsts[second];
    }

    /**
            Get the first key by the second one.
        Parameters:
            first = the first key
        Returns: the second key.
        Throws: RangeError exception if the key not found.
    */
    const(SecondT) opIndex(FirstT first) const {
        return seconds[first];
    }

    /*
            Get the range of the first keys.
        Returns: range of the firsts.
    */
    auto seconds_by_key() {
        return seconds.byKey;
    }

    /*
            Get the range of the second keys.
        Returns: range of the seconds.
    */
    auto firsts_by_key() {
        return firsts.byKey;
    }

    /**
            Add a pair &ltfirst key&gt/&ltsecond key&gt.
        Parameters:
            first = the first key
            second = the second key
    */
    void add(FirstT first, SecondT second) {
        assert(second !in firsts && first !in seconds,
                "Keys are already in the map. We won't want to have assimetric maps.");     // if not, we risk having assimetric maps.
        firsts[second] = first;
        seconds[first] = second;
        assert(second in firsts && first in seconds);
    }

    /**
            Remove pair &ltfirst key&gt/&ltsecond key&gt. If there is no such pair, nothing happens.
        Parameters:
            first = the first key
            second = the second key
    */
    void remove(FirstT first, SecondT second) {
        firsts.remove(second);
        seconds.remove(first);
        assert(second !in firsts && first !in seconds);
    }

    /**
                Rebuild associative arrays to make them more efficient.
    */
    void rehash() {
        firsts.rehash;
        seconds.rehash;
    }

    invariant {
        assert(firsts.length == seconds.length);
        foreach(first; seconds.byKey) {
            assert(cast(FirstT)firsts[cast(SecondT)seconds[cast(FirstT)first]] == cast(FirstT)first);  // we need casts because invariant is the const attribute by default
        }
        foreach(second; firsts.byKey) {
            assert(cast(SecondT)seconds[cast(FirstT)firsts[cast(SecondT)second]] == cast(SecondT)second);  // we need casts because invariant is the const attribute by default
        }
    }

    private:
    FirstT[SecondT] firsts;
    SecondT[FirstT] seconds;
}   // struct TidCross

///
unittest {
    CrossMap!(string, int) cm;
    cm.add("one", 1);
    assert(cm.length == 1);
    assert("one" in cm);
    assert(1 in cm);

    import std.array: array;
    import std.algorithm.iteration: sum, joiner;
//  cm.add("two", 1);       // this will produce an error, because 1 is in the cross already. We won't want to end up with assimetric maps.
    cm.add("two", 2);
    assert(cm[2] == "two");
    assert(cm["two"] == 2);
    assert(cm.firsts_by_key.sum == 3);
    assert(cm.seconds_by_key.joiner.array.length == 6);
//  writeln(cm.firsts);     // will produce ["two", "one"]

    // throws RangeError on non-existent key
    import core.exception: RangeError;
    try {
        cast(void)cm["three"];
    } catch(RangeError e) {
        assert(e.msg == "Range violation");
    }

    cm.remove("one", 1);
    assert(cm.length == 1);
    cm.remove("three", 1);  // nothing happens
    assert(cm.length == 1);

    cm.rehash;
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

/// Two-sided stack and queue like in Java, based, also like in Java, on a cyclic buffer.
struct Deque(T) {
    import core.exception: RangeError;

    /// Constructor
    this(long reserve){
        cBuf_.reserve(reserve);
        capacity_ = cBuf_.capacity;
        cBuf_.length = capacity_;
    }

    /// Postblit constructor. If we don't have the deep copy, the "foreach" statement will mutate our struct, (see
    /// the "help GC" comment) because it uses a copy of the input range to go through (not the save() member function).
    this(this) {
        cBuf_ = cBuf_.dup;
    }

    string toString() const {
        if(empty) return "[]";
        string s = format!"[%s"(this[0]);
        for(int i = 1; i < length_; i++)
            s ~= format!", %s"(this[i]);
        return s ~ "]";
    }

    /// Show internal representation of the queue
    string toInnerString(){
        string s = typeid(this).toString;
        s ~= format!"\n    length_ = %s"(length_);
        s ~= format!"\n    capacity_ = %s"(capacity_);
        s ~= format!"\n    head_ = %s"(head_);
        s ~= format!"\n    tail_ = %s"(tail_);
        s ~= format!"\n    cBuf.length = %s, cBuf.capacity = %s"(cBuf_.length, cBuf_.capacity);
        s ~= format!"\n    cBuf: %s"(cBuf_);
        return s;
    }

    /// Test for emptiness of the queue. Part of the input range interface.
    bool empty() const {
        return length_ == 0;
    }

    /// Take the first element of the queue. Part of the input range interface.
    T front() const {
        return cBuf_[head_];
    }

    /// Take out an element from the front of the queue. Part of the input range interface.
    void popFront() {
        if(length_ == 0)
            throw new RangeError;
        else
            if(length_ < (capacity_>>2))
                reallocateDecreasing_;

        cBuf_[head_] = T.init;  // help GC (if elements contain refs to objects on the heap)
        ++head_;
        --length_;
        if(head_ == capacity_) {
            head_ = 0;
            if(length_ == 0)
                tail_ = -1;
        }
    }

    /// For forward range interface.
    auto save() {
        auto s = this;
        s.cBuf_ = cBuf_.dup;
        return s;
    }


    /// Take the last element of the queue. Part of the bidirectional range interface.
    T back() const {
        return cBuf_[tail_];
    }

    /// Take out an element from the end of the queue. Part of the bidirectional range interface.
    void popBack() {
        if(length_ == 0)
            throw new RangeError;
        else
            if(length_ < (capacity_>>2))
                reallocateDecreasing_;

        cBuf_[tail_] = T.init;  // help GC (if elements contain refs to objects on the heap)
        --tail_;
        --length_;
        if(tail_ == -1 && length_ != 0)
            tail_ = capacity_ - 1;
    }

    /**
            Index operator overload. Part of the random access range interface.
        Parameters:
            ind = index of the element in the queue (relative to the head of the queue).
    */
    T opIndex(size_t i) const {
        return cBuf_[actualIndex_(cast(long)i)];
    }

    /// Get number of element in the queue.
    size_t length() const {
        return length_;
    }

    /// Get current size of the buffer.
    size_t capacity() { return capacity_; }

    /// Add an element to the end of the queue.
    alias add = addBack;
    void addBack(T el) {
        if(length_ == capacity_) reallocateIncreasing_;
        ++tail_;
        if(tail_ == capacity_) tail_ = 0;
        cBuf_[tail_] = el;
        ++length_;
    }

    /// Add an element to the head of the queue.
    void addFront(T el) {
        if(length_ == capacity_) reallocateIncreasing_;
        --head_;
        if(head_ == -1) head_ = capacity_ - 1;
        cBuf_[head_] = el;
        ++length_;
    }

    //===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
    //
    //                                  Private
    //
    //===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
    private:
    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%
        T[] cBuf_;        /// Cyclic buffer.
        long head_;       /// index of the first element
        long tail_ = -1;  /// index of the last element
        long length_;     /// number of element in the queue
        long capacity_;   /// current capacity of the buffer. It has the same value as cBuf.capacity, but faster.

    /**
                Calculate real index in the buffer.
        Parameters:
            queInd = index relative to the head of the queue.
        throws: RangeError
    */
    long actualIndex_(long queInd) const {
        if(queInd < 0 || queInd > length_)
            throw new RangeError;

        const long bufInd = head_ + queInd;     // index relative to the beginning of the buffer
        return bufInd >= capacity_? bufInd - capacity_: bufInd;
    }

    /// Add space to the buffer
    //void reallocateIncreasing_() {
    //    cBuf_.reserve(capacity_ + 1);
    //    capacity_ = cBuf_.capacity;
    //    cBuf_.length = capacity_;       // initialize free array space
    //
    //    // May be move the content to the end
    //    if      // is tail before head?
    //            (tail_ < head_ && length_ != 0)
    //    {   // move data to the end, probably with overlapping
    //        import std.algorithm.mutation: copy;
    //        const long howMany = length_ - tail_ - 1;
    //        const long toWhere = capacity_ - howMany;
    //        copy(cBuf_[head_..head_+howMany], cBuf_[toWhere..capacity_]);
    //        head_ = toWhere;
    //    }
    //}

    /// Add space to the buffer
    void reallocateIncreasing_() {
        // reallocate
        T[] newBuf;
        newBuf.reserve(capacity_ + 1);
        const long newCapacity = newBuf.capacity;
        newBuf.length = newCapacity;

        // Copy
        if      // is tail before head?
                (tail_ < head_ && length_ != 0)
        {   // move data to the end, probably with overlapping
            newBuf[0..capacity_-head_] = cBuf_[head_..capacity_];
            newBuf[capacity_-head_..capacity_-head_+tail_+1] = cBuf_[0..tail_+1];
        }
        else {
            newBuf[0..length_] = cBuf_[head_..tail_+1];
        }

        cBuf_ = newBuf;
        head_ = 0;
        tail_ = length_ - 1;
        capacity_ = newCapacity;
    }

    /// Free space from the buffer
    void reallocateDecreasing_() {
        const long newReserve = capacity_ >> 1;
        assert(length_ <= newReserve, format!"%s elements cannot fit into array[%s]."(length_, newReserve));

        // reallocate and copy
        T[] newBuf;
        newBuf.reserve(newReserve);
        long newCapacity = newBuf.capacity;
        newBuf.length = newCapacity;    // initialize
        if
                (head_ <= tail_)
        {   // place all elements from the beggining
            newBuf[0..length_] = cBuf_[head_..head_+length_];
            cBuf_ = newBuf;
            head_ = 0;
            tail_ = length_ - 1;
        }
        else {
            // copy first tail_ elements
            newBuf[0..tail_+1] = cBuf_[0..tail_+1];

            // place elements from head_ to the capacity_ of the old array from the end of the new one
            const long howMany = capacity_ - head_;
            const long newHead = newCapacity - howMany;
            newBuf[newHead..newCapacity] = cBuf_[head_..capacity_];
            cBuf_ = newBuf;
            head_ = newHead;
        }
        capacity_ = newCapacity;
    }

    invariant{
        assert(cBuf_.capacity == capacity_);
    }
}

unittest {
    Deque!int deq = Deque!int(1);
    foreach(i; 0..7) deq.addBack(i);
    assert(deq.toString == "[0, 1, 2, 3, 4, 5, 6]");
    assert(format!"%s"(deq.cBuf_) == "[0, 1, 2, 3, 4, 5, 6]");
    assert(deq[6] == 6);
    assert(deq.capacity == 7);

    foreach(i; 1..4) deq.popFront;
    deq.addBack(7);
    assert(deq.toString == "[3, 4, 5, 6, 7]");
    assert(format!"%s"(deq.cBuf_) == "[7, 0, 0, 3, 4, 5, 6]");
    assert(deq[4] == 7);

    foreach(i; 1..4) deq.popBack;
    foreach_reverse (i; -2..3) deq.addFront(i);
    assert(deq.toString == "[-2, -1, 0, 1, 2, 3, 4]");
    assert(format!"%s"(deq.cBuf_) == "[0, 1, 2, 3, 4, -2, -1]");
    assert(deq[0] == -2);

    deq.addFront(-3);

    import std.stdio;
    writefln(format!"deq = %s"(deq));
    logit(deq.toInnerString, TermColor.purple);
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
