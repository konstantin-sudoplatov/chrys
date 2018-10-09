module crank.crank_types;
import std.format;

import project_params, tools;

import chri_data;
import cpt.cpt_abstract, cpt.cpt_actions;

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
        Safe cast. It will throw an assertion if the object cannot be casted as opposed to silent returning null by the
    cast operator (for classes). Don't forget, the type you a trying to cast to must exist, i.e. be defined or imported.
    So, sometimes you will need to copy-paste this template in your module.
    Parameters:
        T = type to cast to
        o = object to cast
    Return: casted object or an assert happens if the object cannot be casted
*/
T scast(T, S)(S o)
    if ((is(T: Object) || is(T: shared Object) || is(T: immutable Object) || is(T == interface))
        && (is(S: Object) || is(S: shared Object) || is(S: immutable Object)))
{
    assert(cast(T)o, format!"Object %s cannot be casted to class(interface) %s"(typeid(o), T.stringof));
import std.stdio; writefln("file = %s, line = %s",__FILE__, __LINE__);
    return cast(T)o;
}

/// Enum template for declaring named dynamic concepts. Used in the crank modules.
enum cd(T : SpiritDynamicConcept, Cid cid)  = CptDescriptor(T.stringof, cid);

/**
        Retrieve a concept from the holy map by its enum constant and cast it from the HolyConcept to its original type,
    which is gotten from the enum constant (the CptDescriptor type). The scast template serves as a guard against silent
    casting an object to null, if the cast happens to be illegal.
    Parameters:
        cd = constant descriptor of the enum, describing the concept
    Returns: the wanted concept casted to its original type.
*/
auto cp(alias cd)() {
    return scast!(type!("shared " ~ cd.className))(_sm_[cd.cid]);
}

//---***---***---***---***---***--- types ---***---***---***---***---***---***

/// Structure of the crank enums.
struct CptDescriptor {
    string className;      // named concept's class
    Cid cid;                // named concept's cid
}
