module crank.crank_registry;
import std.traits, std.conv;

import project_params;

// modules with dynamic concept names and cranks
import crank.crank_types, crank.crank_main;
import crank.subcrank.subcrank_subpile;

/// Full list of modules, that contain dynamic concept names. This list is used at compile time to gather together all dynamic
/// concept names along with cids and put them in the name map.
enum CrankModules {
    pile = "crank.crank_main",
    subpile = "crank.subcrank.subcrank_subpile"
}

/// Manifest constant array of descriptors (cids, names) of all of the named dynamic concepts of the project. Remember, that most
/// of the dynamic concepts are supposed to be unnamed in the sence, that they are not directly visible to the code.
enum dynDescriptors = createDescriptors_();

//---***---***---***---***---***--- data ---***---***---***---***---***--

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
            // filter out members that do not process (packet names cpt, stat, attn) and pick static functions
            static if      // is the member of module a function?
                    (__traits(isStaticFunction, __traits(getMember, mixin(modul), modMbr)))
            {   //yes: generate a call of it
                fps ~= mixin("&" ~ modMbr);
            }

    // Run the crank functions
    foreach(fp; fps)
        fp();
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
private DynDescriptor[] createDescriptors_() {

    // Declare named static descriptor array
    DynDescriptor[] dds;

    // Fill the named descriptors array
    DynDescriptor dd;
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
private struct DynDescriptor {
    Cid cid;                        /// cid of the concept
    string name;                    /// concept's name
    string class_name;              /// concept's class name - will be used for creating the concept object

    /// Reload opCmp to make it sortable on cid (not nescessary, actually, since cid is the first field in the structure).
    int opCmp(ref const DynDescriptor s) const {
        if(cid < s.cid)
            return -1;
        else if(cid > s.cid)
            return 1;
        else
            return 0;
    }
}
