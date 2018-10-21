module stat.stat_types;

import proj_data;

import chri_types: StatCallType;

/**
        Get cid by static concept function name.
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

//---***---***---***---***---***--- data ---***---***---***---***---***--

//---***---***---***---***---***--- functions ---***---***---***---***---***--

//---***---***---***---***---***--- types ---***---***---***---***---***---***
