module cpt.cpt_registry;
import std.stdio: writeln, writefln;
import std.traits, std.format;

/// List of modules, containg concrete spirit concept clasess. The spirit concepts are annotated with class identifier (clid)
/// user defined attribute.
enum CptModules {
    actions = "cpt.cpt_actions",
    neurons = "cpt.cpt_neurons",
    premises = "cpt.cpt_premises",
    stat = "cpt.cpt_stat",
}

// Import those modules. Unfortunately, this code seems to work after, not before the code, that uses these imports. Forced
// to import them manually.
static foreach(moduleName; [EnumMembers!CptModules]) {
    mixin("import " ~ moduleName ~ ";");
}

// Import those
//import cpt.cpt_actions;
//import cpt.cpt_neurons;
//import cpt.cpt_premises;
//import cpt.cpt_stat;

//---***---***---***---***---***--- functions ---***---***---***---***---***--

void aaa() {
    SpAction sa = new SpAction(0);
    pragma(msg, typeof(typeid(SpAction)));
    pragma(msg, typeof(SpAction.classinfo));
}

//---***---***---***---***---***--- types ---***---***---***---***---***---***

