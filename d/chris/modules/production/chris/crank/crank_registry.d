module crank_registry;
import std.traits;
import std.conv;

import global_types;

import global_data;
import cpt_stat;
import stat_main;
import attn_circle_thread;
import cpt_neurons, cpt_actions, cpt_premises;

//---***---***---***---***---***--- types ---***---***---***---***---***---***

// modules with dynamic concept names and cranks
import crank_main;
import crank_subcrank_subpile;

/// Full list of modules, that contain dynamic concept names. This list is used at compile time to gather together all dynamic
/// concept names along with cids and put them in the name map.
enum CrankModules {
    pile = "crank_main",
    subpile = "crank_subcrank_subpile"
}

//---***---***---***---***---***--- data ---***---***---***---***---***--

/// Manifest constant array of descriptors (cids, names) of all of the named dynamic concepts of the project. Remember, that most
/// of the dynamic concepts are supposed to be unnamed in the sence, that they are not directly visible to the code.
enum dynDescriptors = createTempDynDescriptors_();

/**
        Constructor
*/
//this(){}

//---***---***---***---***---***--- functions ---***---***---***---***---***--

/**
            Extract all of the crank functions from crank modules and run them. The functions run in order as modules
    are defined in the CrankModules enum and then in the order of definition functions in the modules.
*/
void runCranks() {

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
                Roll a list of Cid enums into a single anonymous enum of type Cid. CTFE.
        The result of this function is supposed to be mixined into a function that use those enums.
    Used simplification of writing the crank functions. Nested "with" statement can be as well use instead of that
    mixin, but this implementation seems to looke cleaner. Do not forget to use imports for the source enums.
    Parameters:
        enumList = list of enums of type Cid
    Returns: string, containing the resulting enum statement, ready to be mixed in the code.
*/
string dequalify_enums(enumList...)() {
    import global_data: CptDescriptor;
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
    import global_data: CptDescriptor, cd;
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

//===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@
//
//                                  Private
//
//===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@===@@@

//---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

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

//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--

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
