module concept.dirs;
public import tools;

/// Concept ID type.
alias Cid = int;

/// Base of the concept classes
class Concept {
} 

/// A route to a concept.
struct Route {
    Concept cpt;
}

/// Concept directory type: associative array of a concept route by concept IDs.
alias CoDir = Route[Cid];

/// Static concept IDs
enum StCpt: ushort {
    attention_bubble = 1,
    listening,
}

/// Common concept directory.
shared CoDir comCdir;

/// Categories of attention bubbles
enum AbCat {
    conversation = 1,
    conversationInitiatilization,
}

/// Attention bubble ID type.
alias Abid = uint;

/// Private concept directories: associative array of a private concept directory by attention bubble ID.
shared CoDir[Abid] privCdir;

