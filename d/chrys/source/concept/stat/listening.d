module concept.stat.listening;

import concept.dirs;

class Listening: Concept {
    int i = 1;
}

shared static this() {
    comCdir[StCpt.listening] = Route();
    comCdir[StCpt.listening].cpt = new Listening();
}
