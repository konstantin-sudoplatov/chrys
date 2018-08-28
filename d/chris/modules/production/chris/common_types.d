module common_types;
import std.stdio;

import global, tools;
import attn.attn_circle_thread;
import cpt.holy;


//---***---***---***---***---***--- types ---***---***---***---***---***---***

/// Get cid by static concept (it' a function, remember!) name.
deprecated("You can always find cid in the concept object in the holy map.")
template stat_cid(alias cptName)
        if      // annotation consists of two elements and their types are int and StatCallType?
                (__traits(getAttributes, cptName).length == 2 &&
                is(typeof(__traits(getAttributes, cptName)[0]) == int) &&
                is(typeof(__traits(getAttributes, cptName)[1]) == StatCallType))
{   // extract the first element of annotation, which is cid
    enum int stat_cid = __traits(getAttributes, cptName)[0];
}

///
unittest {

    @(1, StatCallType.rCid_p0Cal_p1Cidar_p2Obj) static Cid fun(Caldron spaceName, Cid[] cid, Object extra) {
        return 0;
    }

    Cid cid = __traits(getAttributes, fun)[0];      // its cid
    assert(stat_cid!fun == cid);    // check cid
}

//---***---***---***---***---***--- data ---***---***---***---***---***--

/**
        Constructor
*/
//this(){}

//---***---***---***---***---***--- functions ---***---***---***---***---***--

//###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
//
//                               Private
//
//###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
private:
//---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
