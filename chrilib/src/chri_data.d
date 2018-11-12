module chri_data;
import std.stdio;
import std.conv, std.format, std.concurrency;
import core.exception;

import proj_data, proj_types;

import chri_types;
import cpt.cpt_premises, cpt.cpt_primitives, cpt.cpt_registry;

//---***---***---***---***---***--- data ---***---***---***---***---***--

/// Maximum number of fibers in the fiber pool
enum CALDRON_FIBER_POOL_SIZE = 30;

/// Maximum number of threads in the caldron thread pool
enum CALDRON_THREAD_POOL_SIZE = 50;

/// Call types of the static concept functions.
enum StatCallType: string {
    p0Cal = "void function(Caldron)",                              // void function(Caldron nameSpace)
    p0Calp1Cid = "void function(Caldron, Cid)",                         // void function(Caldron nameSpace, Cid operandCid)
    p0Calp1Cidp2Cid = "void function(Caldron, Cid, Cid)",                    // void function(Caldron nameSpace, Cid firstoperandCid, Cid secondOperandCid)
    p0Calp1Cidp2Cidp3Cid = "void function(Caldron, Cid, Cid, Cid)",               // void function(Caldron nameSpace, Cid firstoperandCid, Cid secondOperandCid, Cid thirdOperandCid)
    p0Calp1Cidp2Int = "void function(Caldron, Cid, int)",                    // void function(Caldron nameSpace, Cid conceptCid, int intValue)
    p0Calp1Cidp2Float = "void function(Caldron, Cid, float)",                  // void function(Caldron nameSpace, Cid conceptCid, float floatValue)
    p0Calp1Cidp2Cidp3Float = "void function(Caldron, Cid, Cid, float)",             // void function(Caldron nameSpace, Cid branchBreedCid, Cid conceptCid, float floatValue)
}

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

/// Fiber and thread pools for caldrons.
import atn.atn_caldron: CaldronFiberPool, CaldronThreadPool;
shared CaldronFiberPool _fiberPool_;
shared CaldronThreadPool _threadPool_;

/// Registry of serializable spirit classes. It's a two way associative array of TypeInfo_Class[Clid].
immutable CrossMap!(ClassInfo, Clid) _spReg_;

/// Concepts, that are not code-agnostic. They are used in static concept functions, for example, so code has to know
/// its cids (not names. The concept names still shouldn't be used in code).
/// , 1354580365, 9082381, 584599776, 1722596122
enum HardCid: DcpDsc {
    /// In this buffer the attention circle thread puts user lines of text, where they wait to get processed. Used in
    /// the attention circle thread.
    userInputBuffer_strqprem_hcid = cd!(SpStringQueuePrem, 1_079_824_511),

    /// This is the root branch of the attention circle. It is set up in the attention circle constructor. Used in the
    /// attention circle thread.
    chat_breed_hcid = cd!(SpBreed, 1_719_007_030),

    /// It is a very special and narrow case of concept. We have in it Tid of the thread, that maintains dialog with user.
    /// It is the thread that controls the console or http connection. The Tid is put in it on start of the chat caldron,
    /// and the primitive is valid only for chat branch, since the Tid field is stored in the live part of the concept.
    /// Used in the attention circle thread.
    userTid_tidprem_hcid = cd!(SpTidPrem, 217_397_612),

    /// This tid premise is injected to the called caldron on coming some messages like sendConceptToBranch_stat based on
    /// the Msg._senderTid field.
    callerTid_tidprem_hcid = cd!(SpTidPrem, 1_373_740_169),
}

shared static this(){
    CrossMap!(ClassInfo, Clid) spReg;
    foreach(i, sc; createSpiritClassesRegistry) {
        if(sc) spReg[cast(Clid)i] = sc;
    }
    _spReg_ = cast(immutable)spReg;

    _fiberPool_ = new shared CaldronFiberPool;
    _threadPool_ = new shared CaldronThreadPool;
}