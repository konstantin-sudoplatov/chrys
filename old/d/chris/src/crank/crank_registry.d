module crank.crank_registry;
import std.traits, std.conv;

import proj_data;

import crank.crank_types;
import chri_data;
import chri_types: DcpDsc;

/// Full list of modules, that contain dynamic concept names in enums of the type DcpDescriptor. This list is used
/// at compile time to gather together all dynamic concept names along with cids and put them in the name map.
enum CrankModules {
    hard = "chri_data",       // the HardCid enum is there
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
            static if(mixin("is(" ~ memberName ~ "==enum)") && mixin("is(" ~ memberName ~ ": DcpDsc)")) {
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
    DynDescriptor lastD;
    foreach(d; dds) {
        assert(d.cid > lastD.cid, "cid "~ to!string(d.cid) ~ "(" ~ d.name ~ ") is already used by " ~ lastD.name);
        lastD = d;
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
