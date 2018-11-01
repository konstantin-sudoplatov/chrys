module crank.crank_types;
import std.format;

import proj_data, proj_funcs;

import chri_types, chri_data;
import atn.atn_caldron;
import cpt.abs.abs_concept, cpt.cpt_actions, cpt.cpt_premises, cpt.cpt_neurons;
import stat.stat_types: statCid;

//---***---***---***---***---***--- data ---***---***---***---***---***--

//---***---***---***---***---***--- functions ---***---***---***---***---***--

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

/**
        Retrieve a concept from the spirit map by its enum and cast it from the SpiritConcept to its original type,
    which is gotten from the enum constant (the CptDescriptor type). The scast template serves as a guard against silent
    casting an object to null, if the cast happens to be illegal.
    Parameters:
        cd = constant descriptor of the enum, describing the concept
    Returns: the wanted concept casted to its original type.
*/
auto cp(alias cd)() {
    return scast!(type!(cd.className))(_sm_[cd.cid]);
}

///
unittest {
    @(1, StatCallType.p0Calp1Cid) static void fun(Caldron spaceName, Cid cid) {}

    const Cid cid = __traits(getAttributes, fun)[0];      // its cid
    assert(statCid!fun == cid);    // check cid
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
    string res = "enum : DcpDescriptor {\n";
    static foreach (enuM; enumList)     // each enum en in the list of enums
    {
        static assert(is(enuM == enum) && is(enuM: DcpDescriptor));
        static foreach(enEl; __traits(allMembers, enuM)) {         // each enum element
            res ~= format!"    %s = %s.%s,\n"(enEl, enuM.stringof, enEl);
        }
    }
    return res ~ "}\n";
}

unittest {
    import std.conv: asOriginalType;
    import cpt.cpt_neurons: SpSeed;
    import cpt.cpt_premises: SpBreed;
    enum CommonConcepts: DcpDescriptor {
        chat_seed = cd!(SpSeed, 2_500_739_441),                  // this is the root branch of the chat
        do_not_know_what_it_is = cd!(SpSeed, 580_052_493),
    }

    enum Chat: DcpDescriptor {
        console_breed = cd!(SpBreed, 4_021_308_401),
        console_seed = cd!(SpSeed, 1_771_384_341),
    }

    // Declare enums with the same members as in the CommonConcepts and Chat
    mixin(dequalify_enums!(CommonConcepts, Chat));
    assert(chat_seed == CommonConcepts.chat_seed);
    assert(do_not_know_what_it_is == CommonConcepts.do_not_know_what_it_is);
}

//---***---***---***---***---***--- types ---***---***---***---***---***---***

/// Info about static concept descriptor (it's all you need to call that function) and also this structure is used to
/// gather together name/cid pairs for dynamic concepts. Arrays of this structures will be stored as enums at compile
/// time for following processing them to fill in the holy and name maps.
struct DynDescriptor {
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
