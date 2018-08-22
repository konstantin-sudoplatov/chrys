module chris;
import std.stdio;
import std.concurrency, core.thread;
import std.variant;

import global;
import messages;

// Show in console, that it is the unittest mode
version(unittest) {
	pragma(msg, "Unittest");
}

/**
	Main function of the project.
	Main initialization including creation of the key processes done in the global module constructor. Actually, the application
	started there. Here we wait for messages requiring termination and do it.
*/
void main()
{
	//while(true) {
	//	TerminateAppMsg termMsg;
	//	Throwable exp;
	//	Variant var;
	//	receive(
	//		(TerminateAppMsg m){termMsg = m;},
	//		(Throwable e){exp = e;},
	//		(Variant v) {var = v;}
	//	);
	//}

	thread_joinAll;
	writeln("good bye, world!");
}
