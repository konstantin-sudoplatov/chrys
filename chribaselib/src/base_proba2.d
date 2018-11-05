module base_proba2;
import std.format;
import std.conv;
import std.stdio;
import std.concurrency;
import core.thread;


//unittest {
//    import core.time;
//    import std.datetime.stopwatch;
//
//    static void bar() {
//        //import std.array;
//        //import std.algorithm.mutation;
//        //enum sz = 100;
//        //static long[] ar1 = new long[sz];
//        //static long[] ar2 = new long[sz];
//        //static long n = sz*long.sizeof;
//
////        ar2[] = ar1[];  // 32 msec
////        (cast(byte[])ar2)[] = (cast(byte[])ar1)[];  // 32 msec
////        foreach(i; 0.. sz) ar2[i] = ar1[i]; // 160 msec
////        foreach(i; 0.. sz) *cast(long*)&ar2[i] = *cast(long*)&ar1[i];  // 160 msec
////        foreach(i; 0.. sz*long.sizeof) (cast(byte*)ar2.ptr)[i] = (cast(byte*)ar1.ptr)[i];  // 722 msec
////        copy(ar1, ar2);     // 37 msec
////        (cast(byte*)ar2)[0..n] = (cast(byte*)ar1)[0..n]; // 24 msec
////        (cast(long*)ar2)[0..sz] = (cast(long*)ar1)[0..sz]; // 24 msec - winner!
////        Thread.sleep(0.msecs);
//    }
//
//    StopWatch sw;
//    enum n = 100;
//    enum n2 = 1000;
//    Duration[n] times;
//    Duration last;
//    foreach (i; 0 .. n)
//    {
//        sw.start(); //start/resume mesuring.
//        foreach(unused; 0..n2) {
//            //Fiber f = new Fiber(&bar);
//            //f.reset;
//            //f.call;
//            spawn(&bar);
//            thread_joinAll;
//        }
//        sw.stop();  //stop/pause measuring.
//
//        //Return value of peek() after having stopped are the always same.
//        writeln((i + 1) * n2, " times done, lap time: ",
//        sw.peek().total!"msecs", "[ms]");
//        times[i] = sw.peek() - last;
//        last = sw.peek();
//    }
//    real sum = 0;
//    // To get the number of seconds,  use properties of Duration.total!(seconds) or (msecs, usecs, hnsecs)
//    foreach (t; times)
//       sum += t.total!"usecs";
//    writeln("Average time: ", sum/n/n2, " usecs");
//}
