module crank.crank_registry;
import std.traits, std.conv;

import proj_shared, proj_tools;

import crank.crank_types;
import chri_shared;
import chri_types: DcpDescriptor;

/// Full list of modules, that contain dynamic concept names in enums of the type DcpDescriptor. This list is used
/// at compile time to gather together all dynamic concept names along with cids and put them in the name map.
enum CrankModules {
    hard = "chri_shared",       // the HardCid enum is there
    pile = "crank.crank_main",
    subpile = "crank.subcrank.subcrank_subpile"
}

//---***---***---***---***---***--- functions ---***---***---***---***---***--

/**
        Create array of name/cid pairs packed into the TempDynDescriptor struct, CTFE.
    Used to create the manifest constant arrays DynDescriptors_.
    Returns: array of static concept descriptors.
*/
DynDescriptor[] createDynDescriptors() {

    // Declare named static descriptor array
    DynDescriptor[] dds;

    // Fill the named descriptors array
    DynDescriptor dd;
    static foreach(moduleName; [EnumMembers!CrankModules]) {
        mixin("import " ~ moduleName ~ ";");
        static foreach(memberName; __traits(allMembers, mixin(moduleName))) {
            static if(mixin("is(" ~ memberName ~ "==enum)") && mixin("is(" ~ memberName ~ ": DcpDescriptor)")) {
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
        Extract all of the crank functions from crank modules and run them. The functions run in order as modules
    are defined in the CrankModules enum and then in the order of definition functions in the modules.
*/
void runCranks() {

    // Fill in fps with addresses of the crank functions
    void function()[] fps;      // array of the pointers of functions
    static foreach(moduleName; [EnumMembers!CrankModules]) {
        mixin("import " ~ moduleName ~ ";");
        static foreach (modMbr; __traits(allMembers, mixin(moduleName)))
            static if // is the member of module a function?
            (__traits(isStaticFunction, __traits(getMember, mixin(moduleName), modMbr)))
            {   //yes: generate a call of it
                fps ~= mixin("&" ~ modMbr);
            }
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
