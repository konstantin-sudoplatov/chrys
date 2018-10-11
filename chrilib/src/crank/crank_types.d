module crank.crank_types;
import std.format;

import project_params, tools;

import chri_shared;
import attn.attn_circle_thread;
import cpt.cpt_abstract, cpt.cpt_actions, cpt.cpt_premises, cpt.cpt_neurons;
import stat.stat_types;

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

/// Enum template for declaring named dynamic concepts. Used in the crank modules.
enum cd(T : SpiritDynamicConcept, Cid cid)  = DcpDescriptor(T.stringof, cid);

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

/// Structure of the crank enums.
struct DcpDescriptor {
    string className;      // named concept's class
    Cid cid;                // named concept's cid
}
