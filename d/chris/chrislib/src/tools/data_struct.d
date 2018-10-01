module tools.data_struct;
import std.stdio;
import std.format;

//---***---***---***---***---***--- types ---***---***---***---***---***---***

//---***---***---***---***---***--- data ---***---***---***---***---***--

/**
        Constructor
*/
//this(){}

//---***---***---***---***---***--- functions ---***---***---***---***---***--

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

/// Adapter for the RawDeque to prevent bloating in case it is a container for pointers
struct Deque(E : E*, Sz = uint)
{
    auto deq = DequeImpl!(void*, Sz)();
    alias deq this;

    E* front() { return cast(E*)deq.front; }
    E* popFront() {return cast(E*)deq.popFront; }
    E* back() { return cast(E*)deq.back; }
    E* popBack() {return cast(E*)deq.popBack; }
    E* opIndex(size_t ind) { return cast(E*)deq.opIndex(ind); }
}
///
unittest{
    Deque!(int*) deq;

    int i0 = -1;
    deq.push(&i0);      // it is a deq.deq.push, thanks to the alias this
    int i1 = 1;
    deq.push(&i1);
    assert(*deq[0] == -1 && *deq[1] == 1);
    deq[0] = &i1;
    assert(*deq[0] == 1);
}

/// Adapter for the RawDeque to prevent bloating in case it is a container for objects
struct Deque(E : Object, Sz = uint)
{
    auto deq = DequeImpl!(Object, Sz)();
    alias deq this;

    E front() { return cast(E)deq.front; }
    E popFront() {return cast(E)deq.popFront; }
    E back() { return cast(E)deq.back; }
    E popBack() {return cast(E)deq.popBack; }
    E opIndex(size_t ind) { return cast(E)deq.opIndex(ind); }
}
///
unittest{
    Deque!(ClassA) deq;     // as a test used ClassA from the test of the type template

    deq.push(new ClassA(-1));      // it is a deq.deq.push, thanks to the alias this
    deq.push(new ClassA(1));
    assert(deq[0].x == -1 && deq[1].x == 1);
    deq[0] = new ClassA(42);
    assert(deq.popFront.x == 42);
}

/// All the rest of types are forwarded to the RawDeque template for instantiation as it is
template Deque(E, Sz = uint){
    alias Deque = DequeImpl!(E, Sz);
}
///
unittest{
    Deque!(short) deq;

    deq.push(1); deq.push(2); deq.push(3);
    assert(deq.popFront == 1 && deq.popFront == 2 && deq.popFront == 3);
}

/**
            Two-sided stack and queue like in Java, based, also like in Java, on a cyclic buffer. This implementation, unlike the
        dynamic arrays, for example, allows you to control the extention size in which the buffer grows and shrinks, and also
    you can free unused space with the thim() function. The structure takes 48 bytes of space at initialization compared to
    16 of the dynamic array, but if you need a storage for a big array of data, it is going to be more efficient. BR
        Note: When use the foreach statement remember, that the struct is deep copyed before ising it by foreach. It cannot be
    avoid because consuming the range is a destructive action for the buffer, taken out elements are replaced by nulls
    to let the GC free them. So, it would be resource consuming for big buffers. To scan it use the for statement instead.
        Parameters:
            E = type of elements
            Sz = type of internal pointers. They determine the maximum size of the buffer.
*/
private struct DequeImpl(E, Sz=uint)
    if(is(Sz == ubyte) || is(Sz == ushort) || is (Sz == uint) || is(Sz == ulong))
{
    import core.exception: RangeError;

    /**
            Constructor.
        Parameters:
            extent = both initial number of elements ond number of elements by which the buffer will be extended.
    */
    this(Sz extent){
        assert(extent > 0);
        extent_ = extent;
    }

    /// Postblit constructor. If we don't have the deep copy, the "foreach" statement will mutate our struct, (see
    /// the "help GC" comment) because it uses a copy of the input range to go through (not the save() member function).
    this(this) {
        import core.memory: GC;
        auto newBuf = cast(E*)GC.malloc(E.sizeof * capacity);
        newBuf[0..capacity_] = cBuf_[0..capacity_];
        cBuf_ = newBuf;
    }

    string toString() const {
        if(empty) return "[]";
        string s = format!"[%s"((cast()this)[0]);
        for(int i = 1; i < length_; i++)
            s ~= format!", %s"((cast()this)[i]);
        return s ~ "]";
    }

    /// Show internal representation of the queue
    string toInnerString() const {
        string s = typeid(this).toString;
        s ~= format!"\n    length_ = %s"(length_);
        s ~= format!"\n    capacity_ = %s"(capacity_);
        s ~= format!"\n    head_ = %s"(head_);
        s ~= format!"\n    tail_ = %s"(tail_);
        s ~= format!"\n    cBuf_: %s"(bufToString);
        return s;
    }

    /// Convert the buf to string for debugging and unittest
    string bufToString() const {
        string s;
        if(length_ == 0)
            s ~= "[]";
        else {
            s ~= format!"[%s"(cBuf_[0]);
            for(int i = 1; i < capacity_; i++)
                s ~= format!", %s"(cBuf_[i]);
            s ~= "]";
        }
        return s;
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /// Test for emptiness of the queue. Part of the input range interface.
    bool empty() const {
        return length_ == 0;
    }

    /// Get the first element of the queue without taking it out. Part of the input range interface.
    E front() {
        return cBuf_[head_];
    }

    /// Take out an element from the front of the queue. Part of the input range interface.
    alias pull = popFront;
    E popFront() {
        if(length_ == 0)
            throw new RangeError;

        // pop
        E el = cBuf_[head_];
        cBuf_[head_] = E.init;  // help GC (if elements contain refs to objects on the heap)
        ++head_;
        --length_;
        if(head_ == capacity_) {
            head_ = 0;
            if(length_ == 0)
                tail_ = -1;
        }

        // May be reallocate decreasing
        auto slim = capacity_ - 2*extent_;    // the slimming limit. if reached reallocate
        if(length_ == slim) reallocate_;

        return el;
    }

    /// For forward range interface.
    auto save() {
        import core.memory: GC;
        auto s = this;
        auto newBuf = cast(E*)GC.malloc(E.sizeof * capacity_);
        newBuf[0..capacity_] = cBuf_[0..capacity_];
        cBuf_ = newBuf;
        return s;
    }

    /// Take the last element of the queue. Part of the bidirectional range interface.
    E back() {
        return cBuf_[tail_];
    }

    /// Take out an element from the end of the queue. Part of the bidirectional range interface.
    alias pop = popBack;
    E popBack() {
        if(length_ == 0)
            throw new RangeError;

        // Pop
        E el = cBuf_[tail_];
        cBuf_[tail_] = E.init;  // help GC (if elements contain refs to objects on the heap)
        --tail_;
        --length_;
        if(tail_ == -1 && length_ != 0)
            tail_ = capacity_ - 1;

        // May be reallocate decreasing
        auto slim = capacity_ - 2*extent_;    // the slimming limit. if reached reallocate
        if(length_ == slim) reallocate_;

        return el;
    }

    /**
            Index operator overload. Part of the random access range interface.
        Parameters:
            ind = index of the element in the queue (relative to the head of the queue).
        Throws: the RangeError exception.
    */
    E opIndex(size_t ind) {
        if(ind >= length_) throw new RangeError;
        return cBuf_[actualIndex_(cast(Sz)ind)];
    }

    /**
            Index assignment overloading.
        Parameters:
            value = value to assign
            ind = idex of the element from the head of the queue.
        Throws: the RangeError exception.
    */
    void opIndexAssign(E value, size_t ind) {
        if(ind >= length_) throw new RangeError;
        cBuf_[actualIndex_(cast(Sz)ind)] = value;
    }

    /// Get number of element in the queue.
    Sz length() const {
        return length_;
    }

    /// Get current size of the buffer.
    Sz capacity() { return capacity_; }

    /// Add an element to the end of the queue.
    alias push = pushBack;
    void pushBack(E el) {
        if(length_ == capacity_) reallocate_;
        ++tail_;
        assert(tail_ < Sz.max);
        if(tail_ == capacity_) tail_ = 0;
        cBuf_[tail_] = el;
        assert(length_ < Sz.max);
        ++length_;
    }

    /// Add an element to the head of the queue.
    void pushFront(E el) {
        if(length_ == capacity_) reallocate_;
        --head_;
        if(head_ == -1) head_ = capacity_ - 1;
        assert(length_ < Sz.max);
        cBuf_[head_] = el;
        ++length_;
    }

    /// Nullify the buffer.
    void clear() {
        import core.memory: GC;
        cBuf_ = cast(E*)GC.malloc(E.sizeof * extent_);
        capacity_ = extent_;
        head_ = 0;
        tail_ = -1;
        length_ = 0;
    }

    /// Free all unused space, i.e. make length be equal capacity.
    void trim() {
        reallocate_(length_);
    }

    //===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
    //
    //                                  Private
    //
    //===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
    private:
    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%
        E* cBuf_;           /// Cyclic buffer.
        Sz head_;         /// index of the first element
        Sz tail_ = -1;    /// index of the last element
        Sz length_;       /// number of element in the queue
        Sz capacity_;     /// current capacity of the buffer. It has the same value as cBuf.capacity, but faster.
        immutable Sz extent_ = 3;       /// by this value capacity is increased.

    //---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

    /**
                Calculate real index in the buffer.
        Parameters:
            queInd = index relative to the head of the queue.
        throws: RangeError
    */
    Sz actualIndex_(Sz queInd) const {
        if(queInd > length_)
            throw new RangeError;

        const ulong bufInd = cast(ulong)head_ + cast(ulong)queInd;     // index relative to the beginning of the buffer
        assert(bufInd < Sz.max);
        return cast(Sz)(bufInd >= capacity_? bufInd - capacity_: bufInd);
    }

    /**
                Reallocate the buffer and copy data to it from the old one. All the data will be arranged from the beginning
        of the buffer, the head before the tail.
        Parameters:
            newCapacity = new size of the buffer. If not specified, then it will be the current length plus extent.
    */
    void reallocate_(Sz newCapacity = 0) {
        import core.memory: GC;

        // Allocate. We don't use new T[], because we want the size of the buffer match PRESIZELY the newCapacity, not the
        // power of 2.
        if(newCapacity == 0) {
            assert(length_ + extent_ < Sz.max);
            newCapacity = length_ + extent_;
        }
        E* newBuf = cast(E*) GC.malloc(E.sizeof * newCapacity);

        // Copy
        if(tail_ >= head_) {
            newBuf[0..length_] = cBuf_[head_..tail_+1];
        }
        else {// the tail before head. First move the head part, then the tail part
            newBuf[0..capacity_-head_] = cBuf_[head_..capacity_];
            newBuf[capacity_-head_..capacity_-head_+tail_+1] = cBuf_[0..tail_+1];
        }

        cBuf_ = newBuf;
        head_ = 0;
        tail_ = length_ - 1;
        capacity_ = newCapacity;
    }
}

unittest {
    auto deq = DequeImpl!int();

    foreach(i; 0..5) deq.pushBack(i);
    foreach_reverse(i; -2..0) deq.pushFront(i);
    assert(deq.toString == "[-2, -1, 0, 1, 2, 3, 4]");

    const deq1 = deq.save;
    deq.clear;
    assert(deq1.toString == "[-2, -1, 0, 1, 2, 3, 4]");

    foreach(i; 0..7) deq.pushBack(i);
    assert(deq.toString == "[0, 1, 2, 3, 4, 5, 6]");

    foreach(i; 1..4) deq.popFront;
    deq.pushBack(7);
    assert(deq.toString == "[3, 4, 5, 6, 7]");

    foreach(i; 1..4) deq.popBack;
    foreach_reverse (i; -2..3) deq.pushFront(i);
    assert(deq.toString == "[-2, -1, 0, 1, 2, 3, 4]");

    deq.pushFront(-3);
    deq.pushFront(-4);
    assert(deq.toString == "[-4, -3, -2, -1, 0, 1, 2, 3, 4]");

    foreach(i; 5..12)
        deq.pushBack(i);
    assert(deq.toString == "[-4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11]");

    deq.pushFront(-5);
    deq.pushFront(-6);
    assert(deq.toString == "[-6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11]");

    foreach(i; 0..12)
        deq.popBack;
    assert(deq.toString == "[-6, -5, -4, -3, -2, -1]");

    foreach(i; -6..-2)
        deq.popFront;
    assert(deq.toString == "[-2, -1]");

    deq.trim;
    assert(deq.toString == "[-2, -1]");
    assert(deq.capacity == 2);
    deq[1] = 5;
    assert(deq[1] == 5);
//import std.stdio; writefln("deq = %s", deq);
//logit(deq.toInnerString, TermColor.purple);
}

//===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
//
//                                  Private
//
//===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@

//---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

/// test class for unittests
private class ClassA {
    int x;
    this(int i) {
        x = i;
    }
}

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
