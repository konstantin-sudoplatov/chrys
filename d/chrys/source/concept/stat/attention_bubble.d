module concept.stat.attention_bubble;

import concept.dirs;

class AttentionBubble: Concept {
    AbCat abCat = AbCat.conversationInitiatilization; 
}

shared static this() {
    comCdir[StCpt.attention_bubble] = Route();
    comCdir[StCpt.attention_bubble].cpt = new AttentionBubble();
}
