module stat.stat_types;

import project_params;

/// Call types of the static concept functions.
enum StatCallType: string {
    p0Cal = "void function(Caldron)",                              // void function(Caldron nameSpace)
    p0Calp1Cid = "void function(Caldron, Cid)",                         // void function(Caldron nameSpace, Cid operandCid)
    p0Calp1Cidp2Cid = "void function(Caldron, Cid, Cid)",                    // void function(Caldron nameSpace, Cid firstoperandCid, Cid secondOperandCid)
    p0Calp1Cidp2Int = "void function(Caldron, Cid, int)",                    // void function(Caldron nameSpace, Cid conceptCid, int intValue)
    p0Calp1Cidp2Float = "void function(Caldron, Cid, float)",                  // void function(Caldron nameSpace, Cid conceptCid, float floatValue)
    p0Calp1Cidp2Cidp3Float = "void function(Caldron, Cid, Cid, float)",             // void function(Caldron nameSpace, Cid branchBreedCid, Cid conceptCid, float floatValue)
}

/**
        Get cid by static concept (it' a function, remember!) name.
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
