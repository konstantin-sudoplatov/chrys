module concept.stat.listen;

import concept.routes;

class Listen {
    int i = 1;
}

shared static this() {
    comCDir[SCpt.listen] = cast(shared(void*))new Listen();
    import std.stdio;
    mixin("comCDir[SCpt.listen]".w);
}
