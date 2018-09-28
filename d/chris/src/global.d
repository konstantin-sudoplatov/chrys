/// Shared memory, global parameters.
module global;
import std.concurrency;
import std.traits;
import std.format;
import std.conv;

import tools_pile;
import attn_dispatcher_thread, attn_circle_thread;
import cpt_abstract, cpt_pile, cpt_neurons, cpt_premises, cpt_actions;

//---***---***---***---***---***--- types ---***---***---***---***---***---***

/// Concept identifier is 4 bytes long at the moment.
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

// modules with static concepts
import stat_pile;
import stat_substat_subpile;

/// Full list of modules, that contain static concept functions. This list is used at compile time to gather together all static
/// concepts and put them in the holy map and their names and cids in the name map.
enum StatConceptModules {
    pile = "stat_pile",
    subpile = "stat_substat_subpile"
}

/// Call types of the static concept (static concept is function).
enum StatCallType: string {
    p0Cal = "void function(Caldron)",                              // void function(Caldron nameSpace)
    p0Calp1Cid = "void function(Caldron, Cid)",                         // void function(Caldron nameSpace, Cid operandCid)
    p0Calp1Cidp2Cid = "void function(Caldron, Cid, Cid)",                    // void function(Caldron nameSpace, Cid firstoperandCid, Cid secondOperandCid)
    p0Calp1Cidp2Int = "void function(Caldron, Cid, int)",                    // void function(Caldron nameSpace, Cid conceptCid, int intValue)
    p0Calp1Cidp2Float = "void function(Caldron, Cid, float)",                  // void function(Caldron nameSpace, Cid conceptCid, float floatValue)
    p0Calp1Cidp2Cidp3Float = "void function(Caldron, Cid, Cid, float)",             // void function(Caldron nameSpace, Cid branchBreedCid, Cid conceptCid, float floatValue)
}

// modules with dynamic concept names and cranks
import crank_pile;
import crank_subcrank_subpile;

/// Full list of modules, that contain dynamic concept names.This list is used at compile time to gather together all dynamic
///// concept names along with cids and put them in the name map.
enum CrankModules {
    pile = "crank_pile",
    subpile = "crank_subcrank_subpile"
}

//---***---***---***---***---***--- data ---***---***---***---***---***--

//      Key threads of the project. The console thead will be spawned, but we don't need to remember its Tid. The circle
// knows it, it's enough.
const shared Tid _mainTid_;         /// Tid of the main thread
const shared Tid _attnDispTid_;     /// Attention dispatcher thread Tid

// Key shared data structures
shared string[Cid] _nm_;        /// name/seed map
shared HolyMap _hm_;        /// The map of holy(stable and storrable and shared) concepts.
debug {
    // set to true after the maps are filled in with names,cids and references to the concept objects
    immutable bool _maps_filled_;

    // set to true after the cranking is finished and the maps rehashed
    immutable bool _cranked_;
}

/**
    Spawn the key threads (console_thread, attention dispatcher), capture their Tids.
*/
shared static this() {
    // Initialize random generator
    rnd_ = Random(unpredictableSeed);

    // Create and initialize the key shared structures
    _hm_ = new shared HolyMap;
    fillInConceptMaps_(_hm_, _nm_);     // static concepts from the stat modules, dynamic concept names from the crank modules
    debug
        _maps_filled_ = true;

    // Crank the system. System must be cranked befor spawning any circle threads since they use the chat_seed concept to start.
    runCranks_;     // create and setup manually programed dynamic concepts
    import std.stdio: writefln;
    writefln("Some free dynamic cids: %s", _hm_.generate_some_cids(5));

    // Remove from the name map entries not related to the concepts.
    cleanupNotUsedNames;
    _hm_.rehash;
    _nm_.rehash;
    debug
        _cranked_ = true;
}

//---***---***---***---***---***--- functions ---***---***---***---***---***--

/// Enum template for declaring named dynamic concepts. Used in the crank modules.
enum cd(T : SpiritDynamicConcept, Cid cid)  = CptDescriptor(T.stringof, cid);

/**
        Convert name of type represented as a string to real type. Don't forget, the type you a trying to instantiate must
    exist, i.e. to be imported or to be a basic type.
    Parameters:
        typeName = name of type
*/
template type(string typeName) {
    mixin("alias type = " ~ typeName ~ ";");
}

/**
        Get cid by static concept (it' a function, remember!) name.
    Parameters:
        cptName = name of the static concept function
    Returns: its cid (from the annotation) as int enum.
*/
template statCid(alias cptName)
    if      // annotation consists of two elements and their types are int and StatCallType?
            (__traits(getAttributes, cptName).length == 2 &&
            is(typeof(__traits(getAttributes, cptName)[0]) == int) &&
            is(typeof(__traits(getAttributes, cptName)[1]) == StatCallType))
{   // extract the first element of annotation, which is cid
    enum Cid statCid = __traits(getAttributes, cptName)[0];
}

///
unittest {
    @(1, StatCallType.p0Calp1Cid) static void fun(Caldron spaceName, Cid cid) {}

    const Cid cid = __traits(getAttributes, fun)[0];      // its cid
    assert(statCid!fun == cid);    // check cid
}

/**
            Check up availability and type of a concept by its cid.
    Parameters:
        T = type to check against
        cid = cid of a concept, that is checked
*/
void checkCid(T: SpiritConcept)(Cid cid) {
    debug if(_maps_filled_) {
        assert(cid in _hm_, format!"Cid: %s(%s) do not exist in the holy map."(cid, _nm_[cid]));
        assert(cast(T)_hm_[cid],
                format!"Cid: %s, must be of type %s and it is of type %s."(cid, T.stringof, typeid(_hm_[cid])));
    }
}

///         Adapter for live concepts.
void checkCid(T)(Caldron caldron, Cid cid)
    if(is(T: Concept) || is(T == interface))
{
    debug if(_cranked_)
        assert((cast(T)caldron[cid]),
                format!"Cid: %s, must be of type %s and it is of type %s."
                        (cid, T.stringof, typeid(caldron[cid])));
}

/**
        Retrieve a concept from the holy map by its enum constant and cast it from the HolyConcept to its original type,
    which is gotten from the enum constant (the CptDescriptor type). The scast template serves as a guard against silent
    casting an object to null, if the cast happens to be illegal.
    Parameters:
        cd = constant descriptor of the enum, describing the concept
    Returns: the wanted concept casted to its original type.
*/
auto cpt(alias cd)() {
    return scast!(type!("shared " ~ cd.className))(_hm_[cd.cid]);
}

///
unittest {
    import std.stdio: writeln;
    import crank_pile: CommonConcepts, Chat;
    mixin(dequalify_enums!(CommonConcepts, Chat));

    assert(cpt!(chat_seed) is cast(shared SpSeed)_hm_[chat_seed.cid]);
}

/// Structure of the crank enums.
struct CptDescriptor {
    string className;      // named concept's class
    Cid cid;                // named concept's cid
//    alias cid this;         // the enum's value by default is cid
}

///**
//            It is a two-way map of concept name/cid. The concepts are both static and dynamic. Here are gathered all
//    of the concepts which have names, that are known to the compiler, i.e. can be manipulated directly from the code.
//    Since this object is changed only in this module constructor (only this thread) and is immutable throughout all
//    the rest lifetime, it is not synchronized.
//*/
//shared final pure nothrow class NameMap {
//    import tools_pile: CrossMap;
//
//    /**
//                Check the length of the map.
//            Returns: number of pairs in the map.
//    */
//    auto length() {
//        return (cast()cross_).length;
//    }
//
//    /**
//                Overload for "in".
//        Parameters:
//            cid = cid of the concept.
//        Returns: pointer to name or null
//    */
//    const(string*) opBinaryRight(string op)(Cid cid) {
//        return cid in cast()cross_;
//    }
//
//    /**
//                Overload for "in".
//        Parameters:
//            name = name of the concept.
//        Returns: pointer to cid or null
//    */
//    const(Cid*) opBinaryRight(string op)(string name) const {
//        return name in cast()cross_;
//    }
//
//    /**
//            Get the cid of the concept by name.
//        Parameters:
//            name = name of the concept.
//        Returns: cid of the concept, 0 if not found
//    */
//    nothrow const(Cid) cid(string name) const {
//        if (auto p = name in cast()cross_)
//            return *p;
//        else
//            return 0;
//    }
//
//    /// Ditto.
//    const(Cid) opIndex(string name) const {
//        return (cast()cross_)[name];
//    }
//
//    /**
//            Get the name of the concept by cid.
//        Parameters:
//            cid = cid of the concept
//        Returns: name of the concept or null if not found.
//    */
//    const(string) name(Cid  cid) const {
//        if (auto p = cid in cast()cross_)
//            return *p;
//        else
//            return null;
//    }
//
//    /// Ditto.
//    const(string) opIndex(Cid cid) const {
//        return (cast()cross_)[cid];
//    }
//
//    /*
//            Get the range of the cids.
//        Returns: range of cids.
//    */
//    auto cids() {
//        return (cast()cross_).seconds_by_key;
//    }
//
//    /*
//            Get a range of the namess.
//        Returns: range of names.
//    */
//    auto names() {
//        return (cast()cross_).firsts_by_key;
//    }
//
//    /**
//            Add a pair cid/name.
//        Parameters:
//            cid = cid of the concept
//            name = name of the concept
//    */
//    void add(Cid cid, string name) {
//        assert(name !in this && cid !in this, "Keys are already in the map. We won't want to have assimetric maps.");     // if not, we risk having assimetric maps.
//        (cast()cross_).add(cid, name);
//        assert(name in this && cid in this);
//    }
//
//    /**
//            Remove pair cid/name. If there is no such a pair, nothing happens.
//        Parameters:
//            cid = cid of the concept
//            name = name of the concept
//    */
//    void remove(Cid cid, string name) {
//        assert((name !in this && cid !in this) || (name in this && cid in this));
//        (cast()cross_).remove(cid, name);
//        assert(name !in this && cid !in this);
//    }
//
//    /**
//                Rebuild associative arrays to make them more efficient.
//    */
//    void rehash() {
//        (cast()cross_).rehash;
//    }
//
//    private:
//    CrossMap!(Cid, string) cross_;   // we use the cross map implementation, adding interface pass-through methods, to make it readable
//}

///
//unittest {
//    shared NameMap nm = new shared NameMap;
//    nm.add(1, "firstCpt");
//    assert(nm.length == 1);
//    assert("firstCpt" in nm);
//    assert(1 in nm);
//
//    import std.array: array;
//    import std.algorithm.iteration: sum, joiner;
//    //  cm.add(1, "secondCpt");       // this will produce an error, because 1 is in the cross already. We won't want to end up with assimetric maps.
//    nm.add(2, "secondCpt");
//    assert(nm.name(2) == "secondCpt");
//    assert(nm.cid("secondCpt") == 2);
//    assert(nm.cids.sum == 3);
//    assert(nm.names.joiner.array.length == 17);
//    //import std.stdio: writeln;
//    //writeln(nm.names);     // will produce ["firstCpt", "secondCpt"]
//
//        // Makes the program crash with code -4. A bug in DMD, obviously. In the tools_pile.d module analogous code works just fine.
//    // throws RangeError on non-existent key
//    //import core.exception: RangeError;
//    //try {
//    //    int cid = nm.cid("three");
//    //} catch(RangeError e) {
//    //    assert(e.msg == "Range violation");
//    //}
//
//    nm.remove(1, "firstCpt");
//    assert(nm.length == 1);
//    nm.remove(1, "firstCpt");  // nothing happens
//    assert(nm.length == 1);
//}

/**
            Holy concepts map. It is a wrapper for actual associative array.
        Map of all static and dynamic shared storrable (holy) concepts. This map will be used concurrently by all caldrons,
    so it must be synchronized. At the moment, it is usual syncronization on the class object. In the future it can possibly
    be changed to atomic, because the concurrent asccess might be intensive. To that end acsses via the class methods would
    help, because this way we could get away with changes to only interface methods for the real map.
*/
import std.random;
shared synchronized final pure nothrow class HolyMap {

    //---***---***---***---***---***--- types ---***---***---***---***---***---***

    //---***---***---***---***---***--- data ---***---***---***---***---***--

    /**
        Constructor
    */
    this(){}

    //---***---***---***---***---***--- functions ---***---***---***---***---***--

    /**
                Length of the map.
        Returns: the length of the map as in an AA.
    */
    auto length() {
        return holyMap_.length;
    }

    /**
                Assign/construct-assign new holy map entry. If cid had not been assigned to the cpt yet, it is generated.
        Parameters:
            cpt = shared concept to assign
    */
    shared(SpiritConcept) add(shared SpiritConcept cpt)
    in {
        assert(cpt !in this, "Cid " ~ to!string(cpt.cid) ~ " - this cid already exists in the holy map.");
        if      // dynamic?
                (cast(shared SpiritDynamicConcept)cpt)
            if      // with preset cid?
                    (cpt.cid != 0)
                assert(cpt.cid >= MIN_DYNAMIC_CID && cpt.cid <= MAX_DINAMIC_CID,
                        "Cid: " ~ to!string(cpt.cid) ~ ", cids for dynamic concepts must lie in the range of " ~
                        to!string(MIN_DYNAMIC_CID) ~ ".." ~ to!string(MAX_DINAMIC_CID));
            else {} //no: dynamic concepts without cid are allowed, cid will be generated
        else if // static?
                (cast(shared SpStaticConcept)cpt)
        {
            assert(cpt.cid != 0, "Static concepts can't have zero cid. Their cids are initialized at construction.");
            assert(cpt.cid >= MIN_STATIC_CID && cpt.cid <= MAX_STATIC_CID,
                    "Cid: " ~ to!string(cpt.cid) ~ ", cids for static concepts must lie in the range of " ~
                    to!string(MIN_STATIC_CID) ~ ".." ~ to!string(MAX_STATIC_CID));
        }
        else    // neither dynamic and nor static?
            assert(false, to!string(cpt) ~ " - not expected type here.");
    }
    do {
        // generate cid and use it
        if      // is not cid set yet?
                (cpt.cid == 0)
            //no: generate and set it
            cast()cpt.cid = generateDynamicCid_;

        // put the pair in the map
        holyMap_[cpt.cid] = cpt;

        return cpt;
    }

    /**
            Remove key from map. Analogously to the AAs.
        Parameters:
            cid = key
        Returns: true if existed, else false
    */
    bool remove(Cid cid) {
        return holyMap_.remove(cid);
    }

    /**
                Get concept by cid, an overload for the index operation.
        Parameters:
            cid = key
        Returns: shared concept
    */
    shared(SpiritConcept) opIndex(Cid cid) {
        return holyMap_[cid];
    }

    /**
                Add concept to the map, an overload for the index assignment operation.
            Example of usage:
        ---
            _hm_[] = cpt;
        ---
        Parameters:
            cpt = concept to add
        Returns: assigned concept
    */
    shared(SpiritConcept) opIndexAssign(shared SpiritConcept cpt) {
        return add(cpt);
    }

    ///**
    //            Get concept by name.
    //    Parameters:
    //        name = key
    //    Returns: shared concept
    //*/
    //shared(SpiritConcept) opIndex(string name) {
    //    assert(name in _nm_);
    //    return holyMap_[_nm_[name]];
    //}

    /**
                Overload for "in".
        Parameters:
            cid = cid of the concept.
        Returns: pointer to the concept or null
    */
    shared(SpiritConcept*) opBinaryRight(string op)(Cid cid) {
        return cid in holyMap_;
    }

    /// Ditto.
    shared(SpiritConcept*) opBinaryRight(string op)(shared SpiritConcept cpt) {
        return cpt.cid in holyMap_;
    }

    /**
                Pass through for byKey.
        Returns: range of cids
    */
    auto byKey() {
        return (cast()holyMap_).byKey;      // need to cast off the shared attribute to avoid a compiler error
    }

    /**
                Pass through for byValue.
        Returns: range of concepts
    */
    auto byValue() {
        return (cast()holyMap_).byValue;      // need to cast off the shared attribute to avoid a compiler error
    }

    /**
                Rebuild associative array to make it more efficient.
    */
    void rehash() {
        holyMap_.rehash;
    }

    /**
                Generate a namber of dynamic cids.
        Parameters:
            howMany = how many cids you need
        Returns: array of fresh cidsj
    */
    Cid[] generate_some_cids(int howMany) {

        Cid[] sids;
        foreach(i; 0..howMany)
            sids ~= generateDynamicCid_;

        return sids;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%
    private SpiritConcept[Cid] holyMap_;       /// map concept/cid

    //---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

    /**
                Generate new unique throughout the system cid.
        Returns: cid
    */
    private Cid generateDynamicCid_() {
        Cid cid;
        do {
            cid = rnd_.uniform!Cid;
        } while(cid in holyMap_);        // do until not repeated in the map

        return cid;
    }

    //---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
}

//###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
//
//                               Private
//
//###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

//---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

/// Manifest constant array of descriptors (cids, names, pointers, call types) of all the static concepts of the project.
private enum statDescriptors_ = createTempStatDescriptors_;

/// Manifest constant array of gaps in the static cids sequense, used for the static concepts.
private enum unusedStaticCids_ = findUnusedStatCids_;

/// Manifest constant array of descriptors (cids, names) of all of the named dynamic concepts of the project. Remember, that most
/// of the dynamic concepts are supposed to be unnamed in the sence, that they are not directly visible to the code.
private enum dynDescriptors_ = createTempDynDescriptors_();

/// rnd generator. Initialized from constructor.
private static typeof(Random(unpredictableSeed())) rnd_;

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

/**
        Create array of static concept descriptors, CTFE.
    Used to create the manifest constant arrays StatDescriptors_.
    Returns: array of static concept descriptors.
*/
private TempStatDescriptor[] createTempStatDescriptors_() {
import std.stdio;
    // Declare named static descriptor array
    TempStatDescriptor[] sds;

    // Fill the named descriptors array
    TempStatDescriptor sd;
    static foreach(modul; [EnumMembers!StatConceptModules]) {
        static foreach(memberName; __traits(allMembers, mixin(modul))) {
            static if(__traits(isStaticFunction, __traits(getMember, mixin(modul), memberName))) {
                sd.cid = __traits(getAttributes, mixin(memberName))[0];
                sd.name = memberName;
                sd.fun_ptr = mixin("&" ~ memberName);
                static assert(is(typeof(mixin("&" ~ memberName)) == type!(__traits(getAttributes, mixin(memberName))[1])),
                        memberName ~ ": annotated type " ~ __traits(getAttributes, mixin(memberName))[1] ~
                        " doesn't match with real type " ~ typeof(mixin("&" ~ memberName)).stringof);
                sd.call_type = __traits(getAttributes, mixin(memberName))[1];
                sds ~= sd;
            }
        }
    }

    // Sort it
    import std.algorithm.sorting: sort;
    sds.sort;

    // Check if cids in the array are unique.
    Cid lastCid = 0;
    foreach(st; sds) {
        assert(st.cid > lastCid, "cid: "~ to!string(st.cid) ~ ", name: " ~ st.name ~
                " - cids cannot be used multiple times.");
        lastCid = st.cid;
    }

    return sds;
}

/**
        Create array of name/cid pairs packed into the TempDynDescriptor struct, CTFE.
    Used to create the manifest constant arrays DynDescriptors_.
    Returns: array of static concept descriptors.
*/
private TempDynDescriptor[] createTempDynDescriptors_() {

    // Declare named static descriptor array
    TempDynDescriptor[] dds;

    // Fill the named descriptors array
    TempDynDescriptor dd;
    static foreach(moduleName; [EnumMembers!CrankModules]) {
        static foreach(memberName; __traits(allMembers, mixin(moduleName))) {
            static if(mixin("is(" ~ memberName ~ "==enum)")) {
                static foreach(enumElem; __traits(allMembers, mixin(memberName))) {
                    dd.cid = mixin(memberName ~ "." ~ enumElem ~ ".cid");
                    dd.name = enumElem;
                    dd.class_name = mixin(memberName ~ "." ~ enumElem ~ ".className");
                    dds ~= dd;
                }
            }
        }
    }

    // Sort it
    import std.algorithm.sorting: sort;
    dds.sort;

    // Check if cids in the array are unique.
    Cid lastCid = 0;
    foreach(d; dds) {
        assert(d.cid > lastCid, "cid: "~ to!string(d.cid) ~ ", name: " ~ d.name ~
                " - cids cannot be used multiple times.");
        lastCid = d.cid;
    }

    return dds;
}


/**
        Create enum array of unused cids, CTFE.
    Parameters:
        descArray = either statDecsriptors_ or DynDescriptors_
    Returns: array of free cids
*/
private Cid[] findUnusedStatCids_() {
    Cid[] unusedCids;

    // find unused cids
    Cid lastCid = 0;
    foreach(sd; statDescriptors_) {
        assert(sd.cid > lastCid, "cid " ~ to!string(sd.cid) ~ ": cids cannot be used multiple times.");
        if
            (sd.cid > lastCid + 1)
        {
            for(Cid j = lastCid + 1; j < sd.cid; ++j ) unusedCids ~= j;
        }
        lastCid = sd.cid;
    }

    return unusedCids;
}

/**
            Fill in gathered in statDescriptors_ and dynDescriptors_ info into the holy map and name map.
    Parameters:
        hm = holy map to fill
*/
private void fillInConceptMaps_(shared HolyMap hm, shared string[Cid] nm)
out{
    assert(hm.length == statDescriptors_.length + dynDescriptors_.length);
    assert(nm.length == statDescriptors_.length + dynDescriptors_.length);
}
do {
    import std.stdio: writefln;

    // Accept static concepts and their names from the statDescriptors_ enum
    foreach(sd; statDescriptors_) {
        assert(sd.cid !in hm, "Cid: " ~ to!string(sd.cid) ~ ". Cids cannot be reused.");
        hm.add(new shared SpStaticConcept(sd.cid, sd.fun_ptr, sd.call_type));
        nm[sd.cid] = sd.name;
    }

    // report static cids usage
    writefln("Unused static cids: %s", unusedStaticCids_);
    writefln("Last used static cid: %s", statDescriptors_[$-1].cid);

    // Accept dynamic concept names from the dynDescriptors_ enum
    foreach(dd; dynDescriptors_) {
        assert(dd.cid !in nm);
        nm[dd.cid] = dd.name;
    }

    // Create dynamic concepts based on the dynDescriptors_ enum
    static foreach(dd; dynDescriptors_) {
        _hm_[] = mixin("new shared " ~ dd.class_name ~ "(" ~ to!string(dd.cid) ~ ")");
    }
}

/**
            Extract all of the crank functions from crank modules and run them. The functions run in order as modules
    are defined in the CrankModules enum and then in the order of definition functions in the modules.
*/
private void runCranks_() {

    // Fill in fps with addresses of the crank functions
    void function()[] fps;      // array of the pointers of functions
    static foreach(modul; [EnumMembers!CrankModules])
        static foreach(modMbr; __traits(allMembers, mixin(modul)))
            static if      // is the member of module a function?
                    (__traits(isStaticFunction, mixin(modMbr)))
            {   //yes: generate a call of it
                fps ~= mixin("&" ~ modMbr);
            }

    // Run the crank functions
    foreach(fp; fps)
        fp();
}

/**
            Remove from the name map all entries that don't have related entry in the holy map.
*/
private void cleanupNotUsedNames() {
    import std.typecons: Tuple;

    // Find all orphan entries in the name map.
    alias Entry = Tuple!(Cid, "cid", string, "name");
    Entry[] orphans;
    Entry orphan;
    foreach(cid; (cast()_nm_).byKey)
        if      //is not cid in the holy map?
                (cid !in _hm_)
        {
            orphan.cid = cid;
            orphan.name =_nm_[cid];

            orphans ~= orphan;
        }

    // Remove orphans
    foreach(orph; orphans)
        _nm_.remove(orph.cid);
}

//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--

/// Info about static concept descriptor (it's all you need to call that function). This structure is used to
/// gather together name/cid pairs for dynamic concepts. Arrays of this structures will be stored as enums at compile
/// time for following processing them to fill in the holy and name maps.
private struct TempStatDescriptor {
    Cid cid;                        /// cid of the concept
    string name;                    /// concept's name
    void* fun_ptr;                  /// pointer to the function
    StatCallType call_type;         /// call аgreement for the function

    /// Reload opCmp to make it sortable on cid (not nescessary, actually, since cid is the first field in the structure).
    int opCmp(ref const TempStatDescriptor s) const {
        if(cid < s.cid)
            return -1;
        else if(cid > s.cid)
            return 1;
        else
            return 0;
    }
}

///
unittest {
    import attn_circle_thread: Caldron;

    // Stat concept to make a test call
    @(1, StatCallType.p0Calp1Cid) static void fun(Caldron spaceName, Cid cid) {
        assert(spaceName is null && cid ==0);
    }

    // extract the descriptor, cid and name from concept's annotation and declaration
    const TempStatDescriptor sd =
            TempStatDescriptor(__traits(getAttributes, fun)[0], "fun", &fun, __traits(getAttributes, fun)[1]);
    assert(sd.call_type == StatCallType.p0Calp1Cid);

    // use the descriptor form the map to call the concept.
    auto fp = cast(void function(Caldron, Cid))sd.fun_ptr;
    fp(null, 0);
}

/// Info about static concept descriptor (it's all you need to call that function) and also this structure is used to
/// gather together name/cid pairs for dynamic concepts. Arrays of this structures will be stored as enums at compile
/// time for following processing them to fill in the holy and name maps.
private struct TempDynDescriptor {
    Cid cid;                        /// cid of the concept
    string name;                    /// concept's name
    string class_name;              /// concept's class name - will be used for creating the concept object

    /// Reload opCmp to make it sortable on cid (not nescessary, actually, since cid is the first field in the structure).
    int opCmp(ref const TempDynDescriptor s) const {
        if(cid < s.cid)
            return -1;
        else if(cid > s.cid)
            return 1;
        else
            return 0;
    }
}

