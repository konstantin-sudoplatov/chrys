module chris;
import std.stdio;
import std.concurrency;

import project_params;

import chri_shared;
import stat.stat_registry;
import cpt.cpt_stat;
import atn.atn_dispatcher_thread;

void main()
{
	preloadConceptMaps(_sm_, _nm_);
}

/**
		Load the spirit and name maps with static concepts. Load name map with dynamic concepts names (dynamic concepts
	themselves will be loaded from DB into the spirit maps dynamically, when they are needed).
*/
void preloadConceptMaps(ref shared SpiritMap sm, ref immutable string[Cid] nm) {

    // Create and load spirit and name maps.
    sm = new shared SpiritMap;
    foreach(sd; createStatDescriptors) {
        sm.add(new SpStaticConcept(sd.cid, sd.fp, sd.call_type));
        cast()nm[sd.cid] = sd.name;
    }

    // Capture Tid of the main thread.
    cast()_mainTid_ = thisTid;

    // Spawn the attention dispatcher thread.
    cast()_attnDispTid_ = spawn(&attention_dispatcher_thread_func);

    spawn(&console_thread_func);
}