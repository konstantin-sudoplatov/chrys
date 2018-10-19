module chri_shared;
import std.stdio;
import std.conv, std.format, std.concurrency;
import core.exception;

import proj_shared, proj_tools;

import chri_types;
import cpt.cpt_premises;

//---***---***---***---***---***--- data ---***---***---***---***---***--

//      Key threads of the project. The console thead will be spawned, but we don't need to remember its Tid. The circle
// knows it, it's enough.
shared const Tid _mainTid_;         /// Tid of the main thread
shared const Tid _attnDispTid_;     /// Attention dispatcher thread Tid

// Key shared data structures
shared SpiritMap _sm_;        /// The map of holy(stable and storrable and shared) concepts.
immutable string[Cid] _nm_;   /// name/seed map
debug {
    // set to true after the maps are filled in with names,cids and references to the concept objects
    immutable bool _maps_filled_;

    // set to true after the cranking is finished and the maps rehashed
    immutable bool _cranked_;
}

/// Concepts, that are not code-agnostic. They are used in static concept functions, for example, so code has to know
/// its cids (not names. The concept names still shouldn't be used in code).
/// 1373740169, 1354580365, 9082381, 584599776, 1722596122
enum HardCid: DcpDescriptor {
    /// In this buffer the attention circle thread puts user lines of text, where they wait to get processed. Used in
    /// the attention circle thread.
    userInputBuffer_hardcid_strqprem = cd!(SpStringQueuePremise, 1_079_824_511),

    /// This is the root branch of the attention circle. It is set up in the attention circle constructor. Used in the
    /// attention circle thread.
    chatBreed_hardcid_breed = cd!(SpBreed, 1_719_007_030),

    /// It is a very special and narrow case of concept. We have in it Tid of the thread, that maintains dialog with user.
    /// It is the thread that controls the console or http connection. The Tid is put in it on start of the chat caldron,
    /// and the primitive is valid only for chat branch, since the Tid field is stored in the live part of the concept.
    /// Used in the attention circle thread.
    userThread_hardcid_tidprem = cd!(SpTidPremise, 217_397_612),
}