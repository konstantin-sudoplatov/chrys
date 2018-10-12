module cpt.cpt_registry;
import std.stdio: writeln, writefln;
import std.traits, std.format;

import project_params, tools;

/// List of modules, containg concrete spirit concept clasess. The spirit concepts are annotated with class identifier (clid)
/// user defined attribute.
enum CptModules {
    actions = "cpt.cpt_actions",
    neurons = "cpt.cpt_neurons",
    premises = "cpt.cpt_premises",
    stat = "cpt.cpt_stat",
}

//---***---***---***---***---***--- functions ---***---***---***---***---***--

/**
        Gather TypeInfo_class for spirit concept classe in an array indexed by class identifiers (spirit classes are
        annotated with clids). Clids are restricted to positive numbers. CTFE.
    Returns: array of classinfos. Last used and unused clids are reported.
*/
TypeInfo_Class[] createSpiritClassesRegistry() {
    TypeInfo_Class[int] classMap;

    // Fill in the classMap, checking for repeating clids
    static foreach(moduleName; [EnumMembers!CptModules]) {
        mixin("import " ~ moduleName ~ ";");
        static foreach(member; __traits(allMembers, mixin(moduleName))) {
            static if // is member a class and
                    (mixin("is(" ~ member ~ " == class)") &&
                    // is the class annotated with one item and
                    __traits(getAttributes, mixin(member)).length == 1 &&
                    // is the type of the attribute int?
                    is(typeof(__traits(getAttributes, mixin(member))[0]) == int))
            {
                assert(__traits(getAttributes, mixin(member))[0] !in classMap,
                        format!"Clid %s, class %s: clid was already used."
                        (__traits(getAttributes, mixin(member))[0], member));
                classMap[__traits(getAttributes, mixin(member))[0]] = mixin(member ~ ".classinfo");
            }
        }
    }

    // Check the lowest clid
    import std.algorithm: min, max, reduce;
    auto tuple = classMap.byKey.reduce!(min, max);
    assert(tuple[0] >= 0, format!"Clids must be positive. Your lowest clid is %s"(tuple[0]));

    // Create array of classes with index as a clid
    TypeInfo_Class[] classArr;
    classArr.length = tuple[1] + 1;
    foreach(clid; classMap.byKey)
        classArr[clid] = classMap[clid];

    // Printout unused clids
    int[] unusedClids;
    foreach(i, cl; classArr)
        if(classArr[i] is null) unusedClids ~= cast(int)i;
    logit(format!"Max used clid: %s, unused clids: %s"(classArr.length-1, unusedClids));

    return classArr;
}

