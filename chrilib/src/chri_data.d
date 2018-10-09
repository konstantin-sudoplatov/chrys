module chri_data;

import project_params;

import chri_types;

//---***---***---***---***---***--- data ---***---***---***---***---***--

// Key shared data structures
shared string[Cid] _nm_;        /// name/seed map
shared SpiritMap _sm_;        /// The map of holy(stable and storrable and shared) concepts.
debug {
    // set to true after the maps are filled in with names,cids and references to the concept objects
    immutable bool _maps_filled_;

    // set to true after the cranking is finished and the maps rehashed
    immutable bool _cranked_;
}

//---***---***---***---***---***--- functions ---***---***---***---***---***---***

//---***---***---***---***---***--- types ---***---***---***---***---***---***


//###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
//
//                               Private
//
//###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

//---%%%---%%%---%%%---%%%---%%% data ---%%%---%%%---%%%---%%%---%%%---%%%

//---%%%---%%%---%%%---%%%---%%% functions ---%%%---%%%---%%%---%%%---%%%---%%%--

//---%%%---%%%---%%%---%%%---%%% types ---%%%---%%%---%%%---%%%---%%%---%%%--
