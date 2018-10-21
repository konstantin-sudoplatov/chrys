module proj_types;
import std.stdio;
import std.format;

import proj_data;

/// Adapter for the DequeImpl to prevent bloating in case it is a container for pointers
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

/// Adapter for the DequeImpl to prevent bloating in case it is a container for objects
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

/// All the rest of types are forwarded to the DequeImpl template to instantiate as is
template Deque(E, Sz = uint){
    alias Deque = DequeImpl!(E, Sz);
}
///
unittest{
    Deque!(short) deq;

    deq.push(1); deq.push(2); deq.push(3);
    assert(deq.popFront == 1 && deq.popFront == 2 && deq.popFront == 3);
}

/// Adapter for the ArrayListImpl to prevent bloating in case it is a container for pointers
struct ArrayList(E : E*, Sz = uint)
{
    auto arl = ArrayListImpl!(void*, Sz)();
    alias arl this;

    E* front() { return cast(E*)arl.front; }
    E* popFront() {return cast(E*)arl.popFront; }
    E* opIndex(size_t ind) { return cast(E*)arl.opIndex(ind); }
}
///
unittest{
    ArrayList!(int*) arl;

    int i0 = -1;
    arl.push(&i0);      // it is a deq.deq.push, thanks to the alias this
    int i1 = 1;
    arl.push(&i1);
    assert(*arl[0] == -1 && *arl[1] == 1);
    arl[0] = &i1;
    assert(*arl[0] == 1);
}

/// Adapter for the ArrayList to prevent bloating in case it is a container for objects
struct ArrayList(E : Object, Sz = uint)
{
    auto arl = ArrayListImpl!(Object, Sz)();
    alias arl this;

    E front() { return cast(E)arl.front; }
    E popFront() {return cast(E)arl.popFront; }
    E opIndex(size_t ind) { return cast(E)arl.opIndex(ind); }
}
///
unittest{
    ArrayList!(ClassA) arl;     // as a test used ClassA from the test of the type template

    arl.push(new ClassA(-1));      // it is a deq.deq.push, thanks to the alias this
    arl.push(new ClassA(1));
    assert(arl[0].x == -1 && arl[1].x == 1);
    arl[1] = new ClassA(42);
    assert(arl.popFront.x == 42);
}

/// All the rest of types are forwarded to the ArrayListImpl template to instantiate as is
template ArrayList(E, Sz = uint){
    alias ArrayList = ArrayListImpl!(E, Sz);
}
///
unittest{
    ArrayList!(short) arl;

    arl.push(1); arl.push(2); arl.push(3);
    assert(arl.popFront == 3 && arl.popFront == 2 && arl.popFront == 1);
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

    /**
            Operator "firsts[first] = second" overload.
        Parameters:
            second = value
            first = index
    */
    void opIndexAssign(SecondT second, FirstT first) {
        firsts[second] = first;
        seconds[first] = second;
    }

    /**
            Operator "seconds[second] = first" overload.
        Parameters:
            first = value
            second = index
    */
    void opIndexAssign(FirstT first, SecondT second) {
        seconds[first] = second;
        firsts[second] = first;
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

    /*
            Get the range of the second keys. If it is for example a Crossmap!(string, int) then instead of "firstsByKey()"
                    you can use "stringsByKey()" (see the alias down there).
        Returns: range of the seconds.
    */
    mixin("alias "~ FirstT.stringof ~ "sByKey" ~ " = firstsByKey;");
    auto firstsByKey() {
        return firsts.byKey;
    }

    /*
            Get the range of the first keys. If it is for example a Crossmap!(string, int) then instead of "secondsByKey()"
        you can use "intsByKey()" (see the alias down there).
        Returns: range of the firsts.
    */
    mixin("alias "~ SecondT.stringof ~ "sByKey" ~ " = secondsByKey;");
    auto secondsByKey() {
        return seconds.byKey;
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

    invariant {
        assert(firsts.length == seconds.length);
    }
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
    cm["two"] = 2;
    assert(cm[2] == "two");
    assert(cm["two"] == 2);
    assert(cm.firstsByKey.sum == 3);
    assert(cm.stringsByKey.sum == 3);
    assert(cm.secondsByKey.joiner.array.length == 6);
    assert(cm.intsByKey.joiner.array.length == 6);
//  writeln(cm.secondsByKey);     // will produce ["two", "one"]
//  writeln(cm.intsByKey);        // will produce ["two", "one"]

    cm[3] = "three";
    assert(cm[3] == "three");
    assert(cm["three"] == 3);

    // throws RangeError on non-existent key
    import core.exception: RangeError;
    try {
        cast(void)cm["fore"];
    } catch(RangeError e) {
        assert(e.msg == "Range violation");
    }

    cm.remove("one", 1);
    assert(cm.length == 2);
    cm.remove("fore", 1);  // nothing happens
    assert(cm.length == 2);

    cm.rehash;
}

//===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
//
//                                  Private
//
//===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@

//---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--

/**
            Two-sided stack and queue like in Java, based, also like in Java, on a cyclic buffer. This implementation,
    unlike the dynamic arrays gives you more granular control on the buffer size. You can free unused space with
    the thim() function and you can reserve exact size of space. Also, extents are not 100% of the current size, but only 50%.
    And the buffer shrinks when elements are removed.  The structure takes 40 (24 for uint) bytes at initialization compared to
    16 for the dynamic arrays, but if you need a storage for a big array of data, it is going to be more efficient. BR
        Note: When use the foreach statement remember, that the struct is duplicated before ising it by foreach. It cannot be
    avoided because consuming the range is a destructive action for the buffer, because taken out elements are replaced by nulls
    to let the GC free them. So, it would be resource consuming for big buffers. The for statement will be more efficient.
        Parameters:
            E = type of elements
            Sz = type of internal pointers. They determine the maximum size of the buffer.
*/
private struct DequeImpl(E, Sz=uint)
    if(is(Sz == ubyte) || is(Sz == ushort) || is (Sz == uint) || is(Sz == ulong))
{
    import core.exception: RangeError;

    /// Postblit constructor. If we don't duplicate the list, the "foreach" statement will mutate our struct, (see
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
        const slim = (capacity_>>>1) - 5;    // the slimming limit. if reached reallocate
        if(length_ == slim) reallocate_;

        return el;
    }

    /// For forward range interface.
    auto save() {
        return this;
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
        const slim = (capacity_>>>1) - 5;    // the slimming limit. if reached reallocate
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
    Sz capacity() {
        return capacity_;
    }

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

    /// Allocate exactly given number of elements.
    void reserve(uint capacity) {
        assert(capacity >= length_);
        reallocate_(capacity);
    }

    /// Nullify the buffer.
    void clear() {
        cBuf_ = null;
        capacity_ = 0;
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

    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%
    private E* cBuf_;         /// Cyclic buffer.
    private Sz head_;         /// index of the first element
    private Sz tail_ = -1;    /// index of the last element
    private Sz length_;       /// number of element in the queue
    private Sz capacity_;     /// current capacity of the buffer

    //---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

    /**
                Calculate real index in the buffer.
        Parameters:
            queInd = index relative to the head of the queue.
        throws: RangeError
    */
    private Sz actualIndex_(Sz queInd) const {
        if(queInd > length_)
            throw new RangeError;

        const ulong bufInd = cast(ulong)head_ + cast(ulong)queInd;     // index relative to the beginning of the buffer
        assert(bufInd < Sz.max);
        return cast(Sz)(bufInd >= capacity_? bufInd - capacity_: bufInd);
    }

    /**
                Reallocate the buffer and copy data to it. All data will be arranged from the beginning
        of the buffer, the head before the tail.
        Parameters:
            newCapacity = new size of the buffer. If not specified, then it will be the current length plus a half.
    */
    private void reallocate_(ulong newCapacity = 0) {
        import core.memory: GC;

        // Allocate. We don't use new T[], because we want the size of the buffer match PRESIZELY the newCapacity, not just the
        // power of 2.
        if(newCapacity == 0) {
            if(length_ < 15)
                newCapacity = length_ + 5;
            else
                newCapacity = cast(ulong)length_ + (length_>>>1);
            assert(newCapacity < Sz.max);
        }
        assert(newCapacity >= length_ && newCapacity > 0);
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
        capacity_ = cast(Sz)newCapacity;
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

/**
            List based on a buffer. This implementation, unlike the dynamic arrays gives you more granular control on the
    buffer size. You can free unused space with the thim() function and you can reserve exact size of space. Also, extents
    are not 100% of the current size, but only 50%. And the buffer shrinks when elements are removed. It can also be used
    as a stack and imlpements a range. BR
        Note: When use the foreach statement remember, that the struct is duplicated before ising it by foreach. It cannot be
    avoided because consuming the range is a destructive action for the buffer, because taken out elements are replaced by nulls
    to let the GC free them. So, it would be resource consuming for big buffers. The for statement will be more efficient.
        Parameters:
            E = type of elements
            Sz = type of internal pointers. They determine the maximum size of the buffer.
*/
private struct ArrayListImpl(E, Sz=Cind)
    if(is(Sz == ubyte) || is(Sz == ushort) || is (Sz == uint) || is(Sz == ulong))
{
    import core.exception: RangeError;

    E* buf;             /// buffer.
    alias buf this;     // allow all not overloaded operations on the buffer

    /// Postblit constructor. If we don't duplicate the list, the "foreach" statement will mutate our struct, (see
    /// the "help GC" comment) because it uses a copy of the input range to go through (not the save() member function).
    this(this) {
        import core.memory: GC;
        auto newBuf = cast(E*)GC.malloc(E.sizeof * length_);
        newBuf[0..length_] = buf[0..length_];
        buf = newBuf;
        capacity_ = length_;
    }

    string toString() const {
        if(empty) return "[]";
        string s = format!"[%s"((cast()this)[0]);
        for(int i = 1; i < length_; i++)
            s ~= format!", %s"((cast()this)[i]);
        return s ~ "]";
    }

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /// Test for emptiness of the list. Part of the input range interface.
    bool empty() const {
        return length_ == 0;
    }

    /// Get the last element of the list without taking it out. Part of the input range interface.
    E front() {
        return buf[length_-1];
    }

    /// Take out an element from the end of the list. Part of the input range interface.
    alias pop = popFront;
    E popFront() {
        if(length_ == 0)
            throw new RangeError;

        // pop
        --length_;
        E el = buf[length_];
        buf[length_] = E.init;  // help GC (if elements contain refs to objects on the heap)

        // May be reallocate decreasing
        const slim = (capacity_>>>1) - 5;    // the slimming limit. if reached reallocate
        if(length_ == slim) reallocate_;

        return el;
    }

    /// For forward range interface.
    auto save() {
        return this;
    }

    /**
            Index operator overload. Part of the random access range interface.
        Parameters:
            ind = index of the element in the queue (relative to the head of the queue).
        Throws: the RangeError exception.
    */
    E opIndex(size_t ind) {
        if(ind >= length_) throw new RangeError;
        return buf[ind];
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
        buf[ind] = value;
    }

    /**
            "~=" operator overloading.
        Parameters:
            el = element to add to the list
    */
    void opOpAssign(string op)(E el) {
        static assert(op == "~");
        push(el);
    }

    /// Ditto.
    void opOpAssign(string op)(E[] el) {
        static assert(op == "~");
        foreach(e; el) push(e);
    }

    /// Get number of element in the queue.
    Sz length() const {
        return length_;
    }

    /// Get current size of the buffer.
    Sz capacity() {
        return capacity_;
    }

    /// Add an element to the end of the list
    void push(E el) {
        if(length_ == capacity_) reallocate_;
        assert(length_ < Sz.max);
        buf[length_] = el;
        ++length_;
    }

    /// Allocate exactly given number of elements.
    void reserve(uint capacity) {
        assert(capacity >= length_);
        reallocate_(capacity);
    }

    /// Nullify the buffer.
    void clear() {
        buf = null;
        capacity_ = 0;
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

    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%
    private Sz length_;         /// number of element in the list
    private Sz capacity_;       /// current capacity of the buffer

    //---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

    /**
                Reallocate the buffer and copy data to it.
        Parameters:
            newCapacity = new size of the buffer. If not specified, then it will be the current length plus a half.
    */
    private void reallocate_(ulong newCapacity = 0) {
        import core.memory: GC;

        // Allocate. We don't use new T[], because we want the size of the buffer match PRESIZELY the newCapacity, not just the
        // power of 2.
        if(newCapacity == 0) {
            if(length_ < 15)
                newCapacity = length_ + 5;
            else
                newCapacity = cast(ulong)length_ + (length_>>>1);
            assert(newCapacity < Sz.max);
        }
        assert(newCapacity >= length_ && newCapacity > 0);
        E* newBuf = cast(E*)GC.malloc(E.sizeof * newCapacity);

        // Copy
        newBuf[0..length_] = buf[0..length_];
        buf = newBuf;
        capacity_ = cast(Sz)newCapacity;
    }
}

unittest{
    ArrayListImpl!int arl;

    foreach(i; 0..30) arl.push(i);
    assert(arl.length == 30 && arl[29] == 29);

    foreach_reverse(i; 9..29) arl.pop;
    assert(arl.length == 10 && arl[9] == 9);

    arl[9] = 8;
    assert(arl[9] == 8);
    assert(arl.toString == "[0, 1, 2, 3, 4, 5, 6, 7, 8, 8]");

    foreach(i; arl) {}
    assert(arl.length == 10 && arl.toString == "[0, 1, 2, 3, 4, 5, 6, 7, 8, 8]");

    foreach(i; arl.save) {}
    assert(arl.length == 10 && arl.toString == "[0, 1, 2, 3, 4, 5, 6, 7, 8, 8]");

    arl.clear;
    arl ~= 0;
    arl ~= 1;
    assert(arl.toString == "[0, 1]");

    ArrayListImpl!int arl1;
    arl1 ~= [2, 3];
    assert(arl1.toString == "[2, 3]");

    // test a not overloaded operation
    arl1[0..2] = arl[0..2];
    assert(arl1.toString == "[0, 1]");

//writeln(lar1);
//logit(deq.toInnerString, TermColor.purple);
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

